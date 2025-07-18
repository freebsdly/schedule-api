package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.QueryTextVO;
import com.example.scheduledemo.api.vo.RobotSendMessageVO;
import com.example.scheduledemo.service.dto.CreateDocDTO;
import com.example.scheduledemo.service.dto.RecordTextResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@Tag(name = "DingTalk API")
public interface DingTalkDoc {

    @Operation(summary = "Get Event Cloud Record Text", description = "获取日程录制音频转文字")
    APIResultVO<RecordTextResultDTO> getEventCloudRecordText(@ParameterObject QueryTextVO vo) throws Exception;

    @Operation(summary = "Create doc in knowledge base", description = "在知识库中创建文档")
    APIResultVO<String> createKnowledgeBaseDoc(CreateDocDTO dto) throws Exception;

    @Operation(summary = "Send message by robot", description = "使用预定义的机器人发送消息")
    APIResultVO<Void> robotBatchSendMessage(RobotSendMessageVO vo) throws Exception;
}
