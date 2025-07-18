package com.example.scheduledemo.service;

import com.aliyun.dingtalkim_1_0.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.open.app.api.callback.OpenDingTalkCallbackListener;
import com.dingtalk.open.app.api.models.bot.ChatbotMessage;
import com.example.scheduledemo.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RobotConsumer implements OpenDingTalkCallbackListener<ChatbotMessage, Void> {

    @Value("${dingtalk.ak}")
    private String appKey;

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private com.aliyun.dingtalkim_1_0.Client imClient;


    private final String messageCardTemplate = """
            {
              "config": {
                "autoLayout": true,
                "enableForward": true
              },
              "header": {
                "title": {
                  "type": "text",
                  "text": "Vision日程助手"
                },
                "logo": "@lALPDfJ6V_FPDmvNAfTNAfQ"
              },
              "contents": [
                {
                  "type": "text",
                  "text": "%s",
                  "id": "text_1693929551595"
                },
                {
                  "type": "divider",
                  "id": "divider_1693929551595"
                },
                {
                  "type": "markdown",
                  "text": "%s",
                  "id": "markdown_1693929674245"
                }
              ]
            }
            """;

    @Override
    public Void execute(ChatbotMessage message) {
        log.debug("received message, type: {}", message.getMsgtype());

        UUID cardInstanceId = UUID.randomUUID();
        // 发送交互式卡片
        String cardData = String.format(messageCardTemplate, "", "");
        SendRobotInteractiveCardRequest request = new SendRobotInteractiveCardRequest();
        request.setCardTemplateId("StandardCard");
        request.setCardBizId(cardInstanceId.toString());
        request.setCardData(cardData);
        request.setRobotCode(appKey);
        request.setSendOptions(new SendRobotInteractiveCardRequest.SendRobotInteractiveCardRequestSendOptions());
        request.setPullStrategy(false);

        if (message.getConversationType().equals("2")) {
            // group chat; 群聊
            request.setOpenConversationId(message.getConversationId());
        } else {
            // ConversationType == "1": private chat; 单聊
            String receiverFormat = """
                    {"userId":"%s"}
                    """;
            String receiver = String.format(receiverFormat, message.getSenderStaffId());
            request.setSingleChatReceiver(receiver);
        }

        try {
            sendInteractiveCard(request);
            String returnMessage = getAnswerString(message);
            UpdateRobotInteractiveCardRequest updateRequest = new UpdateRobotInteractiveCardRequest();
            updateRequest.setCardBizId(cardInstanceId.toString());
            updateRequest.setCardData(String.format(messageCardTemplate, "明天日程列表", returnMessage));
            updateInteractiveCard(updateRequest);

        } catch (Exception e) {
            log.warn("robot send message failed. {}", e.getMessage());
        }

        return null;
    }

    private static @NotNull String getAnswerString(ChatbotMessage message) {
        String text = switch (message.getMsgtype()) {
            case "text" -> message.getText().getContent();
            case "audio" -> {
                // TODO: 音频处理
                yield "明日日程";
            }
            default -> {
                throw new BusinessException("unsupported message type.");
            }
        };
        //TODO: 按正常逻辑生成返回结果
        String returnMessage = "未知";
        if (text.contains("明天日程")) {
            returnMessage = """
                    1 2025-07-10 09:00:00 - 10:00:00 参加培训
                    2 2025-07-10 10:00:00 - 11:00:00 签到
                    3 2025-07-10 11:00:00 - 12:00:00 签退
                    4 2025-07-10 12:00:00 - 13:00:00 休息
                    """;
        }
        return returnMessage;
    }

    private void sendInteractiveCard(SendRobotInteractiveCardRequest request) throws Exception {
        String token = dingTalkService.getAccessToken();
        SendRobotInteractiveCardHeaders headers = new SendRobotInteractiveCardHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        RuntimeOptions runtimeOptions = new RuntimeOptions();

        SendRobotInteractiveCardResponse resp = imClient.sendRobotInteractiveCardWithOptions(request, headers, runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("send interactive card failed. code: {}", resp.getStatusCode());
            throw new RuntimeException("send interactive card failed.");
        }
    }

    private void updateInteractiveCard(UpdateRobotInteractiveCardRequest request) throws Exception {
        String token = dingTalkService.getAccessToken();
        UpdateRobotInteractiveCardHeaders headers = new UpdateRobotInteractiveCardHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        RuntimeOptions runtimeOptions = new RuntimeOptions();

        UpdateRobotInteractiveCardResponse resp = imClient.updateRobotInteractiveCardWithOptions(request, headers, runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("update interactive card failed. code: {}", resp.getStatusCode());
            throw new RuntimeException("update interactive card failed.");
        }
    }

}
