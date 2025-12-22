package com.mnu.jpstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor // 기본 생성자 필수
@Table(name = "member") // DB 테이블명 명시
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 로그인 ID

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false, unique = true, length = 50)
    private String nickname; // 별명

    @Column(length = 20)
    private String role; // ROLE_USER, ROLE_ADMIN (기본값 처리는 DB 혹은 서비스단에서)

    @CreationTimestamp // INSERT 시 시간 자동 저장
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 회원가입용 생성자 편의 메서드
    public Member(String username, String password, String nickname, String role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }
}