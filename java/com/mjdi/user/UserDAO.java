package com.mjdi.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.mjdi.util.DBM;
import com.mjdi.util.SHA256;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import java.util.ArrayList;

public class UserDAO {
    
    private static final String API_KEY = "NCSJDONCTN2IMRMR";
    private static final String API_SECRET = "BPTVHSKTAXBMETVZCMXAOBJVQMURPJUL";
    private static final String SENDER_PHONE = "01065613724";
    
    // 싱글톤 패턴 적용 (선택사항, 기존 방식 유지하려면 new UserDAO() 사용)
    private static UserDAO instance = new UserDAO();
    public static UserDAO getInstance() { return instance; }
    public UserDAO() {}

    // 1. 회원가입 (변경 없음)
    public int joinUser(UserDTO dto) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO jdi_login (jdi_user, jdi_pass, jdi_name, jdi_email, jdi_phone) VALUES (?, ?, ?, ?, ?)";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getJdi_user());
            pstmt.setString(2, SHA256.encodeSha256(dto.getJdi_pass())); 
            pstmt.setString(3, dto.getJdi_name());
            pstmt.setString(4, dto.getJdi_email());
            pstmt.setString(5, dto.getJdi_phone());
            result = pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); } 
        finally { DBM.close(conn, pstmt); }
        return result;
    }

    // 2. 로그인 (★ jdi_status 추가 조회)
    public UserDTO loginCheck(String id, String pw) {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM jdi_login WHERE jdi_user = ? AND jdi_pass = ?";
        try {
            conn = DBM.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, SHA256.encodeSha256(pw));
            rs = pstmt.executeQuery();
            if(rs.next()) {
                user = new UserDTO();
                user.setJdi_user(rs.getString("jdi_user"));
                user.setJdi_name(rs.getString("jdi_name"));
                user.setJdi_email(rs.getString("jdi_email"));
                user.setJdi_phone(rs.getString("jdi_phone"));
                user.setJdi_profile(rs.getString("jdi_profile"));
                user.setJdi_role(rs.getString("jdi_role"));
                
                // ★ [추가] 상태값 가져오기 (없으면 기본값 ACTIVE)
                String status = rs.getString("jdi_status");
                user.setJdi_status(status == null ? "ACTIVE" : status);
            }
        } catch (Exception e) { e.printStackTrace(); } 
        finally { DBM.close(conn, pstmt, rs); }
        return user;
    }

    // 3. SMS 전송 (변경 없음)
    public int sendSmsAndGetCode(String userPhone) {
        int randomCode = (int)(Math.random() * 8999) + 1000;
        try {
            DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET, "https://api.coolsms.co.kr");
            Message message = new Message();
            message.setFrom(SENDER_PHONE);
            message.setTo(userPhone.replaceAll("-", "")); 
            message.setText("[My J-Dic] 인증번호: " + randomCode);
            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) { e.printStackTrace(); }
        return randomCode;
    }
    
    // 4. 아이디 찾기 (변경 없음)
    public String findId(String name, String email) {
        String id = null;
        String sql = "SELECT jdi_user FROM jdi_login WHERE jdi_name=? AND jdi_email=?";
        try(Connection conn = DBM.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) id = rs.getString("jdi_user");
            }
        } catch(Exception e) { e.printStackTrace(); }
        return id;
    }

    // 5. 비번 재설정 (변경 없음)
    public int resetPasswordByPhone(String id, String name, String phone, String newPw) {
        int res = 0;
        String sql = "UPDATE jdi_login SET jdi_pass=? WHERE jdi_user=? AND jdi_name=? AND jdi_phone=?";
        try(Connection conn = DBM.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, SHA256.encodeSha256(newPw));
            pstmt.setString(2, id);
            pstmt.setString(3, name);
            pstmt.setString(4, phone);
            res = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        return res;
    }
    
    // 6. 단순 문자 발송 (변경 없음)
    public void sendSmsMessage(String userPhone, String msgText) {
         try {
            DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET, "https://api.coolsms.co.kr");
            Message message = new Message();
            message.setFrom(SENDER_PHONE);
            message.setTo(userPhone.replaceAll("-", "")); 
            message.setText(msgText);
            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    // 7. 비번 확인 (변경 없음)
    public boolean checkPassword(String id, String inputPw) {
        boolean res = false;
        String sql = "SELECT count(*) FROM jdi_login WHERE jdi_user=? AND jdi_pass=?";
        try(Connection conn = DBM.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, SHA256.encodeSha256(inputPw));
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next() && rs.getInt(1)==1) res = true;
            }
        } catch(Exception e) { e.printStackTrace(); }
        return res;
    }

    // 8. 정보 수정 (변경 없음)
    public int updateAll(String id, String name, String phone, String email, String newPw, String profile) {
        int res = 0;
        String sql = (newPw == null || newPw.equals("")) ?
            "UPDATE jdi_login SET jdi_name=?, jdi_phone=?, jdi_email=?, jdi_profile=? WHERE jdi_user=?" :
            "UPDATE jdi_login SET jdi_name=?, jdi_phone=?, jdi_email=?, jdi_pass=?, jdi_profile=? WHERE jdi_user=?";
        try(Connection conn = DBM.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            if(newPw == null || newPw.equals("")) {
                pstmt.setString(4, profile);
                pstmt.setString(5, id);
            } else {
                pstmt.setString(4, SHA256.encodeSha256(newPw));
                pstmt.setString(5, profile);
                pstmt.setString(6, id);
            }
            res = pstmt.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
        return res;
    }
    
    // 9. 프로필 사진만 업데이트 (변경 없음)
    public int updateProfile(String id, String profile) {
        int result = 0;
        String sql = "UPDATE jdi_login SET jdi_profile=? WHERE jdi_user=?";
        try (Connection conn = DBM.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profile);
            pstmt.setString(2, id);
            result = pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }
    
    // 10. 전체 회원 수 조회 (변경 없음)
    public int getUserCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM jdi_login";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }
    
    // 11. 전체 회원 목록 조회 (★ jdi_status 추가)
    public ArrayList<UserDTO> getAllUsers() {
        ArrayList<UserDTO> list = new ArrayList<>();
        String sql = "SELECT jdi_user, jdi_name, jdi_email, jdi_phone, jdi_profile, jdi_role, jdi_status FROM jdi_login ORDER BY jdi_user ASC";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while(rs.next()) {
                UserDTO dto = new UserDTO();
                dto.setJdi_user(rs.getString("jdi_user"));
                dto.setJdi_name(rs.getString("jdi_name"));
                dto.setJdi_email(rs.getString("jdi_email"));
                dto.setJdi_phone(rs.getString("jdi_phone"));
                dto.setJdi_profile(rs.getString("jdi_profile"));
                dto.setJdi_role(rs.getString("jdi_role"));
                // ★ 상태 추가
                String st = rs.getString("jdi_status");
                dto.setJdi_status(st == null ? "ACTIVE" : st);
                
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); } 
        return list;
    }
    
    // 12. [신규] 회원 상태 변경 (차단/해제)
    public int updateUserStatus(String userId, String status) {
        int result = 0;
        String sql = "UPDATE jdi_login SET jdi_status = ? WHERE jdi_user = ?";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, userId);
            result = pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }
    
    
    // ★ [추가] 금지어 리스트 가져오기
    public ArrayList<String> getForbiddenWords() {
        ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT word FROM forbidden_names";

        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("word"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public ArrayList<String> getMyThemes(String userId) {
        ArrayList<String> list = new ArrayList<>();
        list.add("default"); // 기본 테마는 항상 포함
        
        String sql = "SELECT theme_code FROM jdi_user_theme WHERE jdi_user = ?";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try(ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    list.add(rs.getString("theme_code"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 14. 테마 구매 (기존 코드 유지)
    public int buyTheme(String userId, String themeCode) {
        int result = 0;
        String sql = "INSERT INTO jdi_user_theme (jdi_user, theme_code) VALUES (?, ?)";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, themeCode);
            result = pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    // 15. 테마 적용 (기존 코드 유지)
    public int updateTheme(String userId, String themeCode) {
        int result = 0;
        String sql = "UPDATE jdi_login SET jdi_theme = ? WHERE jdi_user = ?";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, themeCode);
            pstmt.setString(2, userId);
            result = pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public ArrayList<ThemeDTO> getAllThemes() {
        ArrayList<ThemeDTO> list = new ArrayList<>();
        
        // [수정 1] SQL 변경
        // 판매 중(Y)인 것과 시크릿(AY)인 것을 모두 가져옵니다. (N은 제외)
        String sql = "SELECT * FROM jdi_theme_info WHERE is_active IN ('Y', 'A') ORDER BY price ASC";
        
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while(rs.next()) {
                ThemeDTO dto = new ThemeDTO();
                dto.setThemeCode(rs.getString("theme_code"));
                dto.setThemeName(rs.getString("theme_name"));
                dto.setPrice(rs.getInt("price"));
                dto.setDescription(rs.getString("description"));
                
                // [수정 2] is_active 값을 DTO에 담아야 JSP에서 'AY'인지 확인 가능
                dto.setIsActive(rs.getString("is_active"));
                
                list.add(dto);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
    
    public int buyThemeWithConn(Connection conn, String userId, String themeCode) {
        int result = 0;
        String sql = "INSERT INTO jdi_my_theme (jdi_user, theme_code) VALUES (?, ?)";
        
        // try-with-resources 사용 (pstmt만 닫음)
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, themeCode);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            // 서비스(Service)에서 롤백할 수 있도록 예외를 던지거나, 
            // 0을 리턴하여 실패를 알림 (여기서는 0 리턴 방식 사용)
        }
        return result;
    }
    
    public int updateAllWithConn(Connection conn, String id, String name, String phone, String email, String newPw, String profile) {
        int result = 0;
        java.sql.PreparedStatement pstmt = null;
        StringBuilder sql = new StringBuilder();
        
        // 비밀번호 변경 여부에 따라 쿼리가 달라질 수 있음
        // newPw가 비어있으면(null or "") 비밀번호는 수정하지 않음
        boolean updatePw = (newPw != null && !newPw.trim().isEmpty());

        sql.append("UPDATE jdi_user SET ");
        sql.append("jdi_name=?, jdi_phone=?, jdi_email=?, jdi_profile=? ");
        if (updatePw) {
            sql.append(", jdi_pw=? ");
        }
        sql.append("WHERE jdi_user=?");

        try {
            pstmt = conn.prepareStatement(sql.toString());
            int idx = 1;
            pstmt.setString(idx++, name);
            pstmt.setString(idx++, phone);
            pstmt.setString(idx++, email);
            pstmt.setString(idx++, profile);
            
            if (updatePw) {
                pstmt.setString(idx++, newPw); // 암호화 필요 시 여기서 처리
            }
            
            pstmt.setString(idx++, id); // WHERE 절 조건
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            // 서비스에서 롤백하도록 예외를 던지거나 0 리턴
        } finally {
            // Connection은 닫지 않고 Statement만 닫음
            if (pstmt != null) try { pstmt.close(); } catch(Exception e) {}
        }
        
        return result;
    }
    
    public int updateProfileWithConn(Connection conn, String userId, String profilePath) {
        int result = 0;
        PreparedStatement pstmt = null;
        String sql = "UPDATE jdi_user SET jdi_profile = ? WHERE jdi_user = ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, profilePath);
            pstmt.setString(2, userId);
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            // 에러 발생 시 0 반환 (Service에서 이를 감지하여 rollback 처리)
        } finally {
            // 주의: conn.close()는 하지 않고, pstmt만 닫습니다.
            if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
        }
        
        return result;
    }
}