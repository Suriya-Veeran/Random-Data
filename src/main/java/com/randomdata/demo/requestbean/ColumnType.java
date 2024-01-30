package com.randomdata.demo.requestbean;

import lombok.*;


@Getter
public enum ColumnType {
    SUPERHERO("SUPERHERO"),
    ADDRESS("ADDRESS"),
    BOOK("BOOK"),
    MONEY("MONEY"),
    COLOR("COLOR");

    private final String name;

    ColumnType(String name){
        this.name = name;
    }


}
