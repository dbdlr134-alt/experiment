package com.mnu.jpstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "dictionary", indexes = {
    @Index(name = "idx_kanji", columnList = "kanji"),
    @Index(name = "idx_reading", columnList = "reading")
})
public class JapaneseWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long wordId;

    @Column(nullable = false, length = 100)
    private String kanji;   // 표제어

    @Column(length = 100)
    private String reading; // 읽는 법 (요미가나)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String meanings; // 뜻

    @Column(columnDefinition = "TEXT")
    private String example;  // 예문

    @Column(length = 10)
    private String level;    // JLPT 등급

    @Column(name = "crawled_at")
    private LocalDateTime crawledAt;

    @PrePersist
    public void prePersist() {
        this.crawledAt = LocalDateTime.now();
    }
}