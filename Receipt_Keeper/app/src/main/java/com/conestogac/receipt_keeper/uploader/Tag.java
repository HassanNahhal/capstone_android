package com.conestogac.receipt_keeper.uploader;

/**
 * Created by hassannahhal on 2016-06-14.
 */
public class Tag extends com.strongloop.android.loopback.Model {

    private String id;
    private String tagName;


    public Tag() {
    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }


    // Attribute Getters
    public String getTagId() {
        return id;
    }

    public String getTagName() {
        return tagName;
    }


    // Attribute Setters
    public void setTagId(String id) {
        this.id = id;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
