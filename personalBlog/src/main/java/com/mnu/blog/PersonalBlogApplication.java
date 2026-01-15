package com.mnu.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // 추가
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 추가

@SpringBootApplication
public class PersonalBlogApplication {

    // ★ [추가] 암호화 빈을 여기서 먼저 생성하여 순환 참조 방지
    @Bean
    public BCryptPasswordEncoder encodePWD() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(PersonalBlogApplication.class, args);
    }
}