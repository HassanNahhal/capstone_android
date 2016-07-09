package com.conestogac.receipt_keeper.models;

public class Receipt {

    private int id;
    private String customerId;
    private int storeId;
    private int categoryId;
    private String comment;
    private String date;
    private float total;
    private int tagId;
    private String createDate;
    private String imagePath; //todo add to table column at DB Helper

    //For Sync
    private String r_id;
    private String r_categoryId;
    private String r_tagId;
    private boolean isSync;

    public Receipt() {

    }


    public Receipt(int id, String customerId, int storeId, int categoryId, String comment, String date, float total, int tagId) {
        this.id = id;
        this.customerId = customerId;
        this.storeId = storeId;
        this.categoryId = categoryId;
        this.comment = comment;
        this.date = date;
        this.total = total;
        this.tagId = tagId;
    }


    // Attribute Getters
    public int getLocalId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getCreateDate() {
        return createDate;
    }

    public float getTotal() {
        return total;
    }

    public int getTagId() {
        return tagId;
    }

    // Attribute Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setCategoryId(int categroyId) {
        this.categoryId = categroyId;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCreateDate(String date) {
        this.createDate = date;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
