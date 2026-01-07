package com.mnu.blog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @Column(nullable = false)
    private String originalName; // 사용자가 올린 파일명

    @Column(nullable = false)
    private String savedName;    // 서버에 저장된 파일명 (UUID 등)

    @Column(nullable = false)
    private String filePath;     // 저장 경로

    @CreationTimestamp
    private Timestamp createDate;
}