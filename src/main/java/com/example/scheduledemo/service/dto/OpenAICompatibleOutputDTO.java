package com.example.scheduledemo.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenAICompatibleOutputDTO
{
    private String id;
    private Choice[] choices;
    private long created;
    private String model;
    private String systemFingerprint;
    private String object;
    private Usage usage;

    @Data
    @NoArgsConstructor
    public static class Choice
    {
        private String finishReason;
        private long index;
        private Message message;
        private Message delta;
        private LogProbs logprobs;
    }

    @Data
    @NoArgsConstructor
    public static class LogProbs
    {
        private Content[] content;
    }

    @Data
    @NoArgsConstructor
    public static class Content
    {
        private String token;
        private long logprob;
        private long[] bytes;
        private Content[] topLogprobs;
    }

    @Data
    @NoArgsConstructor
    public static class Message
    {
        private String content;
        private String reasoningContent;
        private ToolCall[] toolCalls;
        private String role;
    }

    @Data
    @NoArgsConstructor
    public static class ToolCall
    {
        private String id;
        private String type;
        private Function function;
    }

    @Data
    @NoArgsConstructor
    public static class Function
    {
        private String name;
        private String arguments;
    }

    @Data
    @NoArgsConstructor
    public static class Usage
    {
        private long completionTokens;
        private long promptTokens;
        private long promptCacheHitTokens;
        private long promptCacheMissTokens;
        private long totalTokens;
        private CompletionTokensDetails completionTokensDetails;
    }

    @Data
    @NoArgsConstructor
    public static class CompletionTokensDetails
    {
        private long reasoningTokens;
    }

}
