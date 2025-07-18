package com.example.scheduledemo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "机器人发送消息参数")
public class RobotSendMessageVO {

    @Schema(description = "消息类型")
    private String msgKey;

    @Schema(description = "消息内容")
    private String message;

    @Schema(description = "接收用户ID列表")
    private List<String> userIds;
}
