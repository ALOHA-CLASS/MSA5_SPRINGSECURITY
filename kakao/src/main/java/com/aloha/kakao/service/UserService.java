package com.aloha.kakao.service;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.aloha.kakao.dto.UserAuth;
import com.aloha.kakao.dto.UserSocial;
import com.aloha.kakao.dto.Users;

public interface UserService {

    // 🔐 로그인 (사용자 인증)
    public void login(Users user, HttpServletRequest request);

    // 👩‍💼 회원 조회
    public Users select(String userId) throws Exception;

    // ✅ 회원 가입
    public int join(Users user) throws Exception;

    // ✅ 회원수정
    public int update(Users user) throws Exception;

    // 👩‍💼 회원 권한 등록
    public int insertAuth(UserAuth userAuth) throws Exception;

    // 👩‍💻 소셜 회원 가입
    public int insertSocial(UserSocial userSocial) throws Exception;
    
    // 👩‍💻 소셜 회원 조회
    public UserSocial selectSocial(UserSocial userSocial) throws Exception;

    // 👩‍💻 소셜 회원 정보 갱신
    public int updateSocial(UserSocial userSocial) throws Exception;

    // Principal 추출해서 Users 로 세팅
    public Users principalToUser(Principal principal) throws Exception;
    
}
