package com.example.scheduledemo.service;

import com.example.scheduledemo.service.dto.DifyRequestBodyDTO;
import com.example.scheduledemo.service.dto.DifySSEResponseDTO;
import com.example.scheduledemo.service.dto.DifySSEResponseDataDTO;
import com.example.scheduledemo.service.dto.DifySSEResponseOutputsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;

@Service
@Slf4j
public class AIService {

    @Value("${dify.base-url}")
    private String difyApiUrl;

    @Value("${dify.chat-url}")
    private String difyChatUrl;

    @Value("${dify.meeting-minutes.api-key}")
    private String difyMeetingMinutesApiKey;

    @Value("${dify.meeting-minutes.streaming-mode}")
    private String difyMeetingMinutesStreamingMode;

    @Value("${spring.application.name}")
    private String appName;

    private WebClient webClient = WebClient.create();

    /**
     * 生成会议纪要，阻塞模式，只能获取String类型，直接映射为结构化对象会为空
     *
     * @param content
     * @return
     * @throws Exception
     */
    public String getMeetingMinutes(String content) throws Exception {
        DifyRequestBodyDTO body = DifyRequestBodyDTO.builder()
                .user(appName)
                .query(content)
                .inputs(new HashMap<>())
                .responseMode("blocking")
                .build();

        String result = webClient.post()
                .uri(difyChatUrl)
                .header("Authorization", String.format("Bearer %s", difyMeetingMinutesApiKey))
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.debug("result: {}", result);
        return result;
    }

    public void getMeetingMinutesStreaming(String content) {
        DifyRequestBodyDTO body = DifyRequestBodyDTO.builder()
                .user(appName)
                .query(content)
                .inputs(new HashMap<>())
                .responseMode("streaming")
                .build();

        webClient.post()
                .uri(difyChatUrl)
                .header("Authorization", String.format("Bearer %s", difyMeetingMinutesApiKey))
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(DifySSEResponseDTO.class)
                .subscribe(e -> {
                    if (e.getEvent().equals("workflow_finished")) {
                        DifySSEResponseDataDTO data = e.getData();
                        DifySSEResponseOutputsDTO outputs = data.getOutputs();
                        String answer = outputs.getAnswer();
                        log.info("{}, Dify response: {}", e.getEvent(), answer);
                    }
                });
    }
}
