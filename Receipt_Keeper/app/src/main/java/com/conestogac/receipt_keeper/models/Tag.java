package com.conestogac.receipt_keeper.models;

/**
 * Created by hassannahhal on 2016-06-14.
 */
public class Tag {

    private int id;
    private String tagName;

    //For Sync
    private String r_id;
    private boolean isSync;

    public Tag() {
    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }


    // Attribute Getters
    public int getTagId() {
        return id;
    }
    public String getTagName() {
        return tagName;
    }


    // Attribute Setters
    public void setTagId(int id) {
        this.id = id;
    }
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    // Attribute For Sync
    public void setR_id(String r_id) {
        this.r_id = r_id;
    }
    public String getR_id() {
        return this.r_id;
    }

    public boolean get_isSync() {
        return this.isSync;
    }
    public void set_isSync(boolean isSync) {
        this.isSync = isSync;
    }
}
