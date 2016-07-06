package com.conestogac.receipt_keeper.models;

/**
 * Created by hassannahhal on 2016-06-14.
 */
public class Tag {

    private int tagId;
    private String tagName;

    public Tag() {
    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }


    // Attribute Getters
    public int getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }


    // Attribute Setters
    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
