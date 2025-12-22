package com.mnu.jpstudy.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class PointLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int amount; // 변동량 (50, -100 등)
    private String description; // 사유 (게시글 작성, 테마 구매 등)

    @CreationTimestamp
    private LocalDateTime createdAt;

    public PointLog(Member member, int amount, String description) {
        this.member = member;
        this.amount = amount;
        this.description = description;
    }
}