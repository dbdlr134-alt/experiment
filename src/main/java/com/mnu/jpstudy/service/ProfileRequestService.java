package com.mnu.jpstudy.service;

import org.springframework.stereotype.Service;

import com.mnu.jpstudy.entity.Member;
import com.mnu.jpstudy.entity.ProfileRequest;
import com.mnu.jpstudy.repository.MemberRepository;
import com.mnu.jpstudy.repository.ProfileRequestRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileRequestService {
    private final ProfileRequestRepository requestRepository;
    private final MemberRepository memberRepository;

    // 1. 변경 신청 (유저)
    public void requestProfileChange(Long memberId, String newNickname, String imagePath) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        ProfileRequest req = new ProfileRequest();
        req.setMember(member);
        req.setNewNickname(newNickname);
        req.setNewProfileImageUrl(imagePath);
        req.setStatus(ProfileRequest.RequestStatus.PENDING);
        requestRepository.save(req);
    }

    // 2. 승인 처리 (관리자)
    @Transactional
    public void approveRequest(Long requestId) {
        ProfileRequest req = requestRepository.findById(requestId).orElseThrow();
        req.setStatus(ProfileRequest.RequestStatus.APPROVED);
        
        // 실제 멤버 정보 업데이트
        Member member = req.getMember();
        member.setNickname(req.getNewNickname());
        // member.setProfileImage(req.getNewProfileImageUrl());
    }
}