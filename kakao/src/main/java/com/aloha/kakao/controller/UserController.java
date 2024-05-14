package com.aloha.kakao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aloha.kakao.dto.Users;
import com.aloha.kakao.service.UserService;

import lombok.extern.slf4j.Slf4j;


/**
 * 👩‍💼 회원
 * - 아이디 중복 체크
 * - 
 * 
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    /**
     * 아이디 중복 검사
     * @param userId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/check/{userId}")
    public ResponseEntity<Boolean> userCheck(@PathVariable("userId") String userId) throws Exception {
        log.info("아이디 중복 확인 : " + userId);
        Users user = userService.select(userId);
        // 아이디 중복
        if( user != null ) {
            log.info("중복된 아이디 입니다 - " + userId);
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        // 사용 가능한 아이디입니다.
        log.info("사용 가능한 아이디 입니다." + userId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    

}
