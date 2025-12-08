package com.mjdi.user;

public class ProfileReqDTO {
    private int reqId;
    private String userId;
    private String imagePath;
    private String comment;
    private String status;   // PENDING / APPROVED / REJECTED
    private String reqDate;

    public int getReqId() { return reqId; }
    public void setReqId(int reqId) { this.reqId = reqId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReqDate() { return reqDate; }
    public void setReqDate(String reqDate) { this.reqDate = reqDate; }
}
