package com.mnu.blog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
// 한 유저가 한 게시글에 좋아요는 한 번만 가능 (중복 방지)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name="love_uk",
            columnNames = {"postId", "userId"}
        )
    }
)
public class Love {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}