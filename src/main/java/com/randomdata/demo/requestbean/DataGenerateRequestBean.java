package com.randomdata.demo.requestbean;


import com.randomdata.demo.enums.ExportFormat;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(access =  AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class DataGenerateRequestBean {
    private List<ColumnInfoBean> columnInfo;
    private int rows;
    private ExportFormat format;
    private boolean includeHeader;
}
