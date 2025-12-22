package com.mnu.jpstudy.repository;
import com.mnu.jpstudy.entity.Notification;
import com.mnu.jpstudy.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 안 읽은 알림만 최신순 조회
    List<Notification> findByMemberAndIsReadFalseOrderByCreatedAtDesc(Member member);
}