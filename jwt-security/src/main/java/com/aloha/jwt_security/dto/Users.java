package com.aloha.jwt_security.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Users {
    
    private int no;
    private String userId;
    private String userPw;
    private String userPwCheck;     // 비밀번호 확인
    private String name;
    private String email;
    private Date regDate;
    private Date updDate;
    private int enabled;            // 휴면여부
    
    // 권한 목록
    List<UserAuth> authList;

    public Users() {
        
    }
    
    public Users(Users user) {
        this.no = user.getNo();
        this.userId = user.getUserId();
        this.userPw = user.getUserPw();
        this.name = user.getName();
        this.email = user.getEmail();
        this.authList = user.getAuthList();
    }
}