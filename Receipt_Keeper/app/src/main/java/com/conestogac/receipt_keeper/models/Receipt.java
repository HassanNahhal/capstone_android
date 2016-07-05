package com.conestogac.receipt_keeper.models;

import com.strongloop.android.loopback.Model;

import java.util.Date;

/**
 * Created by hassannahhal on 2016-06-14.
 */
public class Receipt extends Model {

    private int id;
    private String customerId;
    private int storeId;
    private int categoryId;
    private Date date;
    private float total;
    private int numberOfItem;
    private boolean error;
    private String comment;
    private String imageFilePath;
    private String groupId;

    public Receipt() {

    }

    //todo check contructor parameter
    public Receipt(int id, String customerId, int storeId, int categoryId, String comment, Date date, float total, String imageFilePath) {
        this.id = id;
        this.customerId = customerId;
        this.storeId = storeId;
        this.categoryId = categoryId;
        this.comment = comment;
        this.date = date;
        this.total = total;
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

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public float getTotal() {
        return total;
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
        this.categoryId = categoryId;
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
}
