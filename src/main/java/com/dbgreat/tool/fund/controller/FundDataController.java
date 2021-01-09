package com.dbgreat.tool.fund.controller;

import com.dbgreat.tool.fund.entity.ResponseData;
import com.dbgreat.tool.fund.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FundDataController {


    @Autowired
    private DataService service;

    @GetMapping("/fund/data")
    public ResponseData getData(@RequestParam(required = false, defaultValue = "true") boolean useCache) {
        return service.getDataByHttp(useCache);
    }
}
