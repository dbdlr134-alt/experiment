package com.mnu.jpstudy.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mnu.jpstudy.service.ProfileRequestService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileRequestService profileRequestService;

    // 프로필 변경 신청 폼
    @GetMapping("/request")
    public String requestForm() {
        return "profile/request_form";
    }

    // 신청 처리
    @PostMapping("/request")
    public String requestProcess(Principal principal, String nickname, @RequestParam("file") MultipartFile file) {
        // 실제로는 여기서 파일 저장 로직(FileService) 호출 후 경로를 받아와야 함
        String savedPath = "/images/upload/" + file.getOriginalFilename(); 
        
        // Principal.getName()은 username을 반환함 -> 서비스에서 username으로 ID 찾아야 함 (생략)
        // profileRequestService.requestProfileChange(memberId, nickname, savedPath);
        
        return "redirect:/profile/request?success";
    }
}