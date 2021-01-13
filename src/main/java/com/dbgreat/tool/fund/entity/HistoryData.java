package com.dbgreat.tool.fund.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "HistoryData")
@Data
public class HistoryData {
    @Id
    private String id;

    @Indexed
    private String date;

    /**
     * 官网发布的涨跌比率(%)
     */
    private Double officialChangePercent;



    /**
     * 根据十大持仓估算的涨跌比例
     */
    private Double estimateChangePercent;
}
