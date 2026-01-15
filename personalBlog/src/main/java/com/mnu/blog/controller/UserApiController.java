package com.mnu.blog.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.mnu.blog.dto.ResponseDto; // 아래 6단계에서 생성
import com.mnu.blog.dto.UserJoinDto;
import com.mnu.blog.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserApiController {

    @Autowired private UserService userService;

    // 아이디 중복 체크
    @GetMapping("/api/check-username")
    // @RequestParam 옆에 ("username")을 명시적으로 적어줍니다.
    public ResponseDto<Boolean> checkUsername(@RequestParam("username") String username) {
        boolean isDuplicate = userService.checkUsernameDuplicate(username);
        return new ResponseDto<>(HttpStatus.OK.value(), isDuplicate);
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
 // ★ [추가] 닉네임 중복 확인 API
    @GetMapping("/api/check-nickname")
    public ResponseDto<Boolean> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isDuplicate = userService.checkNicknameDuplicate(nickname);
        return new ResponseDto<>(HttpStatus.OK.value(), isDuplicate); // true면 중복
    }
    
 // 회원수정 요청
    @PutMapping("/user")
    public ResponseDto<Integer> update(@RequestBody User user, 
                                       @AuthenticationPrincipal PrincipalDetail principal) { // 세션정보 접근
        user.setId(principal.getUser().getId()); // 누구를 수정할지 ID 세팅
        userService.회원수정(user);
        
        // ★ 중요: 세션(로그인 정보)은 자동으로 안 바뀝니다.
        // 수정 후 강제로 로그아웃 시키거나, 세션을 갱신해야 하는데 
        // 가장 쉬운 방법은 "수정 완료 후 다시 로그인해주세요" 라고 안내하는 것입니다.
        
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }
    
 // (관리자용) 회원 강제 삭제
    @DeleteMapping("/api/user/{id}")
    // ★ 수정됨: @PathVariable("id") 라고 명시적으로 적어줍니다.
    public ResponseDto<Integer> deleteUser(@PathVariable("id") Long id, 
                                           @AuthenticationPrincipal PrincipalDetail principal) {
        
        if (principal.getUser().getRole() != Role.ADMIN) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), 0); 
        }

        userService.회원삭제(id);
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }
    
 // ★ [수정됨] 프로필 사진 업로드 및 수정 API
    @PutMapping("/api/user/profile")
    public ResponseDto<String> profileUpdate(@RequestParam("image") MultipartFile image,
                                             @AuthenticationPrincipal PrincipalDetail principal) {
        
        UUID uuid = UUID.randomUUID(); 
        String imageFileName = uuid + "_" + image.getOriginalFilename(); 
        
        // 1. 저장 경로를 절대 경로로 안전하게 잡기
        String projectPath = System.getProperty("user.dir"); // 프로젝트 루트 (C:\Users\...\blog)
        Path imageFilePath = Paths.get(projectPath + "/src/main/resources/static/images/profile/" + imageFileName);
        
        // 2. 폴더가 없으면 만들기 (안전장치)
        File folder = imageFilePath.getParent().toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            Files.write(imageFilePath, image.getBytes()); // 3. 파일 저장
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "파일 저장 실패");
        }
        
        // 4. DB 업데이트 (변경된 파일명으로)
        User user = principal.getUser();
        user.setProfileUrl("/images/profile/" + imageFileName); // 웹 접근 경로
        userService.회원수정(user); 
        
        // 5. 세션 값 변경 (★중요: 즉시 반영을 위해 principal 객체도 수정)
        principal.setUser(user);
        
        // 변경된 이미지 주소 리턴
        return new ResponseDto<>(HttpStatus.OK.value(), "/images/profile/" + imageFileName);
    }
    
    @PostMapping("/auth/reset-password")
    public ResponseDto<String> resetPassword(@RequestBody User user) {
        // ★ getEmail() 대신 getPhoneNumber() 사용
        String tempPw = userService.비밀번호찾기(user.getUsername(), user.getPhoneNumber());
        return new ResponseDto<>(HttpStatus.OK.value(), tempPw);
    }
    
 // ★ [추가] 아이디 찾기 (닉네임으로)
    @PostMapping("/auth/find-id")
    public ResponseDto<String> findId(@RequestBody User user) {
        // user.getNickname()을 사용합니다.
        String foundUsername = userService.아이디찾기(user.getNickname());
        return new ResponseDto<>(HttpStatus.OK.value(), foundUsername);
    }
}