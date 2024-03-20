package com.im_oregano007.convocraft.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class UserModel {

    private String userName;
    private String phone;
    private Timestamp createdTimestamp;
    private String userId;
    List<String> allGroupsList;

    private String fcmToken;

  private String onlineStatus;

    public UserModel() {
    }

    public UserModel( String userName, String phone, Timestamp createdTimestamp, String userId, String onlineStatus, List<String> allGroupsList) {

        this.userName = userName;
        this.phone = phone;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.onlineStatus = onlineStatus;
        this.allGroupsList = allGroupsList;
     }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public List<String> getAllGroupsList() {
        return allGroupsList;
    }

    public void setAllGroupsList(List<String> allGroupsList) {
        this.allGroupsList = allGroupsList;
    }
}
