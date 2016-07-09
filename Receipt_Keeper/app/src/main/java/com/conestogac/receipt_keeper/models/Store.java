

package com.conestogac.receipt_keeper.models;

public class Store {

    private int id;
    private String name;

    //For Sync
    private String r_id;

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
    public String getR_id() {
        return r_id;
    }

    // Attribute Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setR_id(String id) {
        this.r_id = r_id;
    }
}
