package com.mnu.jpstudy.word;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRequestRepository extends JpaRepository<WordRequest, Long> {

    List<WordRequest> findByStatusOrderByCreatedAtDesc(WordRequestStatus status);
}