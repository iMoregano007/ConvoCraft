package com.im_oregano007.convocraft.model;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private String seenStatus;

    private String msgId;

    private String chatroomID;

    private boolean isGroup;
    private boolean isImage;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderId, Timestamp timestamp, String seenStatus, String msgId, String chatroomID, boolean isGroup, boolean isImage) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.seenStatus = seenStatus;
        this.msgId = msgId;
        this.chatroomID = chatroomID;
        this.isGroup = isGroup;
        this.isImage = isImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSeenStatus() {
        return seenStatus;
    }

    public void setSeenStatus(String seenStatus) {
        this.seenStatus = seenStatus;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getChatroomID() {
        return chatroomID;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public void setChatroomID(String chatroomID) {
        this.chatroomID = chatroomID;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
}
