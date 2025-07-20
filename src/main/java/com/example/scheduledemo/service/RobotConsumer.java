package com.example.scheduledemo.service;

import com.aliyun.dingtalkcalendar_1_0.models.ListEventsViewResponseBody;
import com.aliyun.dingtalkim_1_0.models.SendRobotInteractiveCardRequest;
import com.aliyun.dingtalkim_1_0.models.UpdateRobotInteractiveCardRequest;
import com.dingtalk.open.app.api.callback.OpenDingTalkCallbackListener;
import com.dingtalk.open.app.api.models.bot.ChatbotMessage;
import com.example.scheduledemo.exception.BusinessException;
import com.example.scheduledemo.service.dto.IntentRecognitionDTO;
import com.example.scheduledemo.service.dto.OpenAICompatibleOutputDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Component
public class RobotConsumer implements OpenDingTalkCallbackListener<ChatbotMessage, Void>
{

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private AIService aiService;

    @Autowired
    private ObjectMapper objectMapper;


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
    public Void execute(ChatbotMessage message)
    {
        log.debug("received message, type: {}", message.getMsgtype());

        UUID cardInstanceId = UUID.randomUUID();
        // 发送交互式卡片
        String cardData = String.format(messageCardTemplate, "", "");
        SendRobotInteractiveCardRequest request = new SendRobotInteractiveCardRequest();
        request.setCardTemplateId("StandardCard");
        request.setCardBizId(cardInstanceId.toString());
        request.setCardData(cardData);
        request.setRobotCode(dingTalkService.getAppKey());
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

        Consumer<String> cardUpdater = msg -> {
            UpdateRobotInteractiveCardRequest updateRequest = new UpdateRobotInteractiveCardRequest();
            updateRequest.setCardBizId(cardInstanceId.toString());
            updateRequest.setCardData(String.format(messageCardTemplate, "", msg));
            try {
                dingTalkService.updateInteractiveCard(updateRequest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        try {
            dingTalkService.sendInteractiveCard(request);
            Answer answer = callAiToOperation(message, cardUpdater);
//            UpdateRobotInteractiveCardRequest updateRequest = new UpdateRobotInteractiveCardRequest();
//            updateRequest.setCardBizId(cardInstanceId.toString());
//            updateRequest.setCardData(String.format(messageCardTemplate, answer.getTitle(), answer.getOutput()));
//            dingTalkService.updateInteractiveCard(updateRequest);

        } catch (Exception e) {
            log.warn("robot send message failed. {}", e.getMessage());
        }

        return null;
    }

    @Data
    @AllArgsConstructor
    static class Answer
    {
        private String intent;
        private String output;
        private String title;
    }

    private Answer callAiToOperation(ChatbotMessage message, Consumer<String> callback) throws Exception
    {
        String text = switch (message.getMsgtype()) {
            case "text" -> message.getText().getContent();
            case "audio" -> {
                String downloadCode = message.getContent().getDownloadCode();
                String url = dingTalkService.getAudioMessageDownloadUrl(downloadCode);
                yield speechToText(url);
            }
            default -> {
                throw new BusinessException("unsupported message type.");
            }
        };
        IntentRecognitionDTO intent = userIntentRecognition(text, callback);
        return switch (intent.getIntent()) {
            case "创建日程" -> {
                dingTalkService.createCalendarEvent(intent.getSlots());
                yield new Answer(intent.getIntent(), "创建日程成功", intent.getIntent());
            }
            case "查询日程" -> {
                // TODO: 处理分页查询
                ListEventsViewResponseBody calendarEvents = dingTalkService.getCalendarEvents(intent.getSlots(), null);
                yield new Answer(intent.getIntent(), "查询日程成功", intent.getIntent());
            }
            case "修改日程" -> {
                dingTalkService.updateCalendarEvent(intent.getSlots());
                yield new Answer(intent.getIntent(), "修改日程成功", intent.getIntent());
            }
            case "删除日程" -> {
                dingTalkService.deleteCalendarEvent(intent.getSlots());
                yield new Answer(intent.getIntent(), "取消日程成功", intent.getIntent());
            }
            default -> {
                yield new Answer(intent.getIntent(), "未能识别你的意图", "意图识别");
            }
        };
    }

    /**
     * 用户意图识别
     *
     * @param ask
     * @return
     */
    private IntentRecognitionDTO userIntentRecognition(String ask, Consumer<String> callback)
            throws InterruptedException
    {
        IntentRecognitionDTO data = new IntentRecognitionDTO();
        StringBuffer sb = new StringBuffer();
        Function<String, String> newCallBack = msg -> {
            if (msg.startsWith("[DONE]")) {
                return "";
            }
            try {
                OpenAICompatibleOutputDTO output = objectMapper.readValue(msg, OpenAICompatibleOutputDTO.class);
                String content = output.getChoices()[0].getDelta().getContent();
                sb.append(content);
                int i = sb.indexOf("\n");
                if (i > 0) {
                    callback.accept(sb.substring(i));
                }
                return "";
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        Flux<String> flux = aiService.intentRecognitionByOpenAICompatibleStreaming(ask);
        flux.map(newCallBack).collectList().block();

        int i = sb.indexOf("\n");
        data.setIntent(sb.substring(0, i));
        data.setOutput(sb.substring(i));
        return data;
    }

    private String speechToText(String url)
    {
        return "明日日程";
    }
}
