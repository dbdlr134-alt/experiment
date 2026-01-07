package com.mnu.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.mnu.blog.config.auth.PrincipalDetailService;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor; // ★ import 추가

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // ★ 생성자 주입을 위해 추가
public class SecurityConfig {

    // ★ 자동 로그인을 위해 사용자 정보 가져오는 서비스가 필요함
    private final PrincipalDetailService principalDetailService;

    @Bean
    public BCryptPasswordEncoder encodePWD() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/", "/auth/**", "/js/**", "/css/**", "/images/**", "/board/**", "/dummy/**") 
                .permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/loginForm")
                .loginProcessingUrl("/auth/loginProc")
                .defaultSuccessUrl("/")
                .failureUrl("/auth/loginForm?error")
            )
            // ★ [쿠키 기능 추가] 자동 로그인 (Remember-Me)
            .rememberMe(remember -> remember
                .key("my-unique-secret-key") // 쿠키 암호화에 쓸 나만의 키 (아무거나 가능)
                .tokenValiditySeconds(60 * 60 * 24 * 7) // 쿠키 유지 시간 (7일 설정)
                .userDetailsService(principalDetailService) // 사용자 정보 찾을 때 쓸 서비스
                .rememberMeParameter("remember-me") // HTML 체크박스의 name 값
            );
            
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}