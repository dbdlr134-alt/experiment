package com.mjdi.apply;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mjdi.util.DBM;
import com.mjdi.word.WordDAO;
import com.mjdi.word.WordDTO;

public class ApplyDAO {
    
    // 싱글톤 패턴
    private ApplyDAO() {}
    private static ApplyDAO instance = new ApplyDAO();
    public static ApplyDAO getInstance() { return instance; }

    // =======================================================
    //  PART 1. 신규 단어 등록 신청 (jdi_word_req 테이블)
    // =======================================================

    // 1-1. 등록 신청 (INSERT)
    public int insertApply(ApplyDTO dto) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO jdi_word_req (word, doc, korean, jlpt, jdi_user) VALUES (?, ?, ?, ?, ?)";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getWord());
            pstmt.setString(2, dto.getDoc());
            pstmt.setString(3, dto.getKorean());
            pstmt.setString(4, dto.getJlpt());
            pstmt.setString(5, dto.getJdiUser());
            result = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt); }
        return result;
    }

    // 1-2. 등록 대기 목록 조회
    public ArrayList<ApplyDTO> getWaitList() {
        ArrayList<ApplyDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM jdi_word_req WHERE status = 'WAIT' ORDER BY req_id DESC";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                ApplyDTO dto = new ApplyDTO();
                dto.setReqId(rs.getInt("req_id"));
                dto.setWord(rs.getString("word"));
                dto.setDoc(rs.getString("doc"));
                dto.setKorean(rs.getString("korean"));
                dto.setJlpt(rs.getString("jlpt"));
                dto.setJdiUser(rs.getString("jdi_user"));
                dto.setRegDate(rs.getString("reg_date"));
                list.add(dto);
            }
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return list;
    }

    // 1-3. 등록 신청 상세 조회
    public ApplyDTO getApply(int reqId) {
        ApplyDTO dto = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM jdi_word_req WHERE req_id = ?";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reqId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                dto = new ApplyDTO();
                dto.setReqId(rs.getInt("req_id"));
                dto.setWord(rs.getString("word"));
                dto.setDoc(rs.getString("doc"));
                dto.setKorean(rs.getString("korean"));
                dto.setJlpt(rs.getString("jlpt"));
                dto.setJdiUser(rs.getString("jdi_user"));
            }
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return dto;
    }

    // 1-4. 등록 신청 상태 변경
    public int updateStatus(int reqId, String status) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE jdi_word_req SET status = ? WHERE req_id = ?";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, reqId);
            result = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt); }
        return result;
    }

    // =======================================================
    //  PART 2. 단어 수정 신청 (jdi_word_edit_request 테이블)
    // =======================================================

    // 2-1. 수정 신청 (INSERT)
    public int insertEditApply(ApplyDTO dto) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        // 수정할 원본 단어의 ID(word_id)도 저장해야 함
        String sql = "INSERT INTO jdi_word_edit_request (word, doc, korean, jlpt, jdi_user, word_id, status) VALUES (?, ?, ?, ?, ?, ?, 'WAIT')";

        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getWord());
            pstmt.setString(2, dto.getDoc());
            pstmt.setString(3, dto.getKorean());
            pstmt.setString(4, dto.getJlpt());
            pstmt.setString(5, dto.getJdiUser());
            pstmt.setInt(6, dto.getWordId()); // [중요] 수정 대상 ID

            result = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt); }
        return result;
    }
    
    // 2-2. 수정 대기 목록 조회
    public ArrayList<ApplyDTO> getEditWaitList() {
        ArrayList<ApplyDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM jdi_word_edit_request WHERE status='WAIT' ORDER BY req_id DESC";

        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                ApplyDTO dto = new ApplyDTO();
                dto.setReqId(rs.getInt("req_id"));
                dto.setWord(rs.getString("word"));
                dto.setDoc(rs.getString("doc"));
                dto.setKorean(rs.getString("korean"));
                dto.setJlpt(rs.getString("jlpt"));
                dto.setJdiUser(rs.getString("jdi_user"));
                dto.setWordId(rs.getInt("word_id"));
                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return list;
    }
    
    // 2-3. 수정 신청 상세 조회
    public ApplyDTO getEditApply(int reqId) {
        ApplyDTO dto = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM jdi_word_edit_request WHERE req_id=?";

        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reqId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                dto = new ApplyDTO();
                dto.setReqId(rs.getInt("req_id"));
                dto.setWord(rs.getString("word"));
                dto.setDoc(rs.getString("doc"));
                dto.setKorean(rs.getString("korean"));
                dto.setJlpt(rs.getString("jlpt"));
                dto.setJdiUser(rs.getString("jdi_user"));
                dto.setWordId(rs.getInt("word_id"));
                dto.setStatus(rs.getString("status"));
            }
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return dto;
    }

    // 2-4. ★ [신규] 수정 신청 상태 변경 (테이블이 다름!)
    public int updateEditStatus(int reqId, String status) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        // 등록 신청 테이블이 아니라 '수정 신청 테이블'을 업데이트해야 함
        String sql = "UPDATE jdi_word_edit_request SET status = ? WHERE req_id = ?";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, reqId);
            result = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt); }
        return result;
    }

    // 2-5. 수정 승인 통합 처리
    public void approveEditApply(ApplyDTO dto) {
        // 1. 실제 단어장(WordDAO) 업데이트
        WordDAO wdao = WordDAO.getInstance();
        WordDTO wordDto = new WordDTO();
        
        wordDto.setWord_id(dto.getWordId()); // [중요] 원본 단어 ID
        wordDto.setWord(dto.getWord());
        wordDto.setDoc(dto.getDoc());
        wordDto.setKorean(dto.getKorean());
        wordDto.setJlpt(dto.getJlpt());

        wdao.updateWord(wordDto); // 실제 DB 변경

        // 2. 신청 상태를 'OK'로 변경 (수정 신청 테이블)
        updateEditStatus(dto.getReqId(), "OK");
    }
    
 // 3. [신규] 신규 등록 대기 건수 조회
    public int getNewWaitCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM jdi_word_req WHERE status = 'WAIT'";
        
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }
    
    // 4. [신규] 수정 신청 대기 건수 조회
    public int getEditWaitCount() {
        int count = 0;
        // jdi_word_edit_request 테이블에서 조회
        String sql = "SELECT COUNT(*) FROM jdi_word_edit_request WHERE status = 'WAIT'";
        
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }
    
 // 5. [신규] 프로필 신청 대기 건수 조회
    public int getProfileWaitCount() {
        int count = 0;
        // jdi_word_edit_request 테이블에서 조회
        String sql = "SELECT COUNT(*) FROM profile_request WHERE status = 'PENDING'";
        
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }
    
    /**
     * [트랜잭션용] 신청 상태 변경
     * Connection을 외부에서 받아 처리하며 close() 하지 않음.
     */
    public int updateStatusWithConn(Connection conn, int reqId, String status) {
        int result = 0;
        PreparedStatement pstmt = null;
        String sql = "UPDATE word_apply SET status = ? WHERE req_id = ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, reqId);
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
        }
        return result;
    }
}