package com.dbgreat.tool.fund.service.impl;

import com.dbgreat.tool.fund.entity.HistoryData;
import com.dbgreat.tool.fund.service.HistoryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class HistoryDataServiceImpl implements HistoryDataService {

    @Qualifier("fundMongodb")
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void saveHistory(HistoryData data) {
        mongoTemplate.save(data);
    }

    @Override
    public void updateHistory(HistoryData historyData) {
        Criteria c = Criteria.where("date").is(historyData.getDate());
        Update update = new Update();
        update.set("officialChangePercent", historyData.getOfficialChangePercent());
        mongoTemplate.upsert(new Query().addCriteria(c), update, HistoryData.class);
    }

    @Override
    public HistoryData getHistory(String date) {
        Criteria c = Criteria.where("date").is(date);
        return mongoTemplate.findOne(new Query().addCriteria(c), HistoryData.class);
    }

    @Override
    public List<HistoryData> getHistoryList() {
        return mongoTemplate.find(
                new Query().skip(0).limit(100).with(new Sort(Sort.Direction.DESC, "date")),
                HistoryData.class
        );
    }
}
