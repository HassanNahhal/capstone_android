

package com.conestogac.receipt_keeper.models;

public class Store {

    private int id;
    private String name;

    //For Sync
    private String r_id;
    private boolean isSync;

    public Store() {

    }

    public Store(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Attribute Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    // Attribute Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Attribute For Sync
    public String getR_id() {
        return r_id;
    }
    public boolean getIsSync() {
        return isSync;
    }

    public void setR_id(String id) {
        this.r_id = r_id;
    }
    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }
}
