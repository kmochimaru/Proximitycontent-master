package com.estimote.proximitycontent.model;

/**
 * Created by PEEPO on 25/3/2561.
 */

public class LogModel {
    private String email;
    private String date;
    private String time;
    private String action;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LogModel(String email, String date, String time, String action) {

        this.email = email;
        this.date = date;
        this.time = time;
        this.action = action;
    }
}
