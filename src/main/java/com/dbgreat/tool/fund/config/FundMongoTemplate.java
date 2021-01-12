package com.dbgreat.tool.fund.config;

import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class FundMongoTemplate {
    @Value("${spring.data.mongodb.fund.uri}")
    private  String uri;

    @Bean("fundMongodb")
    public org.springframework.data.mongodb.core.MongoTemplate fundMongoTemplate() {
        return new org.springframework.data.mongodb.core.MongoTemplate(fundMongoDBFactory());
    }

    public MongoDbFactory fundMongoDBFactory() {
        return new SimpleMongoDbFactory(new MongoClientURI(uri));
    }
}
