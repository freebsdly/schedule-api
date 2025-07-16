package com.example.scheduledemo.api;

import com.example.scheduledemo.entity.ActionItemDTO;
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
    List<ActionItemDTO> queryAllActionItems() {
        return actionItemService.getActionItems();
    }
}
