package com.mnu.jpstudy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnu.jpstudy.entity.Member;
import com.mnu.jpstudy.entity.PointLog;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {
    List<PointLog> findByMember(Member member);
}