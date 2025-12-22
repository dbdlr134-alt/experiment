package com.mnu.jpstudy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberJoinDto {
    private String username;  // 아이디
    private String password;  // 비밀번호
    private String nickname;  // 닉네임
}