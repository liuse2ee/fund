package com.dbgreat.tool.fund.collector.impl;

import com.dbgreat.tool.fund.collector.DataCollector;
import com.dbgreat.tool.fund.entity.StockInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author : xiongw@glodon.com
 * @date : 2021/1/9 18:28
 */
@Service
@Slf4j
public class InvestingCollector implements DataCollector {

    ZoneId bjZone = ZoneId.of("Asia/Shanghai");
    ZoneId nyZone = ZoneId.of("America/New_York");
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String INVESTING = "investing";

    @Override
    public boolean support(String dataSource) {
        return INVESTING.equalsIgnoreCase(dataSource);
    }

    @Override
    public StockInfo collectData(String code) {
        Long fundId = Long.parseLong(code);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(String.format("https://cn.investing.com/common/modules/js_instrument_chart/api/data.php?pair_id=%d&pair_id_for_news=%d&chart_type=area&pair_interval=86400&candle_count=120&events=yes&volume_series=yes", fundId, fundId))
                .method("GET", null)
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
                .addHeader("Referer", "https://cn.investing.com/equities/samsung-electronics-co-ltd")
                .addHeader("Cookie", "PHPSESSID=vscdf2gbtqms7spfjs2949fa0e; geoC=CN; adBlockerNewUserDomains=1608696017; StickySession=id.75373387854.171cn.investing.com; _ga=GA1.2.688258911.1608696019; _gid=GA1.2.139854748.1608696019; Hm_lvt_a1e3d50107c2a0e021d734fe76f85914=1608536820,1608537147,1608539917,1608696019; adsFreeSalePopUp=3; logglytrackingsession=1d1063b6-1011-4df0-ab2b-ca8f4f2f36be; _gat=1; _gat_allSitesTracker=1; outbrain_cid_fetch=true; SideBlockUser=a%3A2%3A%7Bs%3A10%3A%22stack_size%22%3Ba%3A1%3A%7Bs%3A11%3A%22last_quotes%22%3Bi%3A8%3B%7Ds%3A6%3A%22stacks%22%3Ba%3A1%3A%7Bs%3A11%3A%22last_quotes%22%3Ba%3A4%3A%7Bi%3A0%3Ba%3A3%3A%7Bs%3A7%3A%22pair_ID%22%3Bi%3A1065446%3Bs%3A10%3A%22pair_title%22%3Bs%3A0%3A%22%22%3Bs%3A9%3A%22pair_link%22%3Bs%3A38%3A%22%2Fequities%2Fsamsung-elec-gds%3Fcid%3D1065446%22%3B%7Di%3A1%3Ba%3A3%3A%7Bs%3A7%3A%22pair_ID%22%3Bs%3A5%3A%2243433%22%3Bs%3A10%3A%22pair_title%22%3Bs%3A0%3A%22%22%3Bs%3A9%3A%22pair_link%22%3Bs%3A36%3A%22%2Fequities%2Fsamsung-electronics-co-ltd%22%3B%7Di%3A2%3Ba%3A3%3A%7Bs%3A7%3A%22pair_ID%22%3Bs%3A6%3A%22100705%22%3Bs%3A10%3A%22pair_title%22%3Bs%3A0%3A%22%22%3Bs%3A9%3A%22pair_link%22%3Bs%3A22%3A%22%2Fequities%2Fhs-laobaigan%22%3B%7Di%3A3%3Ba%3A3%3A%7Bs%3A7%3A%22pair_ID%22%3Bi%3A48443%3Bs%3A10%3A%22pair_title%22%3Bs%3A0%3A%22%22%3Bs%3A9%3A%22pair_link%22%3Bs%3A50%3A%22%2Fequities%2Fsamsung-electronics-co-ltd-gdr%3Fcid%3D48443%22%3B%7D%7D%7D%7D; nyxDorf=NjA2Y24mMWwyZWtlM342NTFgMm9heDE6MDk%3D; OptanonConsent=isIABGlobal=false&datestamp=Wed+Dec+23+2020+13%3A22%3A28+GMT%2B0800+(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)&version=6.7.0&hosts=&landingPath=NotLandingPage&groups=C0001%3A1%2CC0002%3A1%2CC0003%3A1%2CC0004%3A1&AwaitingReconsent=false&geolocation=CN%3BBJ; OptanonAlertBoxClosed=2020-12-23T05:22:28.917Z; Hm_lpvt_a1e3d50107c2a0e021d734fe76f85914=1608700951; StickySession=id.71095185635.147cn.investing.com; PHPSESSID=r2bicfdm1p2n1pueqiosd0a7f5; geoC=CN; adsFreeSalePopUp=1; adBlockerNewUserDomains=1609562694; udid=948759d9c8a6bf486c3e0afb6bcb4f28; smd=948759d9c8a6bf486c3e0afb6bcb4f28-1609571219.480; firstUdid=0")
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

    //解析字符串
    private StockInfo parseString(String str) {
        log.info(str);
        String content = StringEscapeUtils.unescapeJava(str);
        Document doc = Jsoup.parse(content);
        String change = doc.getElementById("chart-info-change").text();
        String changePercent = doc.getElementById("chart-info-change-percent").text();
        String lastUpdateTime = null;
        Elements spans = doc.getElementsByTag("span");
        if (!CollectionUtils.isEmpty(spans)) {
            long timeSpan = Long.parseLong(spans.get(spans.size() - 1).text());
            ZonedDateTime time = Instant.ofEpochMilli(timeSpan).atZone(bjZone);
            lastUpdateTime = time.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        }

        log.info("change: {},changePercent: {},lastUpdateTime:{}", change, changePercent, lastUpdateTime);
        return new StockInfo(change, changePercent, lastUpdateTime);
    }


}
