package com.im_oregano007.convocraft.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    String chatroomId;
    Timestamp lastMessageTimeStamp;
    List<String> userIds;
    String lastMessageSenderId;
    String lastMessage;
    String groupName;
    boolean isGroup;

    public ChatroomModel() {
    }

    public ChatroomModel(String chatroomId, Timestamp lastMessageTimeStamp, List<String> userIds, String lastMessageSenderId, String groupName, boolean isGroup) {
        this.chatroomId = chatroomId;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.userIds = userIds;
        this.lastMessageSenderId = lastMessageSenderId;
        this.groupName = groupName;
        this.isGroup = isGroup;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Timestamp getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(Timestamp lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
}
