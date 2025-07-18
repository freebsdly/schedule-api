package com.example.scheduledemo.service;

import com.dingtalk.open.app.api.OpenDingTalkClient;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.callback.DingTalkStreamTopics;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RobotListener {

    @Autowired
    private RobotConsumer robotConsumer;

    @Value("${dingtalk.ak}")
    private String appKey;

    @Value("${dingtalk.sk}")
    private String appSecret;

    @PostConstruct
    public void init() throws Exception {
        // init stream client
        OpenDingTalkClient client = OpenDingTalkStreamClientBuilder.custom()
                .credential(new AuthClientCredential(appKey, appSecret))
                .registerCallbackListener(DingTalkStreamTopics.BOT_MESSAGE_TOPIC, robotConsumer)
                .build();
        client.start();
    }

}
