package com.aloha.kakao.service;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.aloha.kakao.dto.CustomUser;
import com.aloha.kakao.dto.UserSocial;
import com.aloha.kakao.dto.Users;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAuthServiceImpl implements OAuthService {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserService userService;

    @Value("${default.password}")
    private String defaultPassword;
    
    
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth2UserService를 사용하여 사용자 정보를 로드합니다.
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 로그인 서비스의 이름을 가져옵니다. (예: "google", "kakao" 등)
        String serviceName = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 정보에서 사용자 이름 속성의 이름을 가져옵니다. (예: "name", "email" 등)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                                                  .getUserInfoEndpoint().getUserNameAttributeName();

        // 사용자 정보에서 속성들을 가져옵니다. (예: 사용자의 이메일, 이름 등)
        Map<String, Object> attributes = oAuth2User.getAttributes();


        log.info("userNameAttributeName : " + userNameAttributeName);
        log.info("attributes : " + attributes);
		
        String id;
		String email;
        String name;
        String profileImgUrl;
		
        // 카카오 로그인
		if ("kakao".equals(serviceName)) {
            log.info("::::: 카카오 로그인 :::::");
			Map<String, Object> profile = (Map<String, Object>) attributes.get("kakao_account");
            id = attributes.get("id").toString();
			email = (String) profile.get("email");
			name = (String) ((Map<String, Object>) profile.get("profile")).get("nickname");
			profileImgUrl = (String) ((Map<String, Object>) profile.get("profile")).get("profile_image_url");

            log.info("profile : " + profile);
            log.info("id : " + id);
            log.info("email : " + email);
            log.info("name : " + name);
            log.info("profileImgUrl : " + profileImgUrl);
		} else {
			throw new OAuth2AuthenticationException("허용되지 않는 인증입니다.");
		}


        // 💎 액세스 토큰
        String accessToken = userRequest.getAccessToken().getTokenValue();
        log.info("accessToken : " + accessToken);

        httpSession.setAttribute("id",  id);
        httpSession.setAttribute("socialType",  serviceName);
        httpSession.setAttribute("access_token", accessToken);
        


        // 소셜 회원 가입 여부 조회
        UserSocial userSocial = UserSocial.builder()
                                            .socialType(serviceName)
                                            .clientId(id)
                                            .build();
        UserSocial social = null;
        try {
            social = userService.selectSocial(userSocial);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Users user = null;
        if( social != null && social.getUserId() != null ) {
            try {
                user = userService.select(social.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String socialUserId = serviceName + "_" + id;
        // 회원 가입
        if( user == null ) {
            user = new Users();
            user.setUserId(socialUserId);
            user.setUserPw(defaultPassword);
            user.setEmail(email);
            user.setName(name);
            user.setProfile(profileImgUrl);
            try {
                int result = userService.join(user);
                if( result > 0 ) log.info("소셜 회원 가입 성공!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        // 이미 가입한 회원이면 수정
        else {
            user = new Users();
            user.setUserId(socialUserId);
            user.setUserPw(defaultPassword);
            user.setEmail(email);
            user.setName(name);
            user.setProfile(profileImgUrl);
            try {
                userService.update(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 소셜 정보 갱신
        if( social == null ) {
            log.info("소셜 정보 최초 등록");

            userSocial.setUserId( socialUserId );
            userSocial.setAccessToken(accessToken);
            userSocial.setRefreshToken("");

            try {
                int result = userService.insertSocial(userSocial);
                if( result > 0 ) log.info("소셜 정보 등록 성공!"); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if( user != null ) {
            log.info("소셜 정보 갱신!");
            userSocial.setUserId( socialUserId );
            userSocial.setAccessToken(accessToken);
            userSocial.setRefreshToken("");

            try {
                int result = userService.updateSocial(userSocial);
                if( result > 0 ) log.info("소셜 정보 갱신 성공!"); 
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        user.setUserPw(null);
        user.setUserPwCheck(null);
        httpSession.setAttribute("user", user);


		return new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
				, oAuth2User.getAttributes()
				, userNameAttributeName
		);
    }
    
}
