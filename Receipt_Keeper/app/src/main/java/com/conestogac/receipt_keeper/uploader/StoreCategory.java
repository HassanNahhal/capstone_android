package com.conestogac.receipt_keeper.uploader;

/**
 * Created by infomat on 16-07-11.
 */
public class StoreCategory  extends com.strongloop.android.loopback.User {

    private String storeId;
    private String categoryId;

    public StoreCategory() {
    }


    // Attribute Getters
    public String getStoreId() {
        return storeId;
    }

    public String getCategoryId() {
        return categoryId;
    }


    // Attribute Setters
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


}
