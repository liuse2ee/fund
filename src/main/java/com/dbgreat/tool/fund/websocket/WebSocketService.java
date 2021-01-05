package com.dbgreat.tool.fund.websocket;


import com.dbgreat.tool.fund.entity.ResponseData;

/**
 * @author : xiongw@glodon.com
 * @date : 2020/5/18 17:52
 */
public interface WebSocketService {
    <T> void publishMessage(String code, ResponseData data);
}
