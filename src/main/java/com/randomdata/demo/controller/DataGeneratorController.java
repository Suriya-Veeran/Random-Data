package com.randomdata.demo.controller;

import com.randomdata.demo.requestbean.DataGenerateRequestBean;
import com.randomdata.demo.service.DataGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController("data-generatorController")
@RequestMapping(value = "/data-generator", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DataGeneratorController {

    @Autowired
    private DataGeneratorService dataGeneratorService;

    @PostMapping("/data/random")
    public void randomData(@RequestBody DataGenerateRequestBean dataGenerateRequestBean) throws IOException {
        dataGeneratorService.generateRandomData(dataGenerateRequestBean);
    }
}
