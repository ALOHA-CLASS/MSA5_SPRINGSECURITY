package com.aloha.security5.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.aloha.security5.dto.Users;

@Mapper
public interface UserMapper {
    
    // 🔐 로그인 (사용자 인증)
    public Users login(String username);

}
