package com.aloha.kakao.service;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.aloha.kakao.dto.CustomUser;
import com.aloha.kakao.dto.UserAuth;
import com.aloha.kakao.dto.UserSocial;
import com.aloha.kakao.dto.Users;
import com.aloha.kakao.mapper.UserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void login(Users user, HttpServletRequest request) {
        // 💍 토큰 생성
        String username = user.getUserId();         // 아이디
        String password = user.getUserPwCheck();    // 암호화되지 않은 비밀번호
        UsernamePasswordAuthenticationToken token 
            = new UsernamePasswordAuthenticationToken(username, password);
        
        // 토큰에 요청 정보 등록
        // token.setDetails( new WebAuthenticationDetails(request) );

        // 토큰을 이용하여 인증
        Authentication authentication = authenticationManager.authenticate(token);

        // 인증된 사용자 확인
        CustomUser loginUser = (CustomUser) authentication.getPrincipal();
        log.info("인증된 사용자 아이디 : " + loginUser.getUsername());

        // 시큐리티 컨텍스트에 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * ✅ 회원 가입
     * 1. 비밀번호 암호화
     * 2. 회원 등록
     * 3. 기본 권한 등록
     */
    @Override
    public int join(Users user) throws Exception {
        // 비밀번호 암호화
        String userPw = user.getUserPw();
        String encodedUserPw = passwordEncoder.encode(userPw);  // 🔒 암호화
        user.setUserPw(encodedUserPw);

        // 회원 등록
        int result = userMapper.join(user);

        if( result > 0 ) {
            // 회원 기본 권한 등록
            UserAuth userAuth = new UserAuth();
            userAuth.setUserId( user.getUserId() );
            userAuth.setAuth("ROLE_USER");
            result += userMapper.insertAuth(userAuth);
        }
        return result;
    }

    @Override
    public int insertAuth(UserAuth userAuth) throws Exception {
        int result = userMapper.insertAuth(userAuth);
        return result;
    }
    
    @Override
    public int insertSocial(UserSocial userSocial) throws Exception {
        int result = userMapper.insertSocial(userSocial);
        return result;
    }

    @Override
    public UserSocial selectSocial(UserSocial userSocial) throws Exception {
        UserSocial social = userMapper.selectSocial(userSocial);
        return social;
    }

    @Override
    public Users select(String userId) throws Exception {
        Users user = userMapper.select(userId);
        return user;
    }

    @Override
    public int updateSocial(UserSocial userSocial) throws Exception {
        int result = userMapper.updateSocial(userSocial);
        return result;
    }

    @Override
    public int update(Users user) throws Exception {
        int result = userMapper.update(user);
        return result;
    }

    /**
     * Principal 
     * 1. Principal 이 OAuth2AuthenticationToken 이면, 
     */
    @Override
    public Users principalToUser(Principal principal) throws Exception {

        log.info("principal : " + principal);
        if( principal instanceof OAuth2AuthenticationToken ) {
            log.info("OAuth 로 인증...");
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) principal).getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.valueToTree( attributes );
            
            String profileImg = node.get("properties").get("profile_image").toString();
            log.info("profileImg : " + profileImg);

        }

        return null;
    }   
    
}
