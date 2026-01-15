package com.mnu.blog.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mnu.blog.config.auth.PrincipalDetail;
import com.mnu.blog.domain.Role;
import com.mnu.blog.domain.User;
import com.mnu.blog.dto.ResponseDto;
import com.mnu.blog.dto.UserJoinDto;
import com.mnu.blog.dto.UserUpdateDto; // DTO 사용
import com.mnu.blog.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/api/check-username")
    public ResponseDto<Boolean> checkUsername(@RequestParam("username") String username) {
        return new ResponseDto<>(HttpStatus.OK.value(), userService.checkUsernameDuplicate(username));
    }

    @GetMapping("/api/check-nickname")
    public ResponseDto<Boolean> checkNickname(@RequestParam("nickname") String nickname) {
        return new ResponseDto<>(HttpStatus.OK.value(), userService.checkNicknameDuplicate(nickname));
    }

    @PostMapping("/api/sms/send")
    public ResponseDto<String> sendSms(@RequestBody Map<String, String> body) {
        userService.sendSms(body.get("phoneNumber"));
        return new ResponseDto<>(HttpStatus.OK.value(), "발송완료");
    }

    @PostMapping("/api/sms/verify")
    public ResponseDto<Boolean> verifySms(@RequestBody Map<String, String> body) {
        boolean isVerified = userService.verifySms(body.get("phoneNumber"), body.get("code"));
        return new ResponseDto<>(HttpStatus.OK.value(), isVerified);
    }
    
    @PostMapping("/auth/joinProc")
    public ResponseDto<String> save(@Valid @RequestBody UserJoinDto dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), bindingResult.getFieldError().getDefaultMessage());
        }
        userService.회원가입(dto);
        return new ResponseDto<>(HttpStatus.OK.value(), "회원가입 완료");
    }
    
    // ★ [수정] Entity 대신 DTO 사용
    @PutMapping("/user")
    public ResponseDto<Integer> update(@RequestBody UserUpdateDto dto, 
                                       @AuthenticationPrincipal PrincipalDetail principal) {
        dto.setId(principal.getUser().getId()); // 세션에서 ID 가져와서 세팅
        userService.회원수정(dto);
        
        // 세션 갱신을 위해 재로그인 유도
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }
    
    // ★ [수정] 파일 저장 로직 Service로 위임
    @PutMapping("/api/user/profile")
    public ResponseDto<String> profileUpdate(@RequestParam("image") MultipartFile image,
                                             @AuthenticationPrincipal PrincipalDetail principal) {
        
        String newProfileUrl = userService.프로필사진변경(principal.getUser().getId(), image);
        
        // 세션 이미지 즉시 변경 (화면 반영용)
        principal.getUser().setProfileUrl(newProfileUrl);
        
        return new ResponseDto<>(HttpStatus.OK.value(), newProfileUrl);
    }

    @DeleteMapping("/api/user/{id}")
    public ResponseDto<Integer> deleteUser(@PathVariable("id") Long id, 
                                           @AuthenticationPrincipal PrincipalDetail principal) {
        if (principal.getUser().getRole() != Role.ADMIN) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), 0); 
        }
        userService.회원삭제(id);
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }
    
    @PostMapping("/auth/reset-password")
    public ResponseDto<String> resetPassword(@RequestBody User user) {
        // 서비스 호출 (이제 내부에서 문자까지 보냄)
        userService.비밀번호찾기(user.getUsername(), user.getPhoneNumber());
        
        // 화면에는 비밀번호 대신 성공 메시지 전달
        return new ResponseDto<>(HttpStatus.OK.value(), "임시 비밀번호가 문자로 전송되었습니다.");
    }
    
    @PostMapping("/auth/find-id")
    public ResponseDto<String> findId(@RequestBody User user) {
        String foundUsername = userService.아이디찾기(user.getNickname());
        return new ResponseDto<>(HttpStatus.OK.value(), foundUsername);
    }
}