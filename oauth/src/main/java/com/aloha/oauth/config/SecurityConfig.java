package com.aloha.oauth.config;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration                  // 스프링 빈 설정 클래스로 지정
@EnableWebSecurity              // 스프링 시큐리티 설정 빈으로 등록
public class SecurityConfig  {

    @Autowired
    private DataSource dataSource;      // application.properties 에 정의한 DB 정보

    /**
     * 🔐 스프링 시큐리티 설정 메소드
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // ✅ 인가 설정
        http.authorizeRequests(requests -> requests
                                            .antMatchers("/admin", "/admin/**").hasRole("ADMIN")
                                            .antMatchers("/user", "/user/**").hasAnyRole("USER", "ADMIN")
                                            .antMatchers("/css/**", "/js/**", "/img/**").permitAll()        
                                            .antMatchers("/**").permitAll()
                                            .anyRequest().permitAll()
                                            )
            ;

        // ✅ 로그인 설정
        // 폼 기반 로그인 활성화
        // - 기본 설정      : 시큐리티 제공 로그인 페이지
        http.formLogin(withDefaults());

        http.oauth2Login(login -> login
                .loginPage("/login")
                // .userInfoEndpoint()
                // .userService(customOAuth2UserService)
                )	
            ;


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
    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager userDetailsManager 
                = new JdbcUserDetailsManager(dataSource);

        // 사용자 인증 쿼리
        String sql1 = " SELECT user_id as username, user_pw as password, enabled "
                    + " FROM user "
                    + " WHERE user_id = ? "
                    ;
        // 사용자 권한 쿼리
        String sql2 = " SELECT user_id as username, auth "
                    + " FROM user_auth "
                    + " WHERE user_id = ? "
                    ;
        userDetailsManager.setUsersByUsernameQuery(sql1);
        userDetailsManager.setAuthoritiesByUsernameQuery(sql2);
        return userDetailsManager;
    }


    /**
     * 🍃 AuthenticationManager 빈 등록
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(
                                AuthenticationConfiguration authenticationConfiguration) 
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 🍃 암호화 방식 빈 등록
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
