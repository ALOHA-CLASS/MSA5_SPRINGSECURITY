package com.aloha.security5.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
@RequestMapping("/api")
public class ApiController {

    @GetMapping("")
    public String index() {
        return "/api/index";
    }

    @ResponseBody
    @PostMapping("/form")
    public String form(@RequestParam("name") String name) {
        log.info("[POST] - /api/form");
        log.info("name : " + name);
        return "SUCCESS";
    }

    @ResponseBody
    @PostMapping("/ajax")
    public String ajax(@RequestBody Map<String, String> requestMap) {
        log.info("[POST] - /api/ajax");
        log.info("name : " + requestMap.get("name"));
        return "SUCCESS";
    }
    

    
    
}
