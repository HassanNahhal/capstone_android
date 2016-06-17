package edu.sfsu.cs.orange.ocr;

/**
 * Created by hassannahhal on 2016-06-14.
 */
public class Tag {

    private int tagId;
    private int tagName;

    public Tag() {
    }

    public Tag(int tagId, int tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }


    // Attribute Getters
    public int getTagId() {
        return tagId;
    }

    public int getTagName() {
        return tagName;
    }


    // Attribute Setters
    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public void setTagName(int tagName) {
        this.tagName = tagName;
    }
}
