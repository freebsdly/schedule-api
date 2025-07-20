package com.example.scheduledemo.service;

import com.example.scheduledemo.service.dto.DifyBlockResponseDTO;
import com.example.scheduledemo.service.dto.DifyRequestBodyDTO;
import com.example.scheduledemo.service.dto.DifySSEResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class AIService
{

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

    @Value("${deepseek.base-url}")
    private String openAICompatibleBaseUrl;

    @Value("${deepseek.chat-url}")
    private String openAICompatibleChatUrl;

    @Value("${deepseek.api-key}")
    private String openAICompatibleAppKey;

    private final WebClient webClient = WebClient.create();

    /**
     * 生成会议纪要，阻塞模式，只能获取String类型，直接映射为结构化对象会为空
     *
     * @param content
     * @return
     * @throws Exception
     */
    public DifyBlockResponseDTO getMeetingMinutes(String content) throws Exception
    {
        DifyRequestBodyDTO body = DifyRequestBodyDTO.builder()
                .user(appName)
                .query(content)
                .inputs(new HashMap<>())
                .responseMode("blocking")
                .build();

        DifyBlockResponseDTO result = webClient.post()
                .uri(difyChatUrl)
                .header("Authorization", String.format("Bearer %s", difyMeetingMinutesApiKey))
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(DifyBlockResponseDTO.class)
                .block();
        log.debug("result: {}", result);
        return result;
    }

    public void getMeetingMinutesStreaming(String content)
    {
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
                        DifySSEResponseDTO.SSEResponseData data = e.getData();
                        DifySSEResponseDTO.SSEResponseOutputs outputs = data.getOutputs();
                        String answer = outputs.getAnswer();
                        log.info("{}, Dify response: {}", e.getEvent(), answer);
                    }
                });
    }

    public Flux<String> intentRecognitionByOpenAICompatibleStreaming(String content)
    {
        String system_prompt = """
                你是一位专业的高管智能助理，专注于为C-level高管提供高效、精准的决策支持服务。具备战略思维、商业敏感。
                ## 核心能力
                1. **战略分析**：快速解析复杂商业问题，提供结构化思考框架
                2. **决策支持**：基于数据提供可执行建议，明确利弊分析
                3. **信息整合**：从海量信息中提取关键洞察，形成简明摘要
                4. **沟通辅助**：协助起草专业商业文件，优化表达方式
                5. **时间管理**：智能安排优先
                ## 知识储备
                - 深度掌握商业管理、战略规划、财务分析等专业知识
                - 熟悉主流行业动态和市场趋势
                - 了解组织行为学和领导力发展理论
                - 精通商业文书写作规范
                - 掌握
                ## 交互原则
                1. 响应迅速，结论前置
                2. 提供选项时附带风险评估
                3. 保持专业但友好的沟通风格
                4. 严格保密所有商业信息
                5. 主动询问需求细节以确保输出精准
                6. 返回内哦让那个应清晰、精确、易于理解，在保持质量的同时，尽可能简洁，不要输出多余解释

                ## 典型任务1
                输入"请安排明天上午9点的会议在236会议室讨论项目进展及问题，x总和各项目负责人都参会"，输出如下：
                意图=创建日程
                会议主题=讨论项目进展及问题
                开始时间=2023-05-10 09:00:00
                结束时间=2023-05-10 10:00:00
                会议地点=236会议室
                会议参与人员=x总、y总、z
                ## 典型任务2
                输入"明天上午9点的会议推迟1小时"，输出如下：
                意图=修改日程
                会议主题=讨论项目进展及问题
                开始时间=2023-05-10 09:00:00
                结束时间=2023-05-10 10:00:00
                会议地点=236会议室
                会议参与人员=x总、y总、z
                ## 典型任务3
                输入"取消明天上午9点的会议"，输出如下：
                意图=取消日程
                会议主题=讨论项目进展及问题
                开始时间=2023-05-10 09:00:00
                结束时间=2023-05-10 10:00:00
                会议地点=236会议室
                会议参与人员=x总、y总、z
                ## 典型任务4
                输入"明天的日程"，输出如下：
                意图=查询日程
                开始时间=2023-05-10 00:00:00
                结束时间=2023-05-11 00:00:00""";
        String user_prompt = """
                今天是%s，%s
                """;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        user_prompt = String.format(user_prompt, today.format(formatter), content);
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("messages", List.of(
                Map.of("role", "system", "content", system_prompt),
                Map.of("role", "user", "content", user_prompt)
                                    ));
        body.put("stream", true);

        return webClient.post()
                .uri(openAICompatibleChatUrl)
                .header("Authorization", String.format("Bearer %s", openAICompatibleAppKey))
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class);
    }

    public void intentRecognitionByOpenAICompatibleStreaming(String content, Consumer<String> callback)
    {
        String system_prompt = """
                你是一位专业的高管智能助理，专注于为C-level高管提供高效、精准的决策支持服务。具备战略思维、商业敏感。
                ## 核心能力
                1. **战略分析**：快速解析复杂商业问题，提供结构化思考框架
                2. **决策支持**：基于数据提供可执行建议，明确利弊分析
                3. **信息整合**：从海量信息中提取关键洞察，形成简明摘要
                4. **沟通辅助**：协助起草专业商业文件，优化表达方式
                5. **时间管理**：智能安排优先
                ## 知识储备
                - 深度掌握商业管理、战略规划、财务分析等专业知识
                - 熟悉主流行业动态和市场趋势
                - 了解组织行为学和领导力发展理论
                - 精通商业文书写作规范
                - 掌握
                ## 交互原则
                1. 响应迅速，结论前置
                2. 提供选项时附带风险评估
                3. 保持专业但友好的沟通风格
                4. 严格保密所有商业信息
                5. 主动询问需求细节以确保输出精准
                6. 返回内哦让那个应清晰、精确、易于理解，在保持质量的同时，尽可能简洁，不要输出多余解释

                ## 典型任务1
                输入"请安排明天上午9点的会议在236会议室讨论项目进展及问题，x总和各项目负责人都参会"，输出如下：
                意图=创建日程
                会议主题=讨论项目进展及问题
                开始时间=2023-05-10 09:00:00
                结束时间=2023-05-10 10:00:00
                会议地点=236会议室
                会议参与人员=x总、y总、z
                ## 典型任务2
                输入"明天上午9点的会议推迟1小时"，输出如下：
                意图=修改日程
                会议主题=讨论项目进展及问题
                开始时间=2023-05-10 09:00:00
                结束时间=2023-05-10 10:00:00
                会议地点=236会议室
                会议参与人员=x总、y总、z
                ## 典型任务3
                输入"取消明天上午9点的会议"，输出如下：
                意图=取消日程
                会议主题=讨论项目进展及问题
                开始时间=2023-05-10 09:00:00
                结束时间=2023-05-10 10:00:00
                会议地点=236会议室
                会议参与人员=x总、y总、z
                ## 典型任务4
                输入"明天的日程"，输出如下：
                意图=查询日程
                开始时间=2023-05-10 00:00:00
                结束时间=2023-05-11 00:00:00""";
        String user_prompt = """
                今天是%s，%s
                """;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        user_prompt = String.format(user_prompt, today.format(formatter), content);
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("messages", List.of(
                Map.of("role", "system", "content", system_prompt),
                Map.of("role", "user", "content", user_prompt)
                                    ));
        body.put("stream", true);

        webClient.post()
                .uri(openAICompatibleChatUrl)
                .header("Authorization", String.format("Bearer %s", openAICompatibleAppKey))
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe(callback);
    }
}
