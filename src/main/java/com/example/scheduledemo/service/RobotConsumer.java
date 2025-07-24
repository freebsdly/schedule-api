package com.example.scheduledemo.service;

import com.aliyun.dingtalkcalendar_1_0.models.ListEventsViewResponseBody;
import com.aliyun.dingtalkim_1_0.models.SendRobotInteractiveCardRequest;
import com.aliyun.dingtalkim_1_0.models.UpdateRobotInteractiveCardRequest;
import com.dingtalk.open.app.api.callback.OpenDingTalkCallbackListener;
import com.dingtalk.open.app.api.models.bot.ChatbotMessage;
import com.example.scheduledemo.exception.BusinessException;
import com.example.scheduledemo.service.dto.DTOMapper;
import com.example.scheduledemo.service.dto.IntentRecognitionDTO;
import com.example.scheduledemo.service.dto.OpenAICompatibleOutputDTO;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RobotConsumer implements OpenDingTalkCallbackListener<ChatbotMessage, Void> {

    private final DingTalkService dingTalkService;

    private final AIService aiService;

    private final ObjectMapper objectMapper;


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

        Consumer<IntentRecognitionDTO> cardUpdater = msg -> {
            String title = "";
            if (msg.getIntent() != null) {
                title = msg.getIntent();
            }
            String content = "";
            if (msg.getSummary() != null) {
                content = String.format("Summary: %s\n", msg.getSummary());
            }
            if (msg.getStartTime() != null) {
                content += String.format("StartTime: %s\n", msg.getStartTime());
            }
            if (msg.getEndTime() != null) {
                content += String.format("EndTime: %s\n", msg.getEndTime());
            }
            if (msg.getDescription() != null) {
                content += String.format("Description: %s\n", msg.getDescription());
            }
            if (msg.getLocation() != null) {
                content += String.format("Location: %s\n", msg.getLocation());
            }
            if (msg.getAttendees() != null) {
                content += String.format("Attendees: %s\n", msg.getAttendees());
            }
            UpdateRobotInteractiveCardRequest updateRequest = new UpdateRobotInteractiveCardRequest();
            updateRequest.setCardBizId(cardInstanceId.toString());
            updateRequest.setCardData(String.format(messageCardTemplate, title, content));
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
    static class Answer {
        private String intent;
        private String output;
        private String title;
    }

    private Answer callAiToOperation(ChatbotMessage message, Consumer<IntentRecognitionDTO> callback) throws Exception {
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
                dingTalkService.createCalendarEvent(DTOMapper.INSTANCE.toDTO(intent));
                yield new Answer(intent.getIntent(), "创建日程成功", intent.getIntent());
            }
            case "查询日程" -> {
                // TODO: 处理分页查询
                ListEventsViewResponseBody calendarEvents = dingTalkService.getCalendarEvents(DTOMapper.INSTANCE.toDTO(intent), null);
                yield new Answer(intent.getIntent(), "查询日程成功", intent.getIntent());
            }
            case "修改日程" -> {
                dingTalkService.updateCalendarEvent(DTOMapper.INSTANCE.toDTO(intent));
                yield new Answer(intent.getIntent(), "修改日程成功", intent.getIntent());
            }
            case "删除日程" -> {
                dingTalkService.deleteCalendarEvent(DTOMapper.INSTANCE.toDTO(intent));
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
    private IntentRecognitionDTO userIntentRecognition(String ask, Consumer<IntentRecognitionDTO> callback)
            throws InterruptedException {
        AtomicReference<IntentRecognitionDTO> data = new AtomicReference<>(new IntentRecognitionDTO());
        StringBuffer sb = new StringBuffer();
        Function<String, String> newCallBack = msg -> {
            if (msg.startsWith("[DONE]")) {
                return "";
            }
            try {
                OpenAICompatibleOutputDTO output = objectMapper.readValue(msg, OpenAICompatibleOutputDTO.class);
                String content = output.getChoices()[0].getDelta().getContent();
                log.debug("content: {}", sb);
                sb.append(content);
                IntentRecognitionDTO intentRecognitionDTO = parseAnswer(sb.toString());
                data.set(intentRecognitionDTO);
                if (intentRecognitionDTO.getIntent() != null && intentRecognitionDTO.getSummary() != null) {
                    callback.accept(intentRecognitionDTO);

                }
                return "";
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        Flux<String> flux = aiService.intentRecognitionByOpenAICompatibleStreaming(ask);
        flux.map(newCallBack).collectList().block();

        return data.get();
    }

    private String speechToText(String url) {
        return "明日日程";
    }

    private IntentRecognitionDTO parseAnswer(String answer) {
        IntentRecognitionDTO data = new IntentRecognitionDTO();
        data.setOutput(answer);
        JsonFactory factory = new JsonFactory();
        try (JsonParser jsonParser = factory.createParser(new StringReader(answer))) {
            String currentFieldName = null;
            while (jsonParser.nextToken() != null) {
                JsonToken token = jsonParser.getCurrentToken();

                if (token == null) {
                    break;
                }
                switch (token) {
                    case FIELD_NAME:
                        currentFieldName = jsonParser.currentName();
                        break;
                    case VALUE_STRING:
                        String valueAsString = jsonParser.getValueAsString();
                        switch (Objects.requireNonNull(currentFieldName)) {
                            case "indent":
                                data.setIntent(valueAsString);
                                break;
                            case "summary":
                                data.setSummary(valueAsString);
                                break;
                            case "description":
                                data.setDescription(valueAsString);
                                break;
                            case "location":
                                data.setLocation(valueAsString);
                                break;
                            case "start_time":
                                data.setStartTime(valueAsString);
                                break;
                            case "end_time":
                                data.setEndTime(valueAsString);
                                break;
                            default:
                                break;
                        }
                        break;
                    case START_ARRAY:
                        List<String> arrayValues = new ArrayList<>();
                        // Keep reading until the array ends
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            if (jsonParser.getCurrentToken() == JsonToken.VALUE_STRING) {
                                arrayValues.add(jsonParser.getValueAsString());
                            }
                        }
                        data.setAttendees(arrayValues);
                        break;
                    default:
                        // Handle or ignore other token types as needed
                        break;
                }
            }
            return data;
        } catch (Exception e) {
            log.warn("parse answer failed. {}", e.getMessage());
            return data;
        }
    }

}
