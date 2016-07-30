package com.conestogac.receipt_keeper.uploader;


public class Customer extends com.strongloop.android.loopback.User {

    private String firstName;
    private String groupId;

    public Customer() {

    }

    // Attribute Getters
    public String getUsername() {
        return firstName;
    }
    public String getGroupId() {
        return groupId;
    }

    // Attribute Setters
    public void setUserName(String name) {
        this.firstName = name;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

