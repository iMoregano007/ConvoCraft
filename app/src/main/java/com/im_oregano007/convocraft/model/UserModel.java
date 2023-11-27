package com.im_oregano007.convocraft.model;

import com.google.firebase.Timestamp;

public class UserModel {

    private String userName;
    private String phone;
    private Timestamp createdTimestamp;
    private String userId;

    private String fcmToken;

    public UserModel() {
    }

    public UserModel( String userName, String phone, Timestamp createdTimestamp, String userId) {

        this.userName = userName;
        this.phone = phone;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
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


}
