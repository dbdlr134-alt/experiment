package com.mnu.jpstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "dictionary_crawl_log")
public class DictionaryCrawlLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crawl_id")
    private Long crawlId;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(name = "result_count")
    private int resultCount = 0;

    @Column(length = 20)
    private String status; // SUCCESS, FAIL

    private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}