package com.dbgreat.tool.fund.controller;

import com.dbgreat.tool.fund.entity.HistoryData;
import com.dbgreat.tool.fund.entity.ResponseData;
import com.dbgreat.tool.fund.service.DataService;
import com.dbgreat.tool.fund.service.HistoryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
public class FundDataController {


    @Autowired
    private DataService service;

    @Autowired
    HistoryDataService historyDataService;

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @GetMapping("/fund/data")
    public ResponseData getData(@RequestParam(required = false, defaultValue = "true") boolean useCache) {
        ResponseData data = service.getDataByHttp(useCache);
        HistoryData historyData = historyDataService.getHistory(LocalDate.now(ZoneOffset.UTC).plusDays(-1).format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        data.setHistoryData(historyData);
        return data;
    }


    @PutMapping("/fund/historyData")
    public HistoryData saveHistory() {
        HistoryData historyData = service.getOfficialData();
        historyDataService.updateHistory(historyData);
        return historyData;
    }

    @GetMapping("/fund/historyDataList")
    public List<HistoryData> getHistoryDataList() {
        return historyDataService.getHistoryList();
    }

}
