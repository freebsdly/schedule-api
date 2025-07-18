package com.example.scheduledemo.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "API返回结果")
public class APIResultVO<T> implements Serializable {
    @Schema(description = "错误码")
    private Long code;
    @Schema(description = "错误信息")
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "数据")
    private T data;

    public APIResultVO(Long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> APIResultVO<T> success() {
        return new APIResultVO<>(0L, "success", null);
    }

    public static <T> APIResultVO<T> success(T data) {
        return new APIResultVO<>(0L, "success", data);
    }

    public static <T> APIResultVO<T> failure(Long code, String message) {
        return new APIResultVO<>(code, message, null);
    }
}