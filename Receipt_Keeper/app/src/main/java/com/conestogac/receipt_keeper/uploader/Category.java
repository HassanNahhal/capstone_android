package com.conestogac.receipt_keeper.uploader;

/**
 * Created by infomat on 16-07-11.
 */
public class Category  extends com.strongloop.android.loopback.User {

    private String id;
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }


    // Attribute Getters
    public String getCategoryId() {
        return id;
    }

    public String getCategoryName() {
        return name;
    }


    // Attribute Setters
    public void setCategoryId(String id) {
        this.id = id;
    }

    public void setCategoryName(String name) {
        this.name = name;
    }
}
