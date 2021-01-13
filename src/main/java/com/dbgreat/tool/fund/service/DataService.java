package com.dbgreat.tool.fund.service;

import com.dbgreat.tool.fund.entity.HistoryData;
import com.dbgreat.tool.fund.entity.ResponseData;

import java.util.List;

public interface DataService {

    ResponseData getData();

    ResponseData getDataByHttp(boolean useCache);

    HistoryData getOfficialData();

}
