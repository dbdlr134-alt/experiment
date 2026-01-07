package com.mnu.blog.controller;

import com.mnu.blog.config.auth.PrincipalDetail;
import com.mnu.blog.domain.Post;
import com.mnu.blog.domain.Reply;
import com.mnu.blog.dto.ResponseDto;
import com.mnu.blog.repository.ReplyRepository;
import com.mnu.blog.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class BoardApiController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private ReplyRepository replyRepository;

    // 1. 글쓰기 (이 부분이 없어서 404가 났을 겁니다)
    @PostMapping("/api/board")
    public ResponseDto<Integer> save(@RequestBody Post post, @AuthenticationPrincipal PrincipalDetail principal) {
        boardService.글쓰기(post, principal.getUser());
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }

    // 2. 글 삭제
    @DeleteMapping("/api/board/{id}")
    public ResponseDto<Integer> deleteById(@PathVariable("id") Long id) {
        boardService.글삭제하기(id);
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }

    // 3. 글 수정
    @PutMapping("/api/board/{id}")
    public ResponseDto<Integer> update(@PathVariable("id") Long id, @RequestBody Post post) {
        boardService.글수정하기(id, post);
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }

    // 4. 댓글 작성
    @PostMapping("/api/board/{boardId}/reply")
    public ResponseDto<Integer> replySave(@PathVariable("boardId") Long boardId, 
                                          @RequestBody Reply reply, 
                                          @AuthenticationPrincipal PrincipalDetail principal) {
        boardService.댓글쓰기(principal.getUser(), boardId, reply);
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }

    // 5. 댓글 삭제
    @DeleteMapping("/api/board/{boardId}/reply/{replyId}")
    public ResponseDto<Integer> replyDelete(@PathVariable("boardId") Long boardId, 
                                            @PathVariable("replyId") Long replyId) {
        replyRepository.deleteById(replyId);
        return new ResponseDto<>(HttpStatus.OK.value(), 1);
    }
    
    // 6. 좋아요 (하트)
    @PostMapping("/api/board/{boardId}/love")
    public ResponseDto<Boolean> love(@PathVariable("boardId") Long boardId, 
                                     @AuthenticationPrincipal PrincipalDetail principal) {
        boolean result = boardService.좋아요(boardId, principal.getUser());
        return new ResponseDto<>(HttpStatus.OK.value(), result);
    }
}