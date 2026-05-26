package com.usbtj.springboot.swimming.entity;

import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
public class ChatMessage {
    private Long id;
    private Long senderId;      // 发送者ID
    private Long receiverId;    // 接收者ID
    private String content;     // 消息内容
    private String messageType; // 消息类型：TEXT, IMAGE, FILE
    private Boolean isRead;     // 是否已读
    private LocalDateTime sendTime;
    
    // 临时字段，不存储到数据库
    private String senderName;  // 发送者姓名
    private String receiverName; // 接收者姓名

    public ChatMessage() {
    }

    public ChatMessage(Long senderId, Long receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.messageType = "TEXT";
        this.isRead = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", content='" + content + '\'' +
                ", messageType='" + messageType + '\'' +
                ", isRead=" + isRead +
                ", sendTime=" + sendTime +
                '}';
    }
}
