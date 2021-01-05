package com.dbgreat.tool.fund.websocket.impl;


import com.dbgreat.tool.fund.entity.ResponseData;
import com.dbgreat.tool.fund.websocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * @author : xiongw@glodon.com
 * @date : 2020/5/18 17:52
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;


    @Override
    public <T> void publishMessage(String code, ResponseData data) {
        messagingTemplate.convertAndSend("/topic/fund", data);
    }
}
