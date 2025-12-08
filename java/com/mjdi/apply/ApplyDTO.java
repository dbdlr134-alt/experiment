package com.mjdi.apply;

public class ApplyDTO {
    
    private int reqId;       // 신청 고유 번호 (PK)
    private String word;     // 단어
    private String doc;      // 요미가나 (음독/훈독)
    private String korean;   // 뜻
    private String jlpt;     // JLPT 급수
    private String jdiUser;  // 신청자 아이디 (FK)
    private String status;   // 상태 (WAIT:대기, OK:승인, NO:거절)
    private String regDate;  // 신청일
    
    // ★ [중요] 수정 신청 시, 어떤 단어를 고칠지 식별하기 위한 원본 ID
    private int wordId;      

    // 기본 생성자
    public ApplyDTO() {}

    // 편의용 생성자 (신규 등록 신청용)
    public ApplyDTO(String word, String doc, String korean, String jlpt, String jdiUser) {
        this.word = word;
        this.doc = doc;
        this.korean = korean;
        this.jlpt = jlpt;
        this.jdiUser = jdiUser;
    }

    // --- Getter & Setter ---

    public int getReqId() {
        return reqId;
    }
    public void setReqId(int reqId) {
        this.reqId = reqId;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public String getDoc() {
        return doc;
    }
    public void setDoc(String doc) {
        this.doc = doc;
    }
    public String getKorean() {
        return korean;
    }
    public void setKorean(String korean) {
        this.korean = korean;
    }
    public String getJlpt() {
        return jlpt;
    }
    public void setJlpt(String jlpt) {
        this.jlpt = jlpt;
    }
    public String getJdiUser() {
        return jdiUser;
    }
    public void setJdiUser(String jdiUser) {
        this.jdiUser = jdiUser;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRegDate() {
        return regDate;
    }
    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }
    public int getWordId() {
        return wordId;
    }
    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    @Override
    public String toString() {
        return "ApplyDTO [reqId=" + reqId + ", word=" + word + ", user=" + jdiUser + ", status=" + status + "]";
    }
}