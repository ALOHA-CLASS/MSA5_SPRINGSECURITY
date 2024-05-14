package com.aloha.kakao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/oauth")
public class OAuthController {

    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v1/user/logout"; 

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    @GetMapping("/kakao/logout")
    public String kakaoLogout(@AuthenticationPrincipal OAuth2User oauth2User
                             ,HttpServletRequest request) {
        log.info("카카오 로그아웃...");

        String userId = oauth2User.getName();
        log.info("userId : " + userId);

        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        // 본문 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("target_id_type", "user_id");
        requestBody.add("target_id", userId);

        // 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
            KAKAO_API_URL,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // 응답 처리
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            log.info("Response Body: " + responseBody);
        } else {
            log.error("Error occurred. Status code: " + response.getStatusCodeValue());
        }
        
        
        return "redirect:/";
    }
    
    
}
