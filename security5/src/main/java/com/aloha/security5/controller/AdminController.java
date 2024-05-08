package com.aloha.security5.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * 관리자 페이지
     * @return
     */
    @GetMapping({"/", ""})
    public String index() {
        log.info("/admin");

        return "/admin/index";
    }
    
    
}
