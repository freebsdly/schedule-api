package com.example.scheduledemo.feignclients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dingtalk-client", url = "https://oapi.dingtalk.com/topapi")
public interface DingTalkFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/v2/department/listsub")
    DepartmentResponseDTO getSubDepartments(@RequestParam(name = "access_token") String accessToken, @RequestParam(name = "dept_id") Long departmentId);


    @RequestMapping(method = RequestMethod.POST, value = "/v2/user/list")
    EmployeeResponseDTO getDepartmentEmployees(@RequestParam(name = "access_token") String accessToken, @RequestBody EmployeeQuery body);
}
