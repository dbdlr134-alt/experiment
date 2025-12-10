package com.mjdi.notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mjdi.util.DBM;

public class NoticeDAO {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // 싱글톤 패턴
    private NoticeDAO() {}
    private static NoticeDAO instance = new NoticeDAO();
    public static NoticeDAO getInstance() { 
        return instance; 
    }

    // 1. 모든 공지사항 조회 (작성자 닉네임 포함)
    public List<NoticeDTO> noticeList() {
        List<NoticeDTO> list = new ArrayList<>();
        // jdi_notice(n)와 jdi_login(u)을 조인하여 닉네임(jdi_name)을 가져옴
        String sql = "SELECT n.idx, n.title, n.content, n.created_at, n.is_top, "
                   + "       n.writer_id, u.jdi_name AS writer_name "
                   + "FROM jdi_notice n "
                   + "LEFT JOIN jdi_login u ON n.writer_id = u.jdi_user "
                   + "ORDER BY n.is_top DESC, n.created_at DESC";

        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                NoticeDTO dto = new NoticeDTO();
                dto.setIdx(rs.getInt("idx"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content"));
                dto.setCreated_at(rs.getString("created_at"));
                dto.setIs_top(rs.getInt("is_top"));
                
                // 작성자 ID와 닉네임 세팅
                dto.setWriter_id(rs.getString("writer_id"));
                
                // 닉네임이 없으면(탈퇴 등) '알수없음' 또는 ID로 대체
                String writerName = rs.getString("writer_name");
                if(writerName == null) writerName = "관리자";
                dto.setWriter(writerName); 

                list.add(dto);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt, rs);
        }
        return list;
    }


    // 2. 공지사항 상세보기 (작성자 닉네임 포함)
    public NoticeDTO noticeView(int idx) {
        NoticeDTO dto = new NoticeDTO();
        String sql = "SELECT n.*, u.jdi_name AS writer_name "
                   + "FROM jdi_notice n "
                   + "LEFT JOIN jdi_login u ON n.writer_id = u.jdi_user "
                   + "WHERE n.idx=?";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            
            rs = pstmt.executeQuery();
            
            if(rs.next()) {
                dto.setIdx(rs.getInt("idx"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content"));
                dto.setCreated_at(rs.getString("created_at"));
                dto.setIs_top(rs.getInt("is_top"));
                dto.setWriter_id(rs.getString("writer_id"));
                
                String writerName = rs.getString("writer_name");
                if(writerName == null) writerName = "관리자";
                dto.setWriter(writerName);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt, rs);
        }
        return dto;
    }
 
    // 3. 공지사항 등록 (작성자 ID 포함)
    public NoticeDTO noticeWrite(NoticeDTO dto) {
        // SQL 수정: writer_id 추가
        String sql = "INSERT INTO jdi_notice (title, content, writer_id) VALUES (?, ?, ?)";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getTitle());
            pstmt.setString(2, dto.getContent());
            
            // DTO에 담겨있는 작성자 ID를 DB에 저장
            // (컨트롤러/서비스에서 sessionUser의 ID를 dto.setWriter_id()로 넣어줘야 함)
            pstmt.setString(3, dto.getWriter_id());
            
            pstmt.executeUpdate();
            
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt);
        }
        return dto;
    }
    
    // 4. 공지사항 삭제
    public int noticeDelete(int idx) {
        int row = 0;
        String sql = "DELETE FROM jdi_notice WHERE idx=?";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idx);
            
            row = pstmt.executeUpdate();
            
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt);
        }
        return row;
    }
    
    // 5. 공지사항 수정
    public void noticeUpdate(NoticeDTO dto) {
        // 작성자는 수정하지 않는 것이 일반적입니다.
        String sql = "UPDATE jdi_notice SET title=?, content=? WHERE idx=?";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getTitle());
            pstmt.setString(2, dto.getContent());
            pstmt.setInt(3, dto.getIdx());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt);
        }
    }
    
    // 6. 공지글 상단 고정/해제
    public int setTopNotice(int idx, boolean isTop) {
        int row = 0;
        String sql = "UPDATE jdi_notice SET is_top = ? WHERE idx = ?";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, isTop ? 1 : 0);
            pstmt.setInt(2, idx);
            row = pstmt.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBM.close(conn, pstmt);
        }
        return row;
    }
}