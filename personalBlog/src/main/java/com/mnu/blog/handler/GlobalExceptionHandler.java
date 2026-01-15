package com.mnu.blog.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.mnu.blog.dto.ResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 모든 IllegalArgumentException을 잡아서 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDto<String> handleArgumentException(IllegalArgumentException e) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    
    // 그 외 예상치 못한 모든 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseDto<String> handleException(Exception e) {
        e.printStackTrace(); // 서버 로그에는 남김
        return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다: " + e.getMessage());
    }
}