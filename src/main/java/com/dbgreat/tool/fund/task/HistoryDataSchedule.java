package com.dbgreat.tool.fund.task;

import com.alibaba.fastjson.JSON;
import com.dbgreat.tool.fund.entity.HistoryData;
import com.dbgreat.tool.fund.entity.ResponseData;
import com.dbgreat.tool.fund.service.DataService;
import com.dbgreat.tool.fund.service.HistoryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
public class HistoryDataSchedule {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Autowired
    private DataService dataService;
    @Autowired
    private HistoryDataService hisDataService;

    //周一到周五，UTC时间22点(美东时间17点)保存最终估算值
    @Scheduled(cron = "0 0 22 ? * MON-FRI")
    public void saveHistory() {
        String localDate = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        log.info(localDate);
        ResponseData data = dataService.getData();
        HistoryData historyData = new HistoryData();
        historyData.setDate(localDate);
        historyData.setEstimateChangePercent(data.getWeightPLPercent());
        log.info("save historyData:{}", JSON.toJSONString(historyData));
        hisDataService.saveHistory(historyData);
    }

    //周一到周五，UTC时间2点(美东时间晚九点)保存官网发布的前一日数据
    @Scheduled(cron = "0 0 2 ? * MON-FRI")
    public void updateHistory() {
        HistoryData historyData = dataService.getOfficialData();
        log.info("update histData is:{}", JSON.toJSONString(historyData));
        hisDataService.updateHistory(historyData);
    }
}
