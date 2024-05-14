package com.aloha.kakao.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private static final String KAKAO_LOGOUT_URL = "https://kapi.kakao.com/v1/user/logout"; 
    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink"; 

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;


    @Value("${kakao.rest-api-key}")
    private String restAPIKey;

    @Override
    public void logout(HttpServletRequest request
                     , HttpServletResponse response
                     , Authentication authentication) {

        log.info("authentication: " + authentication);

        HttpSession session = request.getSession();
        String accessToken = (String) session.getAttribute("access_token");

        log.info("accessToken : " + accessToken);

        if( authentication instanceof OAuth2AuthenticationToken ) {
            log.info("OAuth 로그아웃한 경우..");

            // RestTemplate 인스턴스 생성
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.set("Authorization", "Bearer " + accessToken);

            // 요청 엔티티 생성
            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> responseEntity = null;

           
            // 👩‍💻 카카오 연결 끊기
            // ✅ 연결 끊기를 하면, 재로그인 시, 동의 항목부터 시작한다.
            // GET 요청 보내기
            // responseEntity = restTemplate.exchange(
            //         KAKAO_UNLINK_URL,
            //         HttpMethod.GET,
            //         requestEntity,
            //         String.class
            // );

            // // 응답 처리
            // if (responseEntity.getStatusCode() == HttpStatus.OK) {
            //     String responseBody = responseEntity.getBody();
            //     log.info("Response Body: " + responseBody);
            // } else {
            //     log.error("Error occurred. Status code: " + responseEntity.getStatusCodeValue());
            // }

            
            // 👩‍💻 카카오 로그아웃
            // ✅ 현재 액세스 토큰을 만료시킨다. 
            // POST 요청 보내기
            // responseEntity = restTemplate.exchange(
            //         KAKAO_LOGOUT_URL,
            //         HttpMethod.POST,
            //         requestEntity,
            //         String.class
            // );

            // // 응답 처리
            // if (responseEntity.getStatusCode() == HttpStatus.OK) {
            //     String responseBody = responseEntity.getBody();
            //     log.info("Response Body: " + responseBody);
            // } else {
            //     log.error("Error occurred. Status code: " + responseEntity.getStatusCodeValue());
            // }




        }

        
    }
    
}
