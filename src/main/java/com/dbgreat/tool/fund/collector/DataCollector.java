package com.dbgreat.tool.fund.collector;

import com.dbgreat.tool.fund.entity.StockInfo;

/**
 * @author : xiongw@glodon.com
 * @date : 2021/1/9 18:23
 */
public interface DataCollector {

    boolean support(String dataSource);

    StockInfo collectData(String code);
}
