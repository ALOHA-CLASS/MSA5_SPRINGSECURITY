package com.aloha.kakao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class CommonConfig {

    // @Autowired
    // private DataSource dataSource;

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

    // /**
    //  * 🍃 암호화 방식 빈 등록
    //  * @return
    //  */
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

    // /**
    // * 🍃 자동 로그인 저장소 빈 등록
    // * ✅ 데이터 소스
    // * ⭐ persistent_logins 테이블 생성
    // * 
    //         create table persistent_logins (
    //             username varchar(64) not null
    //             , series varchar(64) primary key
    //             , token varchar(64) not null
    //             , last_used timestamp not null
    //         );
    //     * @return
    // */
    // @Bean
    // public PersistentTokenRepository tokenRepository() {
    //     // JdbcTokenRepositoryImpl : 토큰 저장 데이터 베이스를 등록하는 객체
    //     JdbcTokenRepositoryImpl repositoryImpl = new JdbcTokenRepositoryImpl();

    //     // persistent_logins (자동 로그인) 테이블 생성 
    //     // repositoryImpl.setCreateTableOnStartup(true);

    //     // 토큰 저장소를 사용하는 데이터 소스 지정
    //     repositoryImpl.setDataSource(dataSource);   

    //     return repositoryImpl;
    // }
    
}
