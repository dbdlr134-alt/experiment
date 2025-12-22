package com.mnu.jpstudy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ProfileRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String newNickname;
    private String newProfileImageUrl; // 파일 업로드 후 경로 저장

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING, APPROVED, REJECTED

    private LocalDateTime requestDate = LocalDateTime.now();

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }
}