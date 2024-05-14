package com.aloha.kakao.service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Service;

import com.aloha.kakao.dto.CustomUser;
import com.aloha.kakao.dto.Users;
import com.aloha.kakao.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomRememberMeService implements RememberMeServices {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserMapper userMapper;

    @Value("${default.password}")
    private String defaultPassword;

    @Override
    public Authentication autoLogin(HttpServletRequest request
                                  , HttpServletResponse response) {
        log.info("자동 로그인 처리 시작...");

        String rememberMeToken = "";
        Cookie[] cookies = request.getCookies();    
        if( cookies != null)
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if( name.equals("remember-me") ) {
                rememberMeToken = cookie.getValue();
            }
        }

        log.info("rememberMeToken : " + rememberMeToken);

        if( rememberMeToken == null || rememberMeToken.equals("") ) return null;

        String series = rememberMeToken.split(":")[0];
        String token = rememberMeToken.split(":")[1];

        log.info("series : " + series);
        log.info("token : " + token);

        JdbcTokenRepositoryImpl repositoryImpl = new JdbcTokenRepositoryImpl();
        repositoryImpl.setDataSource(dataSource);
        PersistentRememberMeToken persistentRememberMeToken = repositoryImpl.getTokenForSeries(series);

        if( persistentRememberMeToken == null ) return null;
        String userId = persistentRememberMeToken.getUsername();

        log.info("userId : " + userId);
        if( userId == null ) return null;
        Users user = null;
        try {
            user = userMapper.select(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if( user == null ) return null;
        // 🔐 CustomUser ➡ UserDetails
        
        user.setUserId(userId + "_RememberMe");
        UserDetails userDetails = new CustomUser(user, true, defaultPassword);   // true 붙이면 자동로그인
        log.info("userDetails : " + userDetails);
        log.info("getPassword() : " + userDetails.getPassword());

        // 자동 로그인을 위한 Authentication 객체 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(
            userDetails, defaultPassword, userDetails.getAuthorities());

        // SecurityContextHolder.getContext().setAuthentication(auth);
        // auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("auth : " + auth);
        return auth;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        log.error("자동 로그인 실패!!!");

        return;
    }

    /**
     * 로그인 성공 시
     * remember-me 🍪 쿠키 생성
     */
    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication successfulAuthentication) {

        String rememberMe = request.getParameter("remember-me");
        // 필요한 경우 자동 로그인 처리
        // 예시: 사용자가 "자동 로그인" 옵션을 선택한 경우에만 자동 로그인 처리
        log.info("rememberMe : " + rememberMe);
        // 일반 로그인의 경우 자동로그인 체크박스 체크 시
        boolean rememberMeCheck = rememberMe != null && ( rememberMe.equals("true") || rememberMe.equals("on") );

        // 소셜 로그인의 경우 무조건 자동 로그인
        log.info("successfulAuthentication : " + successfulAuthentication);
        if( !rememberMeCheck )
            rememberMeCheck = successfulAuthentication instanceof OAuth2AuthenticationToken;

        // String userId = successfulAuthentication.getName();
        String socialType = (String) request.getSession().getAttribute("socialType");

        String userId = successfulAuthentication.getName();
        if( socialType != null )
            userId = socialType + "_" + successfulAuthentication.getName();

        if ( rememberMeCheck ) {
            log.info("자동로그인 remember-me 쿠키 생성");
            // 자동 로그인 토큰 생성 및 저장하는 로직 구현
            String series = UUID.randomUUID().toString();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String token = passwordEncoder.encode( series + ":" + userId );

            // 생성된 시리즈와 토큰을 데이터베이스에 저장하는 로직 구현
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            Date date = calendar.getTime();
            PersistentRememberMeToken rememberMeToken = new PersistentRememberMeToken(userId, series, token, date);
            JdbcTokenRepositoryImpl repositoryImpl = new JdbcTokenRepositoryImpl();
            repositoryImpl.setDataSource(dataSource);

            repositoryImpl.removeUserTokens(userId);            // 기존 토큰 삭제
            repositoryImpl.createNewToken(rememberMeToken);     // 토큰 생성

            // 쿠키에 시리즈와 토큰을 저장하는 로직 구현
            Cookie cookie = new Cookie("remember-me", series + ":" + token);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7일간 유효한 쿠키 설정
            response.addCookie(cookie);
        }
    }
    
   
    
}
