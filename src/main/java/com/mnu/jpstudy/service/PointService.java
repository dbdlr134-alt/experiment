package com.mnu.jpstudy.service;

import org.springframework.stereotype.Service;

import com.mnu.jpstudy.entity.Member;
import com.mnu.jpstudy.entity.PointLog;
import com.mnu.jpstudy.repository.MemberRepository;
import com.mnu.jpstudy.repository.PointLogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointLogRepository pointLogRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void changePoint(Long memberId, int amount, String description) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        
        // 로그 기록
        PointLog log = new PointLog(member, amount, description);
        pointLogRepository.save(log);

        // 실제 멤버 테이블에 포인트 필드가 있다면 여기서 업데이트 (Member 엔티티에 point 필드 필요)
        // member.setPoint(member.getPoint() + amount);
    }
}