package com.mjdi.word;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.mjdi.util.DBM;

public class BookmarkDAO {
    
    private BookmarkDAO() {}
    private static BookmarkDAO instance = new BookmarkDAO();
    public static BookmarkDAO getInstance() { return instance; }

    // 1. 즐겨찾기 상태 확인 (이미 등록했는지?)
    public boolean isBookmarked(String userId, int wordId) {
        boolean result = false;
        String sql = "SELECT count(*) FROM jdi_bookmark WHERE jdi_user=? AND word_id=?";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, wordId);
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next() && rs.getInt(1) > 0) result = true;
            }
        } catch(Exception e) { e.printStackTrace(); }
        return result;
    }

    // 2. 즐겨찾기 추가
    public void addBookmark(String userId, int wordId) {
        String sql = "INSERT INTO jdi_bookmark (jdi_user, word_id) VALUES (?, ?)";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, wordId);
            pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
    }

    // 3. 즐겨찾기 해제 (삭제)
    public void removeBookmark(String userId, int wordId) {
        String sql = "DELETE FROM jdi_bookmark WHERE jdi_user=? AND word_id=?";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, wordId);
            pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
    }

    // 4. 내 즐겨찾기 목록 조회
    public ArrayList<WordDTO> getMyBookmarks(String userId) {
        ArrayList<WordDTO> list = new ArrayList<>();
        // 단어장 테이블과 조인하여 단어 정보를 가져옴
        String sql = "SELECT w.* FROM jdi_bookmark b "
                   + "JOIN japanese_word w ON b.word_id = w.word_id "
                   + "WHERE b.jdi_user = ? ORDER BY b.reg_date DESC";
        
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try(ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    WordDTO dto = new WordDTO();
                    dto.setWord_id(rs.getInt("word_id"));
                    dto.setWord(rs.getString("word"));
                    dto.setDoc(rs.getString("doc"));
                    dto.setKorean(rs.getString("korean"));
                    dto.setJlpt(rs.getString("jlpt"));
                    list.add(dto);
                }
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }
}