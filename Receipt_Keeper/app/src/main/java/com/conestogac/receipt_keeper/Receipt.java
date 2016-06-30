package com.conestogac.receipt_keeper;

import java.util.Date;

/**
 * Created by hassannahhal on 2016-06-14.
 */
public class Receipt {

    private int id;
    private int customerId;
    private int storeId;
    private int categroyId;
    private String comment;
    private Date date;
    private float total;


    public Receipt() {
    }


    public Receipt(int id, int customerId, int storeId, int categroyId, String comment, Date date, float total) {
        this.id = id;
        this.customerId = customerId;
        this.storeId = storeId;
        this.categroyId = categroyId;
        this.comment = comment;
        this.date = date;
        this.total = total;
    }




    // Attribute Getters
    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getCategroyId() {
        return categroyId;
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

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setCategroyId(int categroyId) {
        this.categroyId = categroyId;
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
