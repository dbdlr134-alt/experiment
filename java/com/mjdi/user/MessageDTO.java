package com.mjdi.user;
public class MessageDTO {
    private int msgId;
    private String sender;
    private String receiver;
    private String content;
    private boolean isRead;
    private String sendDate;
    // Getter & Setter 자동 생성해주세요
    public int getMsgId() { return msgId; }
    public void setMsgId(int msgId) { this.msgId = msgId; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    public String getSendDate() { return sendDate; }
    public void setSendDate(String sendDate) { this.sendDate = sendDate; }
}