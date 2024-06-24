package com.aloha.jwt.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aloha.jwt.constants.SecurityConstants;
import com.aloha.jwt.domain.AuthenticationRequest;
import com.aloha.jwt.prop.JwtProps;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class LoginController {

   @Autowired
    private JwtProps jwtProps;

    /**
     * 👩‍💼➡🔐 JWT 을 생성하는 Login 요청
     * [GET] - /login
     * body : 
            {
                "username" : "joeun",
                "password" : "123456"
            }
     * @param authReq
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authReq) {
        // 사용자로부터 전달받은 인증 정보
        String username = authReq.getUsername();
        String password = authReq.getPassword();

        log.info("username : " + username);
        log.info("password : " + password);

        // 사용자 역할 정보
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");

        // 서명에 사용할 키 생성 (새로운 방식)
        String secretKey = jwtProps.getSecretKey();
        byte[] signingKey = jwtProps.getSecretKey().getBytes();

        log.info("secretKey : " + secretKey);
        log.info("signingKey : " + signingKey);

        // JWT 토큰 생성
        String jwt = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)      // 서명에 사용할 키와 알고리즘 설정
                // .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)        // deprecated (version: before 1.0)
                .header()                                                      // update (version : after 1.0)
                    .add("typ", SecurityConstants.TOKEN_TYPE)              // 헤더 설정
                .and()
                .expiration(new Date(System.currentTimeMillis() + 864000000))  // 토큰 만료 시간 설정 (10일)
                .claim("uid", username)                                   // 클레임 설정: 사용자 아이디
                .claim("rol", roles)                                      // 클레임 설정: 역할 정보
                .compact();                                                    // 최종적으로 토큰 생성

        log.info("jwt : " + jwt);

        // 생성된 토큰을 클라이언트에게 반환
        return new ResponseEntity<String>(jwt, HttpStatus.OK);
    }
  
}
