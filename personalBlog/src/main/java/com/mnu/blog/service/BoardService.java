package com.mnu.blog.service;

import com.mnu.blog.domain.*; // BoardType, Role 등 import
import com.mnu.blog.repository.LoveRepository;
import com.mnu.blog.repository.PostRepository;
import com.mnu.blog.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final LoveRepository loveRepository;

    // 1. 글쓰기 (관리자 체크 추가)
    @Transactional
    public void 글쓰기(Post post, User user) {
        // ★ [추가된 보안 로직] 공지사항인데 관리자가 아니라면? 에러 발생!
        if (post.getBoardType() == BoardType.NOTICE && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("공지사항은 관리자만 작성할 수 있습니다.");
        }

        post.setCount(0);
        post.setUser(user);
        postRepository.save(post);
    }

    // 2. 글목록
    @Transactional(readOnly = true)
    public Page<Post> 글목록(Pageable pageable, String search, String category) {
        if (category == null || category.isBlank() || category.equals("ALL")) {
            if (search == null || search.isBlank()) {
                return postRepository.findAll(pageable);
            } else {
                return postRepository.findByTitleContainingOrContentContaining(search, search, pageable);
            }
        } else {
            BoardType type = BoardType.valueOf(category);
            if (search == null || search.isBlank()) {
                return postRepository.findByBoardType(type, pageable);
            } else {
                return postRepository.findByBoardTypeAndTitleContainingOrContentContaining(type, search, search, pageable);
            }
        }
    }

    // 3. 글 상세보기
    @Transactional(readOnly = true)
    public Post 글상세보기(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("글 상세보기 실패: 아이디를 찾을 수 없습니다."));
    }

    // 4. 조회수 증가
    @Transactional
    public void 조회수증가(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setCount(post.getCount() + 1);
    }

    // 5. 글 삭제
    @Transactional
    public void 글삭제하기(Long id) {
        postRepository.deleteById(id);
    }

    // 6. 글 수정 (관리자 체크 추가)
    @Transactional
    public void 글수정하기(Long id, Post requestPost) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("글 찾기 실패: 아이디를 찾을 수 없습니다."));
        
        // ★ [추가된 보안 로직] 수정할 때 공지사항으로 바꾸려는데 관리자가 아니라면? 막음!
        if (requestPost.getBoardType() == BoardType.NOTICE && post.getUser().getRole() != Role.ADMIN) {
             throw new IllegalArgumentException("관리자만 공지사항으로 설정할 수 있습니다.");
        }

        post.setTitle(requestPost.getTitle());
        post.setContent(requestPost.getContent());
        post.setBoardType(requestPost.getBoardType());
    }

    // 7. 댓글 쓰기
    @Transactional
    public void 댓글쓰기(User user, Long boardId, Reply requestReply) {
        Post post = postRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 쓰기 실패: 게시글 id를 찾을 수 없습니다."));
        requestReply.setUser(user);
        requestReply.setPost(post);
        replyRepository.save(requestReply);
    }

    // 8. 좋아요 기능
    @Transactional
    public boolean 좋아요(Long boardId, User user) {
        if(loveRepository.findByUserIdAndPostId(user.getId(), boardId).isPresent()) {
            loveRepository.deleteByUserIdAndPostId(user.getId(), boardId);
            return false;
        } else {
            Post post = postRepository.findById(boardId).orElseThrow();
            Love love = Love.builder().user(user).post(post).build();
            loveRepository.save(love);
            return true;
        }
    }
}