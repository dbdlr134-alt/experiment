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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 메인, 로그인, 회원가입, 정적 리소스는 누구나 접근 가능
                .requestMatchers("/", "/index", "/auth/**", "/css/**", "/js/**", "/images/**").permitAll()
                // 단어 검색, 게시판 목록도 로그인 없이 허용
                .requestMatchers("/word/**", "/board/**").permitAll()
                // 그 외에는 로그인 필요
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")       // 로그인 페이지 경로
                .loginProcessingUrl("/auth/login") // 로그인 폼 action 경로
                .defaultSuccessUrl("/", true)   // 성공 시 메인으로
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
            );
            
        // 개발 중에는 CSRF 보안이 방해될 수 있으므로 필요 시 비활성화 (선택)
        // http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    // 👇 [중요] 이 부분이 없어서 에러가 난 것입니다!
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 도구 등록
    }
}