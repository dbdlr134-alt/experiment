package com.mjdi.qna;

import com.mjdi.util.Action;

public class QnAServiceFactory {
    private QnAServiceFactory() {}
    private static QnAServiceFactory instance = new QnAServiceFactory();
    public static QnAServiceFactory getInstance() { return instance; }
    
    public Action getAction(String cmd) {
        Action action = null;

        // --- 기존 사용자용 ---
        if (cmd == null || cmd.equals("qna_list")) {
            action = new QnAListService();
        } else if (cmd.equals("qna_write")) {
            action = new QnAWriteService();
        } else if (cmd.equals("qna_write_pro")) {
            action = new QnAWriteProService();
        } else if (cmd.equals("qna_view")) { 
            action = new QnAViewService(); 
        } else if (cmd.equals("admin_qna_list")) {// --- ✅ [추가] 관리자 전용 기능 ---
            // 관리자 질문 목록 조회 (question_list.jsp)
        	action = new AdminQnAListService();
        } 
        else if (cmd.equals("admin_answer_view")) {
            // 관리자 답변 작성 화면 조회 (qna_answer.jsp)
            action = new AdminAnswerViewService();
        }
        else if (cmd.equals("admin_answer_write")) {
            // 관리자 답변 DB 저장 처리
            action = new AdminAnswerWriteService();
        }
        
        return action;
    }
}