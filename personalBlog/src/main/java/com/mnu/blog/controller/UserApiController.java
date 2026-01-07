package com.mnu.blog.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mnu.blog.dto.ResponseDto; // 아래 6단계에서 생성
import com.mnu.blog.dto.UserJoinDto;
import com.mnu.blog.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserApiController {

    @Autowired private UserService userService;

    // 아이디 중복 체크
    @GetMapping("/api/check-username")
    public ResponseDto<Boolean> checkUsername(@RequestParam String username) {
        boolean isDuplicate = userService.checkUsernameDuplicate(username);
        return new ResponseDto<>(HttpStatus.OK.value(), isDuplicate); // true면 중복
    }

    // 문자 발송
    @PostMapping("/api/sms/send")
    public ResponseDto<String> sendSms(@RequestBody Map<String, String> body) {
        userService.sendSms(body.get("phoneNumber"));
        return new ResponseDto<>(HttpStatus.OK.value(), "발송완료");
    }

    // 문자 인증 확인
    @PostMapping("/api/sms/verify")
    public ResponseDto<Boolean> verifySms(@RequestBody Map<String, String> body) {
        boolean isVerified = userService.verifySms(body.get("phoneNumber"), body.get("code"));
        return new ResponseDto<>(HttpStatus.OK.value(), isVerified);
    }
    
    // 회원가입 요청 (기존 Controller 대신 이걸 사용)
    @PostMapping("/auth/joinProc")
    public ResponseDto<String> save(@Valid @RequestBody UserJoinDto dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), bindingResult.getFieldError().getDefaultMessage());
        }
        userService.회원가입(dto);
        return new ResponseDto<>(HttpStatus.OK.value(), "회원가입 완료");
    }
}