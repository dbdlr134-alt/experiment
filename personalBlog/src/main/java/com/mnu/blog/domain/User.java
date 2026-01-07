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

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String phoneNumber; // 전화번호 추가

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, USER (Enum 필요)

    @CreationTimestamp
    private Timestamp createDate;
}