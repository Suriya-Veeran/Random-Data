package com.randomdata.demo.service;

import com.github.javafaker.Faker;

import com.randomdata.demo.requestbean.ColumnInfoBean;
import com.randomdata.demo.requestbean.ColumnType;
import com.randomdata.demo.requestbean.DataGenerateRequestBean;
import com.randomdata.demo.utility.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class DataGeneratorServiceImpl implements DataGeneratorService {

    public static final String outputPath = "/home/p3/IdeaProjects/demo/Files";

    @Override
    public void generateRandomData(DataGenerateRequestBean dataGenerateRequestBean) throws IOException {
        File file = createOutputFile(outputPath, dataGenerateRequestBean);
        try(FileInputStream fileInputStream = new FileInputStream(file);Workbook workbook = new XSSFWorkbook(fileInputStream)){
            Sheet data = workbook.createSheet("MOCK_DATA");
            if(Boolean.TRUE.equals(dataGenerateRequestBean.isIncludeHeader())){
                generateHeaders(data,dataGenerateRequestBean.getColumnInfo());
            }
            generateColumnTypeData(data,dataGenerateRequestBean.getColumnInfo(),dataGenerateRequestBean.getRows());

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
        }
    }

    private void generateColumnTypeData(Sheet data, List<ColumnInfoBean> columnInfo, int rows) {
        Faker faker = new Faker();
        int startingIndex=1;
        for (ColumnInfoBean columnInfoBean : columnInfo) {
            ColumnType columnType = columnInfoBean.getColumnType();
            if(ColumnType.SUPERHERO.getName().equals(columnType.getName())){
                String content = invokeColumnFakerDefaultMethod(faker);
                writeRandomData(data,startingIndex,rows, content);
            }
        }
    }

    private String invokeColumnFakerDefaultMethod(Faker faker) {
        return faker.superhero().name();
    }

    private void writeRandomData(Sheet data, int startingIndex, int rows, String content) {
        for(int i = startingIndex;i<=rows;i++){
            data.createRow(startingIndex);
            writeCell(data,startingIndex, content);
            startingIndex++;
        }
    }

    private void writeCell(Sheet data, int startingIndex, String content) {
        int cell = 0;
        data.getRow(startingIndex).createCell(cell).setCellValue(content);
    }

    private File createOutputFile(String outputPath, DataGenerateRequestBean dataGenerateRequestBean) throws IOException {
        String filename = outputPath+ '/' +"file"+".xlsx";
        FileUtils.checkCreateDirectory(outputPath);
        File file = FileUtils.createFile(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try(Workbook workbook = new XSSFWorkbook()){
            workbook.write(fileOutputStream);
        }
        fileOutputStream.close();
        return file;
    }

    private void generateHeaders(Sheet sheet, List<ColumnInfoBean> columnInfo) {
        String[] headers = retrieveHeaders(columnInfo);
        sheet.createRow(0);
        int cell = 0;
        for (String header : headers) {
            sheet.getRow(0).createCell(cell).setCellValue(header);
            cell++;
        }
    }

    private String[] retrieveHeaders(List<ColumnInfoBean> columnInfo) {
        int i = 0;
        String[] headers = new String[columnInfo.size()];
        for (ColumnInfoBean columnInfoBean : columnInfo) {
             headers[i++]= columnInfoBean.getFieldName();
        }
        return headers;
    }
}
