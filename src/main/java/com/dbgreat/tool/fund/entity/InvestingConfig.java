package com.dbgreat.tool.fund.entity;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@ConfigurationProperties(prefix = "ten-stock.investing")
@Configuration
@Data
public class InvestingConfig {


    /**
     * 持仓列表
     */
    private List<HoldingConfig> list = new ArrayList<>();

    public InvestingConfig(List<HoldingConfig> channelConfigurations) {
        super();
        this.list = channelConfigurations;
    }

    public InvestingConfig() {
        super();
    }


    @Data
    public static class HoldingConfig {

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

        private Boolean isUSA;

        private String dataSource;

    }

}

