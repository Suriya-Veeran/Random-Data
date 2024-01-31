package com.randomdata.demo.requestbean;

import lombok.*;

@Data
@Builder
@AllArgsConstructor(access =  AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class ColumnInfoBean {
    private String fieldName;
    private ColumnType columnType;
    private int blankPercentage;
    private String expression;
    private int reoccurrenceCount;
}
