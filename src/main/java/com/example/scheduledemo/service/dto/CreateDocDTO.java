package com.example.scheduledemo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDocDTO {

    String workspaceId;
    String unionId;
    String parentId;
    String templateId;
    String content;
    String docType = "DOC";
    String templateType = "user_template";
    String docName;
    String contentType = "markdown";
}
