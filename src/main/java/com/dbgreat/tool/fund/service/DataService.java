package com.dbgreat.tool.fund.service;

import com.dbgreat.tool.fund.entity.ResponseData;
import com.dbgreat.tool.fund.entity.StockInfo;

public interface DataService {

    ResponseData getData();

    ResponseData getDataByHttp(boolean useCache);
}
