package com.mjdi.user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.mjdi.util.DBM;

public class ProfileReqDAO {

    private static ProfileReqDAO instance = new ProfileReqDAO();
    public static ProfileReqDAO getInstance() { return instance; }
    private ProfileReqDAO() {}

    public void insertRequest(ProfileReqDTO dto) {
        String sql = "INSERT INTO profile_request(user_id, image_path, comment, status) "
                   + "VALUES(?,?,?,?)";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getUserId());
            pstmt.setString(2, dto.getImagePath());
            pstmt.setString(3, dto.getComment());
            pstmt.setString(4, dto.getStatus());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProfileReqDTO> getPendingList() {
        List<ProfileReqDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM profile_request WHERE status = 'PENDING' ORDER BY req_id DESC";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ProfileReqDTO dto = new ProfileReqDTO();
                dto.setReqId(rs.getInt("req_id"));
                dto.setUserId(rs.getString("user_id"));
                dto.setImagePath(rs.getString("image_path"));
                dto.setComment(rs.getString("comment"));
                dto.setStatus(rs.getString("status"));
                dto.setReqDate(rs.getString("req_date"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ProfileReqDTO getRequest(int reqId) {
        String sql = "SELECT * FROM profile_request WHERE req_id = ?";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reqId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ProfileReqDTO dto = new ProfileReqDTO();
                    dto.setReqId(rs.getInt("req_id"));
                    dto.setUserId(rs.getString("user_id"));
                    dto.setImagePath(rs.getString("image_path"));
                    dto.setComment(rs.getString("comment"));
                    dto.setStatus(rs.getString("status"));
                    dto.setReqDate(rs.getString("req_date"));
                    return dto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateStatus(int reqId, String status) {
        String sql = "UPDATE profile_request SET status=? WHERE req_id=?";
        try (Connection conn = DBM.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, reqId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
