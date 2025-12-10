package com.mjdi.qna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mjdi.util.DBM;

public class QuestionDAO {
    // 싱글톤 패턴
    private static QuestionDAO instance = new QuestionDAO();
    public static QuestionDAO getInstance() { return instance; }
    private QuestionDAO() {}
    
    
    // ==========================================
    // 1. 질문(Question) 관련 기능
    // ==========================================

    // [질문 등록]
    public int insertQuestion(QuestionDTO dto) {
        int result = 0;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        String sql = "INSERT INTO jdi_questions (writer_user, title, content) VALUES (?, ?, ?)";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, dto.getWriter_user());
            pstmt.setString(2, dto.getTitle());
            pstmt.setString(3, dto.getContent());
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("질문 등록 에러: " + e.getMessage());
        } finally {
            DBM.close(conn, pstmt);
        }
        return result;
    }

    // [질문 목록] - 닉네임 포함 조회
    public List<QuestionDTO> getQuestionList() {
        List<QuestionDTO> list = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // jdi_login 테이블과 조인하여 jdi_name(닉네임)을 가져옴
        String sql = "SELECT q.q_id, q.writer_user, q.title, q.view_count, "
                   + "       DATE_FORMAT(q.created_at, '%Y-%m-%d') AS created_at, "
                   + "       (SELECT COUNT(*) FROM jdi_answers a WHERE a.q_id = q.q_id) AS answer_count, "
                   + "       u.jdi_name AS writer_name " // 닉네임 별칭
                   + "FROM jdi_questions q "
                   + "LEFT JOIN jdi_login u ON q.writer_user = u.jdi_user "
                   + "ORDER BY q.q_id DESC";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                QuestionDTO dto = new QuestionDTO();
                dto.setQ_id(rs.getInt("q_id"));
                dto.setWriter_user(rs.getString("writer_user")); // 아이디
                dto.setTitle(rs.getString("title"));
                dto.setView_count(rs.getInt("view_count"));
                dto.setCreated_at(rs.getString("created_at"));
                dto.setAnswer_count(rs.getInt("answer_count"));
                
                // 닉네임 세팅 (없으면 아이디로 대체)
                String nick = rs.getString("writer_name");
                if(nick == null) nick = rs.getString("writer_user"); 
                dto.setWriter_name(nick); 
                
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt, rs);
        }
        return list;
    }

 // [1] 조회수 증가 메서드 (새로 추가)
    public void increaseViewCount(int qId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE jdi_questions SET view_count = view_count + 1 WHERE q_id = ?";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, qId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt);
        }
    }

    // [2] 질문 상세 조회 (수정: 조회수 증가 로직 제거 & 닉네임 조회 유지)
    public QuestionDTO getQuestion(int qId) {
        QuestionDTO dto = null;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // 조회수 증가 쿼리(sqlHit)는 여기서 삭제함!
        
        // 닉네임(writer_name)을 가져오는 JOIN 쿼리 유지
        String sqlSelect = "SELECT q.q_id, q.writer_user, q.title, q.content, q.view_count, "
                         + "       DATE_FORMAT(q.created_at, '%Y-%m-%d %H:%i') AS created_at, "
                         + "       u.jdi_name AS writer_name "  // 닉네임 가져오기
                         + "FROM jdi_questions q "
                         + "LEFT JOIN jdi_login u ON q.writer_user = u.jdi_user "
                         + "WHERE q.q_id = ?";
        
        try {
            conn = DBM.getConnection();
            
            // 바로 SELECT 실행
            pstmt = conn.prepareStatement(sqlSelect); 
            pstmt.setInt(1, qId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                dto = new QuestionDTO();
                dto.setQ_id(rs.getInt("q_id"));
                dto.setWriter_user(rs.getString("writer_user"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content")); 
                dto.setView_count(rs.getInt("view_count"));
                dto.setCreated_at(rs.getString("created_at"));
                
                // 닉네임 세팅
                String nick = rs.getString("writer_name");
                if(nick == null) nick = rs.getString("writer_user");
                dto.setWriter_name(nick);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt, rs);
        }
        return dto;
    }
    
    // [질문 삭제]
    public int deleteQuestion(int qId) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM jdi_questions WHERE q_id = ?";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, qId);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt);
        }
        return result;
    }

    // ==========================================
    // 2. 답변(Answer) 관련 기능
    // ==========================================

    // [답변 등록]
    public int insertAnswer(QuestionDTO dto) {
        int result = 0;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO jdi_answers (q_id, writer_user, content) VALUES (?, ?, ?)";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, dto.getQ_id());
            pstmt.setString(2, dto.getWriter_user());
            pstmt.setString(3, dto.getContent());
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt);
        }
        return result;
    }

    // [답변 목록] - 닉네임 포함 조회
    public List<QuestionDTO> getAnswerList(int qId) {
        List<QuestionDTO> list = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // 답변 목록도 닉네임 JOIN
        String sql = "SELECT a.a_id, a.q_id, a.writer_user, a.content, "
                   + "       DATE_FORMAT(a.created_at, '%Y-%m-%d %H:%i') AS created_at, "
                   + "       u.jdi_name AS writer_name "
                   + "FROM jdi_answers a "
                   + "LEFT JOIN jdi_login u ON a.writer_user = u.jdi_user "
                   + "WHERE a.q_id = ? "
                   + "ORDER BY a.a_id ASC";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, qId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                QuestionDTO dto = new QuestionDTO();
                dto.setA_id(rs.getInt("a_id"));
                dto.setQ_id(rs.getInt("q_id"));
                dto.setWriter_user(rs.getString("writer_user"));
                dto.setContent(rs.getString("content"));
                dto.setCreated_at(rs.getString("created_at"));
                
                // 닉네임 세팅
                String nick = rs.getString("writer_name");
                if(nick == null) nick = rs.getString("writer_user");
                dto.setWriter_name(nick);
                
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt, rs);
        }
        return list;
    }
}