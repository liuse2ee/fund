package com.dbgreat.tool.fund.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class StockInfo implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 股票编码
     */
    private String code;


    /**
     * 持股比例
     */
    private Double holdingRate;


    /**
     * 浮动盈亏
     */
    private Double change = 0.0;


    /**
     * 浮动盈亏率（%）
     */
    private Double changePercent = 0.0;

    /**
     * 加权平均盈亏率(%)
     */
    private Double weightPercent = 0.0;

    private String lastUpdateTime;

    public StockInfo(String change, String changePercent, String lastUpdateTime) {
        this.change = Double.valueOf(change);
        this.changePercent = Double.valueOf(changePercent);
        this.lastUpdateTime = lastUpdateTime;
    }

}
