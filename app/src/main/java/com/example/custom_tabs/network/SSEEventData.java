package com.example.custom_tabs.network;

import lombok.Data;


public class SSEEventData {

    private STATUS status;
    private String id;
    private String firstName;
    private String lastName;

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastname) {
        this.lastName = lastname;
    }

    public SSEEventData(STATUS status) {
        this.status = status;
    }

    public SSEEventData(STATUS status, String id, String firstName, String lastname) {
        this.status = status;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastname;
    }
}


