package com.mnu.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateDto {
    private Long id;
    private String password;
    private String nickname;
    private String phoneNumber;
    // 프로필 사진 URL은 별도 API로 처리하거나 필요 시 추가
}