package com.example.scheduledemo.api.vo;

import com.example.scheduledemo.service.dto.MeetingMinutesDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link MeetingMinutesDTO}
 */
@Data
@Schema(description = "会议纪要")
public class MeetingMinutesVO implements Serializable {
    @Schema(description = "id")
    Long id;

    @Schema(description = "会议纪要内容")
    String rawData;
}