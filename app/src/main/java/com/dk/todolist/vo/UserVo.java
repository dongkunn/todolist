package com.dk.todolist.vo;

public class UserVo {
    private String userId;
    private String userPw;
    private String userName;
    private int userPriorOrder;
    private String authCode;
    private String groupCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public int getUserPriorOrder() {
        return userPriorOrder;
    }

    public void setUserPriorOrder(int userPriorOrder) {
        this.userPriorOrder = userPriorOrder;
    }
}
