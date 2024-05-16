package com.aloha.kakao.controller;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.aloha.kakao.dto.Users;
import com.aloha.kakao.service.UserService;

import lombok.extern.slf4j.Slf4j;




@Slf4j
@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping({"/", ""})
    public String home(Principal principal
                    , @AuthenticationPrincipal OAuth2User oauth2User
                    , HttpServletRequest request) throws Exception {
        log.info("::::: 메인 화면 :::::");
        log.info(":::::::::::::::::::: principal ::::::::::::::::::::");
        log.info("principal : " + principal);
        
        log.info("\n");
        log.info(":::::::::::::::::::: oauth2User ::::::::::::::::::::");
        log.info("oauth2User : " + oauth2User);
        log.info("\n");
        if( oauth2User != null ) {
            Map<String, Object> properties = ((Map<String, Object>) oauth2User.getAttributes().get("properties"));
            String profileImg = (String) properties.get("profile_image");
            String nickname = (String) properties.get("nickname");
            String email = (String) properties.get("email");
            log.info("profileImg : " + profileImg);
            log.info("nickname : " + nickname);
            log.info("email : " + email);
        }

        // HttpSession session = request.getSession();
		// String id = (String)session.getAttribute("id");
		// String accessToken = (String)session.getAttribute("access_token");
        // log.info("id : " + id);
        // log.info("accessToken : " + accessToken);

        // //
        // Users user = (Users) session.getAttribute("user");
        // log.info("user : " + user);

        return "index";
    }


    /**
     * 로그인 화면
     * - 🍪 remember-id : 저장된 아이디
     * @return
     */
    @GetMapping("/login")
    public String login(
            @CookieValue(value = "remember-id", required = false) Cookie cookie
           ,Model model
           ) {
        // @CookieValue(value="쿠키명", required="필수 여부")
        // - required=true (default)    : 쿠키를 필수로 가져옴 ➡ 쿠키가 없으면 에러
        // - required=false             : 쿠키 필수 ❌ ➡ 쿠키가 없어도 에러 ❌
        log.info("로그인 페이지...");

        String userId = "";                 // 저장된 아이디
        boolean rememberId = false;         // 아이디 저장 체크 여부 ( ✅, 🟩 )

        if( cookie != null ) {
            log.info("CookieName : " + cookie.getName());
            log.info("CookieValue : " + cookie.getValue());
            userId = cookie.getValue();
            rememberId = true;
        }
 
        model.addAttribute("userId", userId);
        model.addAttribute("rememberId", rememberId);
        return "/login";
    }

    @GetMapping("/join")
    public String join() {
        
        return "join";
    }

    @PostMapping("/join")
    public String joinPro(Users user, HttpServletRequest request) throws Exception {
        int result = userService.join(user);
        if( result > 0 ) {
            userService.login(user, request);
            return "redirect:/";
        }
        return "redirect:/join?error";
    }


    
    
    
}
