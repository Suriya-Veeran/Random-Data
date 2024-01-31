package com.randomdata.demo.enums;

import lombok.Getter;

@Getter
public enum ExportFormat {
    EXCEL("EXCEL"),
    TXT("TXT"),
    CSV("CSV"),
    JSON("JSON");


    private final String name;
    ExportFormat(String name){
        this.name = name;
    }

}
