package com.housemanagement.models.other;

import androidx.room.Ignore;
import androidx.room.TypeConverters;

import com.housemanagement.db.DateConverter;

import java.time.LocalDate;

public class ComplaintToServer {
    private Integer id;
    private Integer idFlat;
    private String status;
    private String description;
    private String photo;
    private String date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdFlat() {
        return idFlat;
    }

    public void setIdFlat(Integer idFlat) {
        this.idFlat = idFlat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ComplaintToServer(Integer id, Integer idFlat, String status, String description, String photo, String date) {
        this.id = id;
        this.idFlat = idFlat;
        this.status = status;
        this.description = description;
        this.photo = photo;
        this.date = date;
    }
}
