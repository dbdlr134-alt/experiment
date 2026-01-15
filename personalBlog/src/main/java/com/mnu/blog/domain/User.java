package com.mnu.blog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String username; // 로그인 ID

    @Column(nullable = false, length = 100)
    private String password; // 암호화된 비번

    @Column(nullable = false, length = 50, unique = true)
    private String nickname;

    // ★ [추가] 소셜 로그인 정보 (provider: "kakao", providerId: "12345...")
    private String provider;
    private String providerId;

    @Column(nullable = false, length = 20)
    private String phoneNumber; // 전화번호 추가

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, USER (Enum 필요)

    @CreationTimestamp
    private Timestamp createDate;
    
 // ★ [추가] 프로필 사진 경로
    @Column(length = 300) // 구글 이미지 URL이 길 수 있어서 넉넉하게
    private String profileUrl;
    
    @Column(columnDefinition = "boolean default false")
    private boolean tempPw;
}