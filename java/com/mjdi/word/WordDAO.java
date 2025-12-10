package com.mjdi.word;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mjdi.util.DBM;

public class WordDAO {
    
    private WordDAO() {}
    private static WordDAO instance = new WordDAO();
    public static WordDAO getInstance() { return instance; }

    // 1. 검색 (정렬: 정확히 일치하는 단어 우선, 그 다음 길이순)
    public ArrayList<WordDTO> searchWords(String keyword) {
        ArrayList<WordDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM japanese_word WHERE word LIKE ? OR korean LIKE ? OR jlpt LIKE ? ORDER BY (CASE WHEN word = ? THEN 0 ELSE 1 END), LENGTH(word)";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            pstmt.setString(4, keyword);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                WordDTO dto = new WordDTO();
                dto.setWord_id(rs.getInt("word_id"));
                dto.setWord(rs.getString("word"));
                dto.setDoc(rs.getString("doc"));
                dto.setKorean(rs.getString("korean"));
                dto.setJlpt(rs.getString("jlpt"));
                list.add(dto);
            }
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return list;
    }

    // 2. 상세 조회
    public WordDTO wordSelect(int word_id){
        WordDTO dto = new WordDTO();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="SELECT * FROM japanese_word WHERE word_id = ?";
        try {
            conn=DBM.getConnection();
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1, word_id);
            rs=pstmt.executeQuery();
            if(rs.next()) {
                dto.setWord_id(rs.getInt("word_id"));
                dto.setWord(rs.getString("word"));
                dto.setDoc(rs.getString("doc"));
                dto.setKorean(rs.getString("korean"));
                dto.setJlpt(rs.getString("jlpt"));
            }
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return dto;
    }
    
    // 3. 자동완성
    public List<WordDTO> autoComplete(String keyword) {
        List<WordDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM japanese_word WHERE word LIKE ? OR korean LIKE ? LIMIT 10";

        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            rs = pstmt.executeQuery();
            while(rs.next()) {
                WordDTO dto = new WordDTO();
                dto.setWord_id(rs.getInt("word_id"));
                dto.setWord(rs.getString("word"));
                dto.setKorean(rs.getString("korean"));
                list.add(dto);
            }
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return list;
    }
    
    // 4. 단어 등록
    public int insertWord(WordDTO dto) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO japanese_word (word, doc, korean, jlpt) VALUES (?, ?, ?, ?)";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getWord());
            pstmt.setString(2, dto.getDoc());
            pstmt.setString(3, dto.getKorean());
            pstmt.setString(4, dto.getJlpt());
            result = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt); }
        return result;
    }
    
    // 5. 단어 수정
    public int updateWord(WordDTO dto) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE japanese_word SET word=?, doc=?, korean=?, jlpt=? WHERE word_id=?";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getWord());
            pstmt.setString(2, dto.getDoc());
            pstmt.setString(3, dto.getKorean());
            pstmt.setString(4, dto.getJlpt());
            pstmt.setInt(5, dto.getWord_id());
            result = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt); }
        return result;
    }
    
 // 7. [신규] 총 단어 수 조회
    public int getTotalWordCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM japanese_word";
        
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
     * [트랜잭션용] 단어 등록
     * Connection을 외부에서 받아 처리하며 close() 하지 않음.
     */
    public int insertWordWithConn(Connection conn, WordDTO dto) {
        int result = 0;
        PreparedStatement pstmt = null;
        // 테이블명과 컬럼명은 실제 DB에 맞춰주세요
        String sql = "INSERT INTO jdi_word (word, doc, korean, jlpt) VALUES (?, ?, ?, ?)";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getWord());
            pstmt.setString(2, dto.getDoc());
            pstmt.setString(3, dto.getKorean());
            pstmt.setString(4, dto.getJlpt());
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            // 0 반환 시 서비스에서 롤백
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
        }
        return result;
    }
}