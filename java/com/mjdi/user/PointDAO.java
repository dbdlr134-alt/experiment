package com.mjdi.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mjdi.util.DBM;

public class PointDAO {
    
    // 싱글톤 패턴
    private PointDAO() {}
    private static PointDAO instance = new PointDAO();
    public static PointDAO getInstance() { return instance; }
    
    // 1. 포인트 적립/사용 메서드
    // 예: dao.addPoint("test01", 10, "퀴즈 정답");
    public int addPoint(String id, int amount, String reason) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO jdi_point_log (jdi_user, amount, reason) VALUES (?, ?, ?)";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setInt(2, amount);
            pstmt.setString(3, reason);
            result = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt); }
        return result;
    }
    
    // 2. 내 포인트 총합 계산 메서드 (로그인/마이페이지 용)
    // 예: int myPoint = dao.getTotalPoint("test01");
    public int getTotalPoint(String id) {
        int total = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // 내 아이디의 모든 amount를 더함
        String sql = "SELECT SUM(amount) FROM jdi_point_log WHERE jdi_user = ?";
        
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            
            if(rs.next()) {
                total = rs.getInt(1); // 합계 결과 리턴
            }
        } catch(Exception e) { e.printStackTrace(); }
        finally { DBM.close(conn, pstmt, rs); }
        return total;
    }

 // 3. [신규] 포인트가 충분한지 확인
 public boolean checkPointSufficient(String userId, int requiredAmount) {
     // getTotalPoint는 SUM(amount)를 반환합니다.
     int currentPoint = getTotalPoint(userId);
     
     // 현재 포인트가 요구량보다 크거나 같으면 true 반환
     return currentPoint >= requiredAmount;
 }
}