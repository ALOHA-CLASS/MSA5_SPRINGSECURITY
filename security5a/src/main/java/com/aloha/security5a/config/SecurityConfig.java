package com.aloha.security5a.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration                  // 스프링 빈 설정 클래스로 지정
@EnableWebSecurity              // 스프링 시큐리티 설정 빈으로 등록
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;        // 비밀번호 암호화 객체

    @Autowired
    private DataSource dataSource;          // application.properites 에 정의한 데이터 소스를 가져오는 객체


    /**
     * 🛡🔐 스프링 시큐리티 설정 메소드
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin();

        http.rememberMe(me -> me
                .key("joeun")
                // DataSource 가 등록된 PersistentRepository 토큰정보 객체 
                .tokenRepository(tokenRepository())
                // 토큰 유효기간 지정 : 7일 (초 단위)
                .tokenValiditySeconds(60 * 60 * 24 * 7))                    
            ;
    }


    // PersistentRepository 토큰정보 객체 - 빈 등록
    @Bean
    public PersistentTokenRepository tokenRepository() {
        // JdbcTokenRepositoryImpl : 토큰 저장 데이터 베이스를 등록하는 객체
        JdbcTokenRepositoryImpl repositoryImpl = new JdbcTokenRepositoryImpl(); 
        repositoryImpl.setCreateTableOnStartup(true);
        repositoryImpl.setDataSource(dataSource);   // 토큰 저장소를 사용하는 데이터 소스 지정
        return repositoryImpl;
    }

    
		
    /**
     * 👮‍♂️🔐사용자 인증 관리 메소드
     * ⭐ 인메모리 인증 방식
     */
    // @Override
    // protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //     // AuthenticationManagerBuilder : 인증 관리 객체
    //     auth.inMemoryAuthentication()               
    //         // .withUser("아이디").password("비밀번호").roles("권한")
    //         // passwordEncoder.encode("비밀번호")     :   비밀번호 암호화
    //         // BCryptPasswordEncoder 사용
    //         .withUser("user").password(passwordEncoder.encode("123456")).roles("USER")
    //         .and()
    //         .withUser("admin").password(passwordEncoder.encode("123456")).roles("ADMIN")
    //         ;
    //         // NoOpPasswordEncoder 사용
    //         // .withUser("user").password("123456").roles("USER")
    //         // .and()
    //         // .withUser("admin").password("123456").roles("ADMIN")
    //         // ;
    // }


    /**
     * 👮‍♂️🔐사용자 인증 관리 메소드
     * ⭐JDBC 인증 방식
     * ✅ 데이터 소스 
     * ✅ SQL 쿼리 등록
     *      ⭐ 사용자 인증 쿼리
     *      ⭐ 사용자 권한 쿼리
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // ⭐ 사용자 인증 쿼리
        String sql1 = " SELECT user_id as username, user_pw as password, enabled "
                    + " FROM user "
                    + " WHERE user_id = ? ";

        // ⭐ 사용자 권한 쿼리
        String sql2 = " SELECT user_id as username, auth " 
                    + " FROM user_auth "
                    + " WHERE user_id = ? ";

        auth.jdbcAuthentication()
            // 데이터 소스 등록
            .dataSource( dataSource )
            // 인증 쿼리    (아이디/비밀번호/활성여부)
            .usersByUsernameQuery(sql1)
            // 인가 쿼리    (아이디/권한)
            .authoritiesByUsernameQuery(sql2)
            // 비밀번호 암호화 방식 지정 - BCryptPasswordEncoder 또는 NoOpPasswordEncoder
            .passwordEncoder( passwordEncoder );

    }

}