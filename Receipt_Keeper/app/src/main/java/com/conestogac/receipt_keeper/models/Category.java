package com.conestogac.receipt_keeper.models;

/**
 * Created by hassannahhal on 2016-07-13.
 */
public class Category {

    private int id;
    private String name;
    //For Sync
    private String r_id;
    private boolean isSync;

    public Category() {
    }

    // [ Getters ]
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // [ Setters]
    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Attribute For Sync
    public String getR_id() {
        return this.r_id;
    }
    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public boolean get_isSync() {
        return this.isSync;
    }
    public void set_isSync(boolean isSync) {
        this.isSync = isSync;
    }
}
