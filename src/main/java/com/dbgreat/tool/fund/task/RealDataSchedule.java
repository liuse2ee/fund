package com.dbgreat.tool.fund.task;


import com.dbgreat.tool.fund.entity.ResponseData;
import com.dbgreat.tool.fund.service.DataService;
import com.dbgreat.tool.fund.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RealDataSchedule {
    @Autowired
    private DataService dataService;

    @Autowired
    private WebSocketService webSocketService;
    //0 0 5 ? * MON-FRI
    //0 */10 * * * ?
    //@Scheduled(initialDelay = 5000, fixedRate = 300000)

    @Scheduled(cron = "0 */30 * ? * MON-FRI")
    public void runTask() {
        log.info("start push message!");
        //获取数据
        ResponseData data = dataService.getData();

        //发送数据
        webSocketService.publishMessage("", data);

        log.info("push message complete !");
    }
}
