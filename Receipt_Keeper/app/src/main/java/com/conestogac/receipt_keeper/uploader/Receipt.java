package com.conestogac.receipt_keeper.uploader;

import com.strongloop.android.loopback.Model;

import java.util.Date;


public class Receipt extends Model {

    private String id;
    private String customerId;
    private String storeId;
    private String categoryId;
    private String comment;
    private Date date;
    private float total;
    private String tagId;


    public Receipt() {

    }


    public Receipt(String id, String customerId, String storeId, String categoryId, String comment, Date date, float total, String tagId) {
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
    public String getLocalId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public float getTotal() {
        return total;
    }

    public String getTagId() {
        return tagId;
    }

    // Attribute Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setCategoryId(String categroyId) {
        this.categoryId = categroyId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
