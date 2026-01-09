package com.mnu.blog.config;

import com.mnu.blog.config.auth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration 
@EnableWebSecurity 
@EnableMethodSecurity(securedEnabled = true) 
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePWD() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // 1. 정적 자원 및 메인 페이지, 인증 페이지 (모두 허용)
                .requestMatchers("/", "/auth/**", "/js/**", "/css/**", "/images/**", "/dummy/**", "/api/**").permitAll()
                
                // 3. 관리자 페이지 (ADMIN만 허용)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // 4. 그 외 모든 요청은 로그인해야 접근 가능
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/loginForm")       
                .loginProcessingUrl("/auth/loginProc") 
                .defaultSuccessUrl("/")             
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/loginForm")       
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(principalOauth2UserService) 
                )
                .defaultSuccessUrl("/")             
            );

        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}