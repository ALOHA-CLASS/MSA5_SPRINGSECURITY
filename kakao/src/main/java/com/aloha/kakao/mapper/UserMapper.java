package com.aloha.kakao.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.aloha.kakao.dto.UserAuth;
import com.aloha.kakao.dto.UserSocial;
import com.aloha.kakao.dto.Users;

@Mapper
public interface UserMapper {
    
    // 🔐 로그인 (사용자 인증)
    public Users login(String username);

    // 👩‍💼 회원 조회
    public Users select(String userId) throws Exception;

    // ✅ 회원가입
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
    
}
