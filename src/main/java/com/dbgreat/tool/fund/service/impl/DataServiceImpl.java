package com.dbgreat.tool.fund.service.impl;

import com.dbgreat.tool.fund.collector.DataCollector;
import com.dbgreat.tool.fund.entity.InvestingConfig;
import com.dbgreat.tool.fund.entity.ResponseData;
import com.dbgreat.tool.fund.entity.StockInfo;
import com.dbgreat.tool.fund.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


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
