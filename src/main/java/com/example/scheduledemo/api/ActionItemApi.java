package com.example.scheduledemo.api;

import com.example.scheduledemo.entity.ActionItemDto;
import com.example.scheduledemo.service.ActionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/action-items")
public class ActionItemApi {

    @Autowired
    ActionItemService actionItemService;

    @GetMapping()
    List<ActionItemDto> queryAllActionItems() {
        return actionItemService.getActionItems();
    }
}
