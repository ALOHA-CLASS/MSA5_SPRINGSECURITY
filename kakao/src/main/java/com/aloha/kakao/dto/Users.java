package com.aloha.kakao.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Users {
    
    private int userNo;
    private String userId;
    private String userPw;
    private String userPwCheck;
    private String name;
    private String email;
    private String profile;
    private Date regDate;
    private Date updDate;
    private int enabled;

    // 권한 목록
    List<UserAuth> authList;
    
}
