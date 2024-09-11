package com.dk.todolist.vo;

import java.io.Serializable;

public class ItemVo implements Serializable {
    private int id;
    private String groupCode;
    private String itemType;
    private String itemDate;
    private String itemYear;
    private String itemDateDetail;
    private String itemContents;
    private String itemComYn;
    private boolean isRepetition;
    private String delYn;
    private int priorOrder;
    private String createAuthCode;
    private String createUserId;
    private String createDate;
    private String updateUserId;
    private String updateDate;
    private String schIitemContents;

    public String getSchIitemContents() {
        return schIitemContents;
    }

    public void setSchIitemContents(String schIitemContents) {
        this.schIitemContents = schIitemContents;
    }

    public String getItemYear() {
        return itemYear;
    }

    public void setItemYear(String itemYear) {
        this.itemYear = itemYear;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemDate() {
        return itemDate;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

    public String getItemDateDetail() {
        return itemDateDetail;
    }

    public void setItemDateDetail(String itemDateDetail) {
        this.itemDateDetail = itemDateDetail;
    }

    public String getItemContents() {
        return itemContents;
    }

    public void setItemContents(String itemContents) {
        this.itemContents = itemContents;
    }

    public String getItemComYn() {
        return itemComYn;
    }

    public void setItemComYn(String itemComYn) {
        this.itemComYn = itemComYn;
    }

    public String getDelYn() {
        return delYn;
    }

    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    public int getPriorOrder() {
        return priorOrder;
    }

    public void setPriorOrder(int priorOrder) {
        this.priorOrder = priorOrder;
    }

    public String getCreateAuthCode() {
        return createAuthCode;
    }

    public void setCreateAuthCode(String createAuthCode) {
        this.createAuthCode = createAuthCode;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isRepetition() {
        return isRepetition;
    }

    public void setRepetition(boolean repetition) {
        isRepetition = repetition;
    }
}
