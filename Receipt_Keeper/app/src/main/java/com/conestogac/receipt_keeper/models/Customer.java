package com.conestogac.receipt_keeper.models;

/**
 * Created by hassannahhal on 2016-06-14.
 */
public class Customer  {

    private int id;
    private String email;
    private String name;
    private String pathToImage;
    private int groupId;

    //For Sync
    private String r_id;
    private String r_groupId;
    private boolean isSync;

    public Customer() {

    }

    public Customer(int id, String email, String name, String pathToImage, int groupId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.pathToImage = pathToImage;
        this.groupId = groupId;
    }


    // Attribute Getters
    public int getLocalId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public int getGroupId() {
        return groupId;
    }

    // Attribute Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}

