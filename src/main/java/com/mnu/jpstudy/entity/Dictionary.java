package com.mnu.jpstudy.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dictionary")
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wordId;

    @Column(nullable = false, length = 100)
    private String kanji; // 한자 표제어 (猫)

    @Column(length = 100)
    private String reading; // 요미가나 (ねこ)

    @Lob // 대용량 텍스트 (TEXT 타입 매핑)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String meanings; // 뜻

    @Lob
    @Column(columnDefinition = "TEXT")
    private String example; // 예문

    private String level; // JLPT 등급 (N1, N2...)

    @CreationTimestamp
    @Column(name = "crawled_at")
    private LocalDateTime crawledAt;
}