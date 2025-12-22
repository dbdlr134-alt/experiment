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
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 수신자

    @Column(nullable = false, length = 50)
    private String type; // NEW_REPLY, NEW_LIKE 등

    @Column(name = "reference_id")
    private Long referenceId; // 관련된 게시글/댓글 ID

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read")
    private boolean isRead = false; // 읽음 여부

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}