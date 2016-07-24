package com.conestogac.receipt_keeper.uploader;

/**
 * Created by infomat on 16-07-22.
 */
public class ReceiptTag  extends com.strongloop.android.loopback.Model {

    private String receiptId;
    private String tagId;

    public ReceiptTag() {
    }


    // Attribute Getters
    public String getReceiptId() {
        return receiptId;
    }

    public String getTagId() {
        return tagId;
    }


    // Attribute Setters
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }


}