package com.aloha.kakao.dto;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
public class CustomUser implements UserDetails {

    // 사용자 DTO
    private Users user;

    // 자동 로그인 
    // - 자동 로그인 시, 인증된 토큰만 확인하지 비밀번호는 확인할 수 없다.
    //   따라서 자동 로그인 시도인 경우는 기본 비밀번호롤 인증이 되도록 세팅한다.
    private boolean rememberMe = false;

    private String defaultPassword;

    public CustomUser(Users user) {
        this.user = user;
    }

    public CustomUser(Users user, boolean rememberMe, String defaultPassword) {
        this.user = user;
        this.rememberMe = rememberMe;
        this.defaultPassword = defaultPassword;
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
        if( this.rememberMe ) {
            log.info("자동 로그인 시, 기본 비번 가져옴");
            log.info("기본 비밀번호 확인 : " + this.defaultPassword);
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.encode(this.defaultPassword);
            // return this.defaultPassword;
        }

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
