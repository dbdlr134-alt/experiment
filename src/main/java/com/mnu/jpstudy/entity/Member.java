package com.mnu.jpstudy.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data // Getter, Setter 자동 생성 (Lombok)
@Entity // 이 클래스는 DB 테이블과 연결된다는 표시
@Table(name = "member") // 실제 DB 테이블 이름 지정
public class Member {

    @Id // 기본키 (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment (자동 번호 증가)
    private Long memberId;

    @Column(unique = true, nullable = false, length = 50)
    private String username; // 로그인 ID

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    private String role; // ROLE_USER, ROLE_ADMIN

    @CreationTimestamp // INSERT 시 현재 시간 자동 저장
    @Column(name = "created_at", updatable = false) // updatable=false: 수정 불가
    private LocalDateTime createdAt;
}