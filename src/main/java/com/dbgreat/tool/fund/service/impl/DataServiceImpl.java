package com.dbgreat.tool.fund.service.impl;

import com.dbgreat.tool.fund.collector.DataCollector;
import com.dbgreat.tool.fund.entity.HistoryData;
import com.dbgreat.tool.fund.entity.InvestingConfig;
import com.dbgreat.tool.fund.entity.ResponseData;
import com.dbgreat.tool.fund.entity.StockInfo;
import com.dbgreat.tool.fund.service.DataService;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 英为财情解析
 */
@Service
@Slf4j
public class DataServiceImpl implements DataService {
    @Autowired
    private InvestingConfig investingConfig;

    @Autowired
    List<DataCollector> dataCollectors;

    private ResponseData data;

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    ZoneId bjZone = ZoneId.of("Asia/Shanghai");
    ZoneId nyZone = ZoneId.of("America/New_York");

    @Override
    public ResponseData getData() {
        List<StockInfo> list = new ArrayList<>();
        ResponseData data = new ResponseData();
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime bjTime = utcTime.withZoneSameInstant(bjZone);
        ZonedDateTime nyTime = utcTime.withZoneSameInstant(nyZone);
        String utcTimeStr = utcTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        String bjTimeStr = bjTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        String nyTimeStr = nyTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        data.setBjTime(bjTimeStr);
        data.setUsaTime(nyTimeStr);

        log.info("UTC时间：{},北京时间：{},美国东部时间:{}", utcTimeStr, bjTimeStr, nyTimeStr);

        investingConfig.getList().forEach(cfg -> {
            StockInfo realInfo = null;
            for (DataCollector collector : dataCollectors) {
                if (collector.support(cfg.getDataSource())) {
                    realInfo = collector.collectData(cfg.getCode());
                }
            }
            if (null != realInfo) {
                realInfo.setCode(cfg.getCode());
                realInfo.setName(cfg.getName());

                //持仓比例
                realInfo.setHoldingRate(cfg.getHoldingRate());

                //加权平均盈亏率
                if (!cfg.getIsUSA() || showUSAStock(nyTime)) {
                    realInfo.setWeightPercent(BigDecimal.valueOf(cfg.getHoldingRate()).multiply(BigDecimal.valueOf(realInfo.getChangePercent())).doubleValue());
                } else {
                    //美股非开盘时间特殊处理
                    realInfo.setChangePercent(0.0);
                    realInfo.setWeightPercent(0.0);
                }

                list.add(realInfo);
                if (!cfg.getIsUSA() || showUSAStock(nyTime)) {
                    data.setTotalHoldingRate(data.getTotalHoldingRate() + cfg.getHoldingRate());
                    data.setTotalWeightPercent(data.getTotalWeightPercent() + realInfo.getWeightPercent());
                }
            }
        });
        data.setStockInfos(list);
        data.setWeightPLPercent(BigDecimal.valueOf(data.getTotalWeightPercent()).divide(BigDecimal.valueOf(data.getTotalHoldingRate()), 2, RoundingMode.HALF_UP).doubleValue());
        this.data = data;
        return data;
    }

    @Override
    public ResponseData getDataByHttp(boolean useCache) {
        if (null != this.data && useCache) {
            return this.data;
        }
        return getData();
    }

    @Override
    public HistoryData getOfficialData() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://am.jpmorgan.com/FundsMarketingHandler/product-data?cusip=HK0000055761&country=tw&role=per&language=zh&userLoggedIn=false&version=3.37.6_1605863001")
                .method("GET", null)
                .addHeader("Cookie", "ppwaf_3736=!7XTPTX846XAVyDXvrke1bL2Q1n612pcCQeVMYEPhVhk9ZzS+vI+JFwAgpPiODm4zdmD/tnk7Mv63k/M=; TS01e46c65=013dd7d8c4fba66650306b48160878e73bef06a9cbf8ee9da7b499ae8594dc16379a63efdabc4805b77e78a786d72df0d4ebf41f71; ppnet_3736=!WXVSk9a3+1KGfq8avHeFWqdoRKuC4CTxgS5e1L86qhlhAnKcG0r3jVcprgX7bwBukHNKLV+E16zzq6k=; userGeo=ROW")
                .build();
        try {
            String body = Objects.requireNonNull(client.newCall(request).execute().body()).string();
            if (!StringUtils.isEmpty(body)) {
                Double changePercent = JsonPath.read(body, "fundData.shareClass.nav.changePercentage");
                String date = JsonPath.read(body, "fundData.shareClass.nav.date");
                //Double change = JsonPath.read(body, "fundData.shareClass.nav.changeAmount");

                HistoryData data = new HistoryData();
                if (!StringUtils.isEmpty(date)) {
                    data.setDate(date);
                }

                if (null != changePercent) {
                    data.setOfficialChangePercent(changePercent);
                }
                return data;
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return null;
    }


    /**
     * 是否显示美投
     *
     * @param usaTime 美东(西五区)时间
     * @return
     */
    private boolean showUSAStock(ZonedDateTime usaTime) {
        LocalDateTime localDateTime = usaTime.toLocalDateTime();
        LocalDateTime startTime = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 9, 30);
        LocalDateTime endTime = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 20, 0);//北京时间开盘前都显示
        return localDateTime.isAfter(startTime) && localDateTime.isBefore(endTime);
    }
}
