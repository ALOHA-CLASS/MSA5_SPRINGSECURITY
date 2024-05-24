package com.aloha.kakao.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.aloha.kakao.security.CustomLogoutHandler;
import com.aloha.kakao.security.CustomOAuthSuccessHandler;
import com.aloha.kakao.security.LoginSuccessHandler;
import com.aloha.kakao.service.CustomRememberMeService;
import com.aloha.kakao.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CustomLogoutHandler customLogoutHandler;
    
    @Autowired
    private CustomOAuthSuccessHandler authSuccessHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomRememberMeService rememberMeService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    // 스프링 시큐리티 설정 메소드
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // ✅ 인가 설정
        http.authorizeRequests(requests -> requests
                                            .antMatchers("/admin", "/admin/**").hasRole("ADMIN")
                                            // .antMatchers("/user", "/user/**").hasAnyRole("USER", "ADMIN")
                                            .antMatchers("/css/**", "/js/**", "/img/**").permitAll()        
                                            .antMatchers("/**").permitAll()
                                            .anyRequest().permitAll()
                                            )
            ;


        // ✅ 로그인 설정
        // 폼 기반 로그인 활성화
        // - 기본 설정      : 시큐리티 제공 로그인 페이지
        http.formLogin(login -> login.loginPage("/login")
                                     .loginProcessingUrl("/login")
                                     .successHandler(loginSuccessHandler)
                    );

        // 사용자 정의 인증 설정
        http.userDetailsService(customUserDetailsService);


        // OAuth 로그인 설정
        http.oauth2Login(login -> login
                .loginPage("/login")
                .successHandler(authSuccessHandler)
                // .userInfoEndpoint()
                // .userService(customOAuth2UserService)
                )	
            ;


        // 🔐👩‍💼 자동 로그인 설정
        // key()                    : 자동 로그인에서 토큰 생성/검증에 사용되는 식별키
        // tokenRepository()        : 토큰 저장할 저장소 지정 (데이터소스 포함함 저장소객체)
        //                            🎁 persistent_logins (자동로그인 테이블)
        // tokenValiditySeconds()   : 토큰 유효시간 설정 (예시 : 7일)

        // 보류 : // .rememberMeServices(rememberMeService)
        http.rememberMe(me -> me.key("aloha")
                                .rememberMeServices(rememberMeService)
                                .tokenRepository(tokenRepository())
                                .tokenValiditySeconds(60 * 60 * 24 * 7));

        // 로그아웃 설정
        http.logout(logout -> logout.addLogoutHandler(customLogoutHandler)

                                    // .invalidateHttpSession(true) // HttpSession 무효화 여부 설정
                                    .deleteCookies("JSESSIONID", "remember-me")
                                    );


        return http.build();
    }


    /**
     * 👮‍♂️🔐 사용자 인증 관리 빈 등록 메소드
     * JDBC 인증 방식
     * ✅ 데이터 소스 (URL, ID, PW) - application.properties
     * ✅ SQL 쿼리 등록         
     * ⭐ 사용자 인증 쿼리
     * ⭐ 사용자 권한 쿼리
     * @return
     */
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     JdbcUserDetailsManager userDetailsManager 
    //             = new JdbcUserDetailsManager(dataSource);

    //     // 사용자 인증 쿼리
    //     String sql1 = " SELECT user_id as username, user_pw as password, enabled "
    //                 + " FROM user "
    //                 + " WHERE user_id = ? "
    //                 ;
    //     // 사용자 권한 쿼리
    //     String sql2 = " SELECT user_id as username, auth "
    //                 + " FROM user_auth "
    //                 + " WHERE user_id = ? "
    //                 ;
    //     userDetailsManager.setUsersByUsernameQuery(sql1);
    //     userDetailsManager.setAuthoritiesByUsernameQuery(sql2);
    //     return userDetailsManager;
    // }


    // /**
    //  * 🍃 AuthenticationManager 빈 등록
    //  * @param authenticationConfiguration
    //  * @return
    //  * @throws Exception
    //  */
    // @Bean
    // public AuthenticationManager authenticationManager(
    //                             AuthenticationConfiguration authenticationConfiguration) 
    //         throws Exception {
    //     return authenticationConfiguration.getAuthenticationManager();
    // }

    /**
     * 🍃 암호화 방식 빈 등록
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
    * 🍃 자동 로그인 저장소 빈 등록
    * ✅ 데이터 소스
    * ⭐ persistent_logins 테이블 생성
    * 
            create table persistent_logins (
                username varchar(64) not null
                , series varchar(64) primary key
                , token varchar(64) not null
                , last_used timestamp not null
            );
        * @return
    */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        // JdbcTokenRepositoryImpl : 토큰 저장 데이터 베이스를 등록하는 객체
        JdbcTokenRepositoryImpl repositoryImpl = new JdbcTokenRepositoryImpl();

        // persistent_logins (자동 로그인) 테이블 생성 
        // repositoryImpl.setCreateTableOnStartup(true);

        // 토큰 저장소를 사용하는 데이터 소스 지정
        repositoryImpl.setDataSource(dataSource);   

        return repositoryImpl;
    }


}
