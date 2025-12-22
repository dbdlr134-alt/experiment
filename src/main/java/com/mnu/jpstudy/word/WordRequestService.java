package com.mnu.jpstudy.word;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordRequestService {

    private final WordRequestRepository wordRequestRepository;
    private final WordService wordService;

    @Transactional
    public WordRequest submitNewRequest(WordRequestForm form) {
        WordRequest request = WordRequest.builder()
                .requestType(WordRequestType.NEW)
                .status(WordRequestStatus.PENDING)
                .word(form.getWord())
                .doc(form.getDoc())
                .korean(form.getKorean())
                .jlpt(form.getJlpt())
                .requestedBy(form.getRequestedBy())
                .build();
        return wordRequestRepository.save(request);
    }

    @Transactional
    public WordRequest submitEditRequest(WordRequestForm form) {
        JapaneseWord target = wordService.findById(form.getWordId());
        WordRequest request = WordRequest.builder()
                .requestType(WordRequestType.EDIT)
                .status(WordRequestStatus.PENDING)
                .targetWord(target)
                .word(form.getWord())
                .doc(form.getDoc())
                .korean(form.getKorean())
                .jlpt(form.getJlpt())
                .requestedBy(form.getRequestedBy())
                .build();
        return wordRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<WordRequest> getPendingRequests() {
        return wordRequestRepository.findByStatusOrderByCreatedAtDesc(WordRequestStatus.PENDING);
    }

    @Transactional
    public WordRequest approve(Long requestId, String processor) {
        WordRequest request = getRequest(requestId);

        if (request.getStatus() != WordRequestStatus.PENDING) {
            return request;
        }

        if (request.getRequestType() == WordRequestType.NEW) {
            JapaneseWord created = wordService.createWord(request.getWord(), request.getDoc(), request.getKorean(), request.getJlpt());
            request.setTargetWord(created);
        } else if (request.getRequestType() == WordRequestType.EDIT && request.getTargetWord() != null) {
            wordService.updateWord(request.getTargetWord().getWordId(), request.getWord(), request.getDoc(), request.getKorean(), request.getJlpt());
        }

        request.setStatus(WordRequestStatus.APPROVED);
        request.setProcessedBy(processor);
        request.setProcessedAt(LocalDateTime.now());
        return wordRequestRepository.save(request);
    }

    @Transactional
    public WordRequest reject(Long requestId, String processor) {
        WordRequest request = getRequest(requestId);
        if (request.getStatus() != WordRequestStatus.PENDING) {
            return request;
        }
        request.setStatus(WordRequestStatus.REJECTED);
        request.setProcessedBy(processor);
        request.setProcessedAt(LocalDateTime.now());
        return wordRequestRepository.save(request);
    }

    private WordRequest getRequest(Long requestId) {
        return wordRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다: " + requestId));
    }
}