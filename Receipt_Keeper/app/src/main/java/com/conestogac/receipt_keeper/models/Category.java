package com.conestogac.receipt_keeper.models;

/**
 * Created by hassannahhal on 2016-07-13.
 */
public class Category {

    private int id;
    private String name;

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
}
