package com.example.scheduledemo.feignclients;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "dingtalk-client")
public interface DingtalkClient {
}
