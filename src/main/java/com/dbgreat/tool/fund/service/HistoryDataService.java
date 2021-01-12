package com.dbgreat.tool.fund.service;

import com.dbgreat.tool.fund.entity.HistoryData;

import java.util.List;

public interface HistoryDataService {

    void saveHistory(HistoryData data);

    void updateHistory(HistoryData historyData);

    HistoryData getHistory(String date);

    List<HistoryData> getHistoryList();
}
