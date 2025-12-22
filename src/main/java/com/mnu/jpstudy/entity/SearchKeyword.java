package com.mnu.jpstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "search_keyword")
public class SearchKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long keywordId;

    @Column(nullable = false, unique = true, length = 100)
    private String keyword;

    @Column(name = "search_count")
    private int searchCount = 1;

    @Column(name = "last_searched_at")
    private LocalDateTime lastSearchedAt;

    @PrePersist
    @PreUpdate
    public void updateTime() {
        this.lastSearchedAt = LocalDateTime.now();
    }
}