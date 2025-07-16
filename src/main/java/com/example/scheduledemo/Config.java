package com.example.scheduledemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@Configuration
public class Config {

    @Bean
    public com.aliyun.dingtalkcalendar_1_0.Client calendarClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.setProtocol("https");
        config.setRegionId("central");
        return new com.aliyun.dingtalkcalendar_1_0.Client(config);
    }

    @Bean
    public com.aliyun.dingtalkconference_1_0.Client conferenceClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.setProtocol("https");
        config.setRegionId("central");
        return new com.aliyun.dingtalkconference_1_0.Client(config);
    }

    @Bean
    public com.aliyun.dingtalkoauth2_1_0.Client oauth2Client() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.setProtocol("https");
        config.setRegionId("central");
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

}
