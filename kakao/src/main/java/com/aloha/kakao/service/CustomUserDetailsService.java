package com.aloha.kakao.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aloha.kakao.dto.CustomUser;
import com.aloha.kakao.dto.Users;
import com.aloha.kakao.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    HttpSession httpSession;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService - 여기서 사용자 정보 체크");
        log.info("username : " + username);

        // 자동 로그인인 경우 ([userId]__RememberMe)
        if( username.contains("_RememberMe") ) {
            username = username.replace("_RememberMe", "");
            Users user = userMapper.login(username);
            if( user == null ) return null;
            httpSession.setAttribute("user", user); 
            CustomUser customUser = new CustomUser(user, true, "123456");
            return customUser;
        }

        // 일반 로그인
        Users user = userMapper.login(username);
        if( user == null ) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다." + username);
        }

        // 🔐 CustomUser ➡ UserDetails
        CustomUser customUser = new CustomUser(user);
        return customUser;
    }
    
}
