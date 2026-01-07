package com.mnu.blog.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern; // ★ 여기가 핵심입니다
import jakarta.validation.constraints.Size;

@Data
public class UserJoinDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    // 8자 이상 + 영문 + 숫자 + 특수문자
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", 
             message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자여야 합니다.")
    private String nickname;

    @NotBlank(message = "전화번호를 입력해주세요.")
    // 전화번호 형식
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", 
             message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;
    
    private String verificationCode;
}