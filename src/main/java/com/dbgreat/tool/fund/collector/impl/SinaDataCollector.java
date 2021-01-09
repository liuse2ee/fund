package com.dbgreat.tool.fund.collector.impl;

import com.dbgreat.tool.fund.collector.DataCollector;
import com.dbgreat.tool.fund.entity.StockInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author : xiongw@glodon.com
 * @date : 2021/1/9 18:27
 */
@Service
@Slf4j
public class SinaDataCollector implements DataCollector {

    private static final String SINA = "sina";
    @Override
    public StockInfo collectData(String code) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(String.format("http://hq.sinajs.cn/list=%s", code))
                .method("GET", null)
                .build();
        try {
            String body = Objects.requireNonNull(client.newCall(request).execute().body()).string();
            if (!StringUtils.isEmpty(body)) {
                return parseString(body);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean support(String dataSource) {
        return SINA.equalsIgnoreCase(dataSource);
    }

    //解析字符串
    private StockInfo parseString(String str) {
        log.info(str);
        String[] dataArr = str.split(",");
        String change = dataArr[4];
        String changePercent = dataArr[2];
        String lastUpdateTime = dataArr[3];
        log.info("change: {},changePercent: {},lastUpdateTime:{}", change, changePercent, lastUpdateTime);
        return new StockInfo(change, changePercent, lastUpdateTime);
    }
}
