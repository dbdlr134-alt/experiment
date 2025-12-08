package com.mjdi.quiz;

import com.mjdi.util.Action;

public class QuizServiceFactory {
    
    // 싱글톤 패턴
    private QuizServiceFactory() {}
    private static QuizServiceFactory instance = new QuizServiceFactory();
    public static QuizServiceFactory getInstance() { return instance; }

    public Action getAction(String cmd) {
        Action action = null;
        
        if (cmd.equals("word_quiz")) {
            action = new WordQuizService();      // 일반 퀴즈 (랜덤/급수별)
        } else if (cmd.equals("quiz_incorrect")) {
            action = new QuizIncorrectService(); // 오답 노트 조회
        } else if (cmd.equals("daily_quiz")) {
            action = new DailyQuizService();     // 오늘의 퀴즈 (하루 1번)
        } else if (cmd.equals("quiz_retry")) { 
            action = new QuizRetryService();     // 오답 복습 (재시험)
        }
        
        return action;
    }
}