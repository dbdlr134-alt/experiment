package com.mjdi.quiz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletContext;

import com.mjdi.util.DBM;

public class QuizDAO {
    
    // --- 싱글톤 패턴 ---
    private QuizDAO() {}
    private static QuizDAO instance = new QuizDAO();
    public static QuizDAO getInstance() { return instance; }

    // ==========================================================
    // 1. JLPT 급수별 퀴즈 랜덤 생성 (japanese_word 테이블 활용)
    //    -> jquiz 테이블이 비어있어도 단어장에서 문제를 만들어냄
    // ==========================================================
    public List<QuizDTO> getRandomQuiz(String jlpt) {
        List<QuizDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBM.getConnection();
            
            // (1) 해당 급수의 단어 중 정답 단어 5개 랜덤 추출
            String sql = "SELECT * FROM japanese_word WHERE jlpt = ? ORDER BY RAND() LIMIT 5";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, jlpt);
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
                // 정답 데이터
                int wordId = rs.getInt("word_id");
                String word = rs.getString("word");
                String answerDoc = rs.getString("doc"); // 정답 (요미가나)
                String korean = rs.getString("korean"); // 뜻 (힌트)
                
                // (2) 오답 보기 3개 생성 (현재 정답이 아닌 단어 중에서 랜덤)
                List<String> options = new ArrayList<>();
                options.add(answerDoc); // 정답 먼저 넣기
                
                // 내부에서 별도 쿼리 실행
                String sqlWrong = "SELECT doc FROM japanese_word WHERE word_id != ? ORDER BY RAND() LIMIT 3";
                PreparedStatement pstmt2 = conn.prepareStatement(sqlWrong);
                pstmt2.setInt(1, wordId);
                ResultSet rs2 = pstmt2.executeQuery();
                
                while(rs2.next()) {
                    options.add(rs2.getString("doc"));
                }
                rs2.close();
                pstmt2.close();
                
                // 단어가 부족해서 보기가 4개가 안 되면 문제 생성 스킵
                if(options.size() < 4) continue;
                
                // (3) 보기 섞기 (정답 위치 숨기기)
                Collections.shuffle(options);
                
                // (4) DTO 포장
                QuizDTO dto = new QuizDTO();
                dto.setQuiz_id(wordId);
                dto.setWord(word);
                dto.setKorean(korean);
                dto.setJlpt(jlpt);
                
                dto.setSelection1(options.get(0));
                dto.setSelection2(options.get(1));
                dto.setSelection3(options.get(2));
                dto.setSelection4(options.get(3));
                
                // 정답 번호 찾기 (1~4)
                int ansIdx = options.indexOf(answerDoc) + 1;
                dto.setAnswer(String.valueOf(ansIdx));
                
                list.add(dto);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt, rs);
        }
        return list;
    }

    // ==========================================================
    // 2. 오늘의 퀴즈 (하루에 한 번만 변경 - 서버 전체 저장)
    // ==========================================================
    public void checkAndSetGlobalQuiz(ServletContext application) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        String savedDate = (String) application.getAttribute("quizDate");
        Object savedObj = application.getAttribute("todayQuiz");
        
        // 안전장치: 저장된 객체가 QuizDTO가 아니면 null 처리 (서버 재시작 없이 오류 방지)
        QuizDTO savedQuiz = (savedObj instanceof QuizDTO) ? (QuizDTO) savedObj : null;

        // 퀴즈가 없거나, 날짜가 바뀌었으면 새로 생성
        if (savedQuiz == null || savedDate == null || !today.equals(savedDate)) {
            System.out.println(">> [QuizDAO] 오늘의 퀴즈를 새로 생성합니다 (" + today + ")");
            
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DBM.getConnection();
                
                // 전체 단어 중 1개 랜덤 추출
                String sqlQuiz = "SELECT * FROM japanese_word ORDER BY RAND() LIMIT 1";
                pstmt = conn.prepareStatement(sqlQuiz);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    int wordId = rs.getInt("word_id");
                    String word = rs.getString("word");
                    String answerDoc = rs.getString("doc");
                    String korean = rs.getString("korean");
                    String jlpt = rs.getString("jlpt");

                    // 오답 보기 3개
                    String sqlWrong = "SELECT doc FROM japanese_word WHERE word_id != ? ORDER BY RAND() LIMIT 3";
                    PreparedStatement pstmt2 = conn.prepareStatement(sqlWrong);
                    pstmt2.setInt(1, wordId);
                    ResultSet rs2 = pstmt2.executeQuery();
                    
                    List<String> options = new ArrayList<>();
                    options.add(answerDoc);
                    while(rs2.next()) {
                        options.add(rs2.getString("doc"));
                    }
                    Collections.shuffle(options);
                    
                    QuizDTO quizDto = new QuizDTO();
                    quizDto.setQuiz_id(wordId);
                    quizDto.setWord(word);
                    quizDto.setKorean(korean);
                    quizDto.setJlpt(jlpt);
                    
                    quizDto.setSelection1(options.get(0));
                    quizDto.setSelection2(options.get(1));
                    quizDto.setSelection3(options.get(2));
                    quizDto.setSelection4(options.get(3));
                    
                    int answerIndex = options.indexOf(answerDoc) + 1;
                    quizDto.setAnswer(String.valueOf(answerIndex));
                    
                    // 서버 공용 저장소(Application)에 저장
                    application.setAttribute("todayQuiz", quizDto);
                    application.setAttribute("quizDate", today);
                    application.setAttribute("todaySolvedUsers", new HashSet<String>());
             
                    System.out.println(">> [QuizDAO] 오늘의 퀴즈 및 참여자 명단이 초기화되었습니다.");
                    
                    rs2.close();
                    pstmt2.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DBM.close(conn, pstmt, rs);
            }
        }
    }

    // ==========================================================
    // 3. 오답 노트 추가 (틀렸을 때)
    // ==========================================================
    public void addIncorrectNote(String userId, int quizId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        String sql = "INSERT INTO incorrect_note (jdi_user, quiz_id, wrong_count, wrong_date) "
                   + "VALUES (?, ?, 1, NOW()) "
                   + "ON DUPLICATE KEY UPDATE wrong_count = wrong_count + 1, wrong_date = NOW()";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId); 
            pstmt.setInt(2, quizId);    
            
            pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt);
        }
    }

    // ==========================================================
    // 4. 내 오답 노트 조회 (수정됨)
    // ==========================================================
    public List<QuizDTO> getIncorrectNotes(String userId) {
        List<QuizDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // n.quiz_id는 사실상 word_id와 같습니다.
        String safeSql = "SELECT n.quiz_id, w.word, w.doc, w.korean, n.wrong_count, n.wrong_date "
                       + "FROM incorrect_note n "
                       + "JOIN japanese_word w ON n.quiz_id = w.word_id "
                       + "WHERE n.jdi_user = ? "
                       + "ORDER BY n.wrong_date DESC";

        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(safeSql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
                QuizDTO dto = new QuizDTO();
                
                // ★ [핵심 수정] quiz_id뿐만 아니라 word_id도 꼭 넣어줘야 합니다!
                int id = rs.getInt("quiz_id");
                dto.setQuiz_id(id);
                dto.setWord_id(id); // <--- 이 부분이 빠져서 0으로 나오고 있었습니다.
                
                dto.setWord(rs.getString("word"));
                
                String doc = rs.getString("doc");
                String kor = rs.getString("korean");
                dto.setDoc(doc == null ? "-" : doc);
                dto.setKorean(kor == null ? "정보 없음" : kor);
                
                dto.setWrong_count(rs.getInt("wrong_count"));
                dto.setWrong_date(rs.getString("wrong_date"));
                
                list.add(dto);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt, rs);
        }
        
        return list;
    }

    // ==========================================
    // 5. 오답노트 개수 조회 (10개 이상인지 체크용)
    // ==========================================
    public int getIncorrectCount(String userId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM incorrect_note WHERE jdi_user = ?";
        
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // ==========================================
    // 6. 오답노트 복습 퀴즈 생성 (오답 목록에서 5개 랜덤 추출)
    // ==========================================
    public List<QuizDTO> getIncorrectQuiz(String userId) {
        List<QuizDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBM.getConnection();
            
            // 내 오답노트(n)와 단어장(w)을 조인하여 단어 정보 가져오기
            String sql = "SELECT w.word_id, w.word, w.doc, w.korean, w.jlpt "
                       + "FROM incorrect_note n "
                       + "JOIN japanese_word w ON n.quiz_id = w.word_id "
                       + "WHERE n.jdi_user = ? "
                       + "ORDER BY RAND() LIMIT 5"; // 랜덤 5문제
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
                int wordId = rs.getInt("word_id");
                String word = rs.getString("word");
                String answerDoc = rs.getString("doc");
                String korean = rs.getString("korean");
                String jlpt = rs.getString("jlpt");

                // 오답 보기 3개 생성 (전체 단어장에서 랜덤)
                List<String> options = new ArrayList<>();
                options.add(answerDoc);
                
                String sqlWrong = "SELECT doc FROM japanese_word WHERE word_id != ? ORDER BY RAND() LIMIT 3";
                PreparedStatement pstmt2 = conn.prepareStatement(sqlWrong);
                pstmt2.setInt(1, wordId);
                ResultSet rs2 = pstmt2.executeQuery();
                
                while(rs2.next()) {
                    options.add(rs2.getString("doc"));
                }
                rs2.close();
                pstmt2.close();
                
                if(options.size() < 4) continue;
                
                Collections.shuffle(options);
                
                QuizDTO dto = new QuizDTO();
                dto.setQuiz_id(wordId);
                dto.setWord(word);
                dto.setKorean(korean);
                dto.setJlpt(jlpt);
                
                dto.setSelection1(options.get(0));
                dto.setSelection2(options.get(1));
                dto.setSelection3(options.get(2));
                dto.setSelection4(options.get(3));
                
                int ansIdx = options.indexOf(answerDoc) + 1;
                dto.setAnswer(String.valueOf(ansIdx));
                
                list.add(dto);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt, rs);
        }
        return list;
    }

    // ==========================================
    // 7. 오답노트 삭제 (복습 성공 시)
    // ==========================================
    public void removeIncorrectNote(String userId, int quizId) {
        String sql = "DELETE FROM incorrect_note WHERE jdi_user = ? AND quiz_id = ?";
        
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, quizId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 // 8. [신규] 문제 풀이 횟수 증가 (퀴즈 제출 시 호출)
    public void updateSolveCount(String userId, int count) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        // 기존 횟수에 방금 푼 문제 수(count)만큼 더함
        String sql = "UPDATE jdi_login SET jdi_solve_count = jdi_solve_count + ? WHERE jdi_user = ?";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, count);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt); }
    }

    // 9. [신규] 나의 총 풀이 횟수 조회 (마이페이지용)
    public int getMySolveCount(String userId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT jdi_solve_count FROM jdi_login WHERE jdi_user = ?";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return count;
    }
}