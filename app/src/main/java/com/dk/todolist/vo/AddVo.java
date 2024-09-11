package com.dk.todolist.vo;

public class AddVo {
    private String groupCode;
    private String userId;
    private String year;
    private String itemDateDetail;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemDateDetail() {
        return itemDateDetail;
    }

    public void setItemDateDetail(String itemDateDetail) {
        this.itemDateDetail = itemDateDetail;
    }
}
