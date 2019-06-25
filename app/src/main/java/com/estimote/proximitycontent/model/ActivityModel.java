package com.estimote.proximitycontent.model;

/**
 * Created by PEEPO on 1/15/2018.
 */

public class ActivityModel {

    String activity_name;
    String date_start;
    String date_end;
    String time_start;
    String time_end;
    String description;
    String id;
    String image_url;

    public ActivityModel() {
    }

    public ActivityModel(String activity_name, String date_start, String date_end, String time_start, String time_end, String description) {
        this.activity_name = activity_name;
        this.date_start = date_start;
        this.date_end = date_end;
        this.time_start = time_start;
        this.time_end = time_end;
        this.description = description;
    }

    public ActivityModel(String id, String activity_name, String date_start, String date_end, String time_start, String time_end, String description, String image_url) {
        this.id = id;
        this.activity_name = activity_name;
        this.date_start = date_start;
        this.date_end = date_end;
        this.time_start = time_start;
        this.time_end = time_end;
        this.description = description;
        this.image_url = image_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
