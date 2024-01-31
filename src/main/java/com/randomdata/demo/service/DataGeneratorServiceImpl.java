package com.randomdata.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.javafaker.Faker;

import com.randomdata.demo.enums.ExportFormat;
import com.randomdata.demo.requestbean.ColumnInfoBean;
import com.randomdata.demo.requestbean.ColumnType;
import com.randomdata.demo.requestbean.DataGenerateRequestBean;
import com.randomdata.demo.utility.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.jexl3.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class DataGeneratorServiceImpl implements DataGeneratorService {

    @Value("${output.path}")
    public String outputPath;
    public static final String XLSX = ".xlsx";
    public static final String JSON = ".json";
    public static final String CSV = ".csv";
    public static final String TXT = ".txt";

    @Override
    public void generateRandomData(DataGenerateRequestBean dataGenerateRequestBean) throws IOException {

        File file = createOutputFile(outputPath, dataGenerateRequestBean.getFormat());
        switch (dataGenerateRequestBean.getFormat()) {
            case TXT:
                generateTxtData(dataGenerateRequestBean, file);
                break;
            case JSON:
                generateJsonData(dataGenerateRequestBean, file);
                break;
            case EXCEL:
                generateExcelData(dataGenerateRequestBean, file);
                break;
            case CSV:
                generateCsvData(dataGenerateRequestBean, file);
                break;
            default:
                break;
        }
    }

    private void generateTxtData(DataGenerateRequestBean dataGenerateRequestBean,
                                 File file) throws IOException {
        int startingIndex = dataGenerateRequestBean.isIncludeHeader() ? 1 : 0;
        List<Map<String, String>> generateDataList = generateDataList(dataGenerateRequestBean, startingIndex);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (Boolean.TRUE.equals(dataGenerateRequestBean.isIncludeHeader())) {
                writer.write(dataGenerateRequestBean.getColumnInfo().stream().map(ColumnInfoBean::getFieldName).collect(Collectors.joining(";")));
                writer.newLine();
            }
            for (Map<String, String> row : generateDataList) {
                writer.write(String.join(";", row.values()));
                writer.newLine();
            }
        }
    }

    private void generateJsonData(DataGenerateRequestBean dataGenerateRequestBean,
                                  File file) throws IOException {
        int startingIndex = dataGenerateRequestBean.isIncludeHeader() ? 1 : 0;
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        List<Map<String, String>> generateDataList = generateDataList(dataGenerateRequestBean, startingIndex);

        Map<String, Object> jsonObject = new HashMap<>();
        if (Boolean.TRUE.equals(dataGenerateRequestBean.isIncludeHeader())) {
            List<String> list = new ArrayList<>();
            for (ColumnInfoBean columnInfoBean : dataGenerateRequestBean.getColumnInfo()) {
                String fieldName = columnInfoBean.getFieldName();
                list.add(fieldName);
            }
            jsonObject.put("headers", list);
        }
        jsonObject.put("data", generateDataList);
        String writtenValueAsString = objectMapper.writeValueAsString(generateDataList);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(writtenValueAsString);
        }
    }

    private void generateExcelData(DataGenerateRequestBean dataGenerateRequestBean,
                                   File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet data = workbook.createSheet("MOCK_DATA");
            if (Boolean.TRUE.equals(dataGenerateRequestBean.isIncludeHeader())) {
                generateHeaders(data, dataGenerateRequestBean.getColumnInfo());
            }
            generateColumnTypeData(data, dataGenerateRequestBean, dataGenerateRequestBean.getRows());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
        }
    }

    private void generateCsvData(DataGenerateRequestBean dataGenerateRequestBean,
                                 File file) throws IOException {
        int startingIndex = dataGenerateRequestBean.isIncludeHeader() ? 1 : 0;
        List<Map<String, String>> dataList = generateDataList(dataGenerateRequestBean, startingIndex);
        CSVFormat csvFormat;
        if (dataGenerateRequestBean.isIncludeHeader()) {
            csvFormat = CSVFormat.DEFAULT.withHeader(dataGenerateRequestBean.getColumnInfo().stream()
                    .map(ColumnInfoBean::getFieldName)
                    .toArray(String[]::new)).withAutoFlush(true);
        } else {
            csvFormat = CSVFormat.DEFAULT.withAutoFlush(true);
        }
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(file), csvFormat)) {
            for (Map<String, String> row : dataList) {
                csvPrinter.printRecord(row.values());
            }
        }
    }

    private String generateColumnData(ColumnInfoBean columnInfoBean,
                                      Faker faker) {
        if (columnInfoBean.getExpression() != null) {
            return evaluateExpression(columnInfoBean.getExpression(), faker);
        }
        return generateFakerData(columnInfoBean, faker);
    }

    private List<Map<String, String>> generateDataList(DataGenerateRequestBean dataGenerateRequestBean,
                                                       int startingIndex) {
        Faker faker = new Faker();
        List<Map<String, String>> dataList = new ArrayList<>();
        for (int i = startingIndex; i <= dataGenerateRequestBean.getRows(); i++) {
            Map<String, String> rowData = new LinkedHashMap<>();
            for (ColumnInfoBean columnInfoBean : dataGenerateRequestBean.getColumnInfo()) {
                String content = generateColumnData(columnInfoBean, faker);
                rowData.put(columnInfoBean.getFieldName(), content);
            }
            dataList.add(rowData);
        }
        return dataList;
    }

    @Override
    public List<String> generateColumnTypes() {
        List<String> columnTypeList = new ArrayList<>();
        for (ColumnType value : ColumnType.values()) {
            columnTypeList.add(value.getName());
        }

        return columnTypeList;
    }

    private void generateColumnTypeData(Sheet data,
                                        DataGenerateRequestBean dataGenerateRequestBean,
                                        int rows) {
        Faker faker = new Faker();
        int startingIndex = dataGenerateRequestBean.isIncludeHeader() ? 1 : 0;
        Sheet sheet = createActiveCell(data, startingIndex, rows, dataGenerateRequestBean.getColumnInfo().size());
        writeRandomValues(sheet, dataGenerateRequestBean, faker, startingIndex);
    }

    private void writeRandomValues(Sheet sheet,
                                   DataGenerateRequestBean dataGenerateRequestBean,
                                   Faker faker,
                                   int startingIndex) {

        for (int i = startingIndex; i <= dataGenerateRequestBean.getRows(); i++) {
            int cell = 0;
            for (ColumnInfoBean columnInfoBean : dataGenerateRequestBean.getColumnInfo()) {
                String content = invokeColumnFakerDefaultMethod(faker, columnInfoBean);
                setCellValue(sheet, i, cell++, content);
            }
        }
    }

    private void setCellValue(Sheet sheet,
                              int rowValue,
                              int cellValue,
                              String content) {
        sheet.getRow(rowValue).getCell(cellValue).setCellValue(content == null ? "" : content);
    }

    private Sheet createActiveCell(Sheet data,
                                   int startingIndex,
                                   int rowIndex,
                                   int cellIndex) {
        for (int i = startingIndex; i <= rowIndex; i++) {
            Row row = data.createRow(i);
            for (int j = 0; j <= cellIndex; j++) {
                row.createCell(j);
            }
        }
        log.info("rows : " + data.getPhysicalNumberOfRows());
        return data;
    }

    private String invokeColumnFakerDefaultMethod(Faker faker,
                                                  ColumnInfoBean columnInfoBean) {
        return generateFakerData(columnInfoBean, faker);
    }

    private String generateFakerData(ColumnInfoBean columnInfoBean,
                                     Faker faker) {
        if (columnInfoBean.getExpression() != null) {
            return evaluateExpression(columnInfoBean.getExpression(), faker);
        }

        return switch (columnInfoBean.getColumnType()) {
            case SUPERHERO_NAME -> faker.superhero().name();
            case SUPERHERO_POWER -> faker.superhero().power();
            case CITY -> faker.address().city();
            case COUNTRY -> faker.address().country();
            case LATITUDE -> faker.address().latitude();
            case LONGITUDE -> faker.address().longitude();
            case ZIP_CODE -> faker.address().zipCode();
            default -> "";
        };
    }

    private File createOutputFile(String outputPath,
                                  ExportFormat format) throws IOException {

        String filename = outputPath + File.separator + "file" + "-" + format.getName() + new Date().getTime() + retrieveFormat(format);
        FileUtils.checkCreateDirectory(outputPath);
        File file = FileUtils.createFile(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try (Workbook workbook = new XSSFWorkbook()) {
            workbook.write(fileOutputStream);
        }
        fileOutputStream.close();
        return file;
    }

    private String retrieveFormat(ExportFormat format) {
        return switch (format) {
            case CSV -> CSV;
            case EXCEL -> XLSX;
            case JSON -> JSON;
            case TXT -> TXT;
        };
    }

    private void generateHeaders(Sheet sheet,
                                 List<ColumnInfoBean> columnInfo) {
        String[] headers = retrieveHeaders(columnInfo);
        int cell = 0;
        Row row = sheet.createRow(0);
        for (String header : headers) {
            row.createCell(cell).setCellValue(header);
            cell++;
        }
    }

    private String[] retrieveHeaders(List<ColumnInfoBean> columnInfo) {
        int i = 0;
        String[] headers = new String[columnInfo.size()];
        for (ColumnInfoBean columnInfoBean : columnInfo) {
            headers[i++] = columnInfoBean.getFieldName();
        }
        return headers;
    }

    public String evaluateExpression(String expression,
                                     Faker faker) {
        JexlEngine jexlEngine = new JexlBuilder().create();
        JexlExpression jexlExpression = jexlEngine.createExpression(expression);
        JexlContext context = new MapContext();
        context.set("faker", faker);
        Object evaluate = jexlExpression.evaluate(context);
        return evaluate != null ? expression : "";
    }
}
