package com.randomdata.demo.requestbean;

import lombok.*;


@Getter
public enum ColumnType {

    SUPERHERO_NAME("SUPERHERO NAME"),
    SUPERHERO_POWER("SUPERHERO POWER"),
    STREET_NAME("STREET NAME"),
    STREET_ADDRESS_NUMBER("STREET ADDRESS NUMBER"),
    STREET_ADDRESS("STREET ADDRESS"),
    ZIP_CODE("ZIP CODE"),
    CITY("CITY"),
    LATITUDE("LATITUDE"),
    LONGITUDE("LONGITUDE"),
    TIME_ZONE("TIME ZONE"),
    COUNTRY("COUNTRY"),
    COUNTRY_CODE("COUNTRY CODE"),
    COMPANY_NAME("COMPANY NAME"),
    COMPANY_INDUSTRY("COMPANY INDUSTRY"),
    COMPANY_PROFESSION("COMPANY PROFESSION")
    ;

    private final String name;

    ColumnType(String name){
        this.name = name;
    }


}
