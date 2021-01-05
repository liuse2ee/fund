package com.dbgreat.tool.fund.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author : xiongw@glodon.com
 * @date : 2021/1/5 9:41
 */
@Slf4j
public class CacheUtil {
    private static LoadingCache<String, String> loadingCache = CacheBuilder
            .newBuilder()
            .maximumSize(2)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .concurrencyLevel(2)
            .recordStats()
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    String value = "";
                    log.info(" load value by key; key:{},value:{}", key, value);
                    return value;
                }
            });

    public static String getValue(String key) {
        try {
            return loadingCache.get(key);
        } catch (Exception e) {
            log.warn(" get key error ", e);
            return null;
        }
    }
}
