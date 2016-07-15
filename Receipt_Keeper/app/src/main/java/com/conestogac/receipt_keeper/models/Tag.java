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
    public boolean getIsSync() {
        return isSync;
    }

    // Attribute Setters
    public void setTagId(int id) {
        this.id = id;
    }
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }
}
