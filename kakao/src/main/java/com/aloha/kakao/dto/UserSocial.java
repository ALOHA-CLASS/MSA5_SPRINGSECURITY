package com.aloha.kakao.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSocial {

    private int no;
    private String clientId;
    private String socialType;
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Date expiresAt;
    
    
}
