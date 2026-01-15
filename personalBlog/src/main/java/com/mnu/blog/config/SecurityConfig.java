package com.mnu.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import com.mnu.blog.config.auth.LoginSuccessHandler; // import 추가
import com.mnu.blog.config.auth.PrincipalOauth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

 private final PrincipalOauth2UserService principalOauth2UserService;
 private final LoginSuccessHandler loginSuccessHandler; // ★ [추가] 핸들러 주입

 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     http
         .csrf(csrf -> csrf.disable())
         .authorizeHttpRequests(authorize -> authorize
             .requestMatchers("/", "/auth/**", "/js/**", "/css/**", "/images/**", "/dummy/**", "/api/**").permitAll()
             .anyRequest().authenticated()
         )
         .formLogin(form -> form
             .loginPage("/auth/loginForm")
             .loginProcessingUrl("/auth/loginProc")
             .successHandler(loginSuccessHandler) // ★ [수정] defaultSuccessUrl 대신 이거 사용!
             .failureUrl("/auth/loginForm?error")
         )
         .oauth2Login(oauth2 -> oauth2
             .loginPage("/auth/loginForm")
             .userInfoEndpoint(userInfo -> userInfo
                 .userService(principalOauth2UserService)
             )
         );
         
     return http.build();
 }
}