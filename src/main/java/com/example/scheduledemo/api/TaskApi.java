package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.service.DingTalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TaskApi implements TaskDoc {

    private final DingTalkService dingTalkService;

    @Override
    @GetMapping(value = "/sync/departments")
    public APIResultVO<Void> syncDepartments() throws Exception {
        dingTalkService.syncDepartments();
        return APIResultVO.success(null);
    }

    @Override
    @GetMapping(value = "/sync/employees")
    public APIResultVO<Void> syncEmployees() throws Exception {
        dingTalkService.syncEmployees();
        return APIResultVO.success(null);
    }
}
