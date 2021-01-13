package com.dbgreat.tool.fund.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResponseData implements Serializable {
    private List<StockInfo> stockInfos;

    /**
     * 加权平均盈亏率
     */
    private Double weightPLPercent;

    /**
     * 官方发布
     */
    private HistoryData historyData;


    /**
     * 总持仓比例
     */
    private Double totalHoldingRate = 0.0;

    /**
     * 总加权盈亏率
     */
    private Double totalWeightPercent = 0.0;

    private String bjTime;


    private String usaTime;
}
