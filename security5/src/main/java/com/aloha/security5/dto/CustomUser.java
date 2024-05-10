package com.aloha.security5.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CustomUser implements UserDetails {

    // 사용자 DTO
    private Users user;

    public CustomUser(Users user) {
        this.user = user;
    }

    /**
     * 🔐 권한 정보 메소드
     * ✅ UserDetails 를 CustomUser 로 구현하여,
     *    Spring Security 의 User 대신 사용자 정의 인증 객체(CustomUser)로 적용
     * ⚠ CustomUser 적용 시, 권한을 사용할 때는 'ROLE_' 붙여서 사용해야한다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // List<SimpleGrantedAuthority> authList = new ArrayList<>();
        // List<UserAuth> userAuthList = user.getAuthList();
        // for (int i = 0; i < userAuthList.size(); i++) {
        //     UserAuth userAuth = userAuthList.get(i);
        //     String auth = userAuth.getAuth();
        //     SimpleGrantedAuthority authority = new SimpleGrantedAuthority(auth);
        //     authList.add(authority);
        // }
        // return authList;

        return user.getAuthList().stream()
                                 .map( (auth) -> new SimpleGrantedAuthority(auth.getAuth()) )
                                 .collect(Collectors.toList());

    }

    @Override
    public String getPassword() {
        return user.getUserPw();
        // return "PROTECTED";
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled() == 0 ? false : true;
    }
    
}
