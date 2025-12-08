package com.mjdi.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.mjdi.util.DBM;

public class MessageDAO {
    
    private MessageDAO() {}
    private static MessageDAO instance = new MessageDAO();
    public static MessageDAO getInstance() { return instance; }

    // 1. 메세지 전송 (관리자 -> 유저)
    public int sendMessage(String sender, String receiver, String content) {
        String sql = "INSERT INTO jdi_message (sender, receiver, content) VALUES (?, ?, ?)";
        int result = 0;
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, content);
            result = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        return result;
    }

    // 2. 안 읽은 메세지 개수 조회 (헤더 알림용)
    public int getUnreadCount(String userId) {
        int count = 0;
        String sql = "SELECT count(*) FROM jdi_message WHERE receiver = ? AND is_read = 'N'";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) count = rs.getInt(1);
            }
        } catch(Exception e) { e.printStackTrace(); }
        return count;
    }

    // 3. 내 메세지 목록 조회
    public ArrayList<MessageDTO> getMyMessages(String userId) {
        ArrayList<MessageDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM jdi_message WHERE receiver = ? ORDER BY send_date DESC";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try(ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    MessageDTO dto = new MessageDTO();
                    dto.setMsgId(rs.getInt("msg_id"));
                    dto.setSender(rs.getString("sender"));
                    dto.setContent(rs.getString("content"));
                    dto.setRead(rs.getString("is_read").equals("Y"));
                    dto.setSendDate(rs.getString("send_date"));
                    list.add(dto);
                }
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }

    // 4. 메세지 읽음 처리 (상세보기 시)
    public void readMessage(int msgId) {
        String sql = "UPDATE jdi_message SET is_read = 'Y' WHERE msg_id = ?";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, msgId);
            pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
    }
}