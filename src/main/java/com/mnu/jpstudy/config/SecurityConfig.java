package com.mnu.jpstudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호 암호화 도구 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**").permitAll() // 누구나 접근 가능
                .anyRequest().authenticated() // 그 외는 로그인해야 접근 가능
            )
            // 2. 로그인 설정
            .formLogin(form -> form
                .loginPage("/auth/login")       // 로그인 페이지 URL
                .loginProcessingUrl("/auth/login") // 로그인 Form Action URL (Security가 가로채서 처리)
                .defaultSuccessUrl("/", true)   // 성공 시 이동할 메인 페이지
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            // 3. 로그아웃 설정
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
            );

        return http.build();
    }
}