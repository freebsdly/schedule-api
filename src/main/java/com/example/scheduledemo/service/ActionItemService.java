package com.example.scheduledemo.service;

import com.example.scheduledemo.entity.ActionItemDto;
import com.example.scheduledemo.entity.ActionItemEntity;
import com.example.scheduledemo.entity.EntityMapper;
import com.example.scheduledemo.repository.ActionItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActionItemService {

    @Autowired
    ActionItemRepository actionItemRepository;


    public List<ActionItemDto> getActionItems() {
        List<ActionItemEntity> items = actionItemRepository.findAll();
        return items.stream().map(EntityMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Tool(description = "Get weather alerts for a US state")
    public String getAlerts(
            @ToolParam(description = "Two-letter US state code (e.g. CA, NY)") String state) {
        // Returns active alerts including:
        // - Event type
        // - Affected area
        // - Severity
        // - Description
        // - Safety instructions
        return null;
    }
}
