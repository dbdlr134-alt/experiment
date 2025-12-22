package com.mnu.jpstudy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class UserTheme {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String themeName; // 테마 식별자 (DARK, LIGHT, PINK...)
    private boolean isEquipped; // 현재 착용 중인지 여부

    private LocalDateTime purchaseDate = LocalDateTime.now();
}