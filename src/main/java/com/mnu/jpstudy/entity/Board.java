package com.mnu.jpstudy.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    private int hitCount; // 조회수
    private int likeCount; // 좋아요 수

    // [중요] 작성자는 Member 테이블과 연결 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "writer_id") // DB 컬럼명
    private Member writer;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // UPDATE 시 현재 시간 자동 갱신
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}