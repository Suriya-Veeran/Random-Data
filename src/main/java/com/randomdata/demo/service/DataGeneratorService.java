package com.randomdata.demo.service;

import com.randomdata.demo.requestbean.DataGenerateRequestBean;

import java.io.IOException;
import java.util.List;

public interface DataGeneratorService {

    void generateRandomData(DataGenerateRequestBean dataGenerateRequestBean) throws IOException;

    List<String> generateColumnTypes();
}
