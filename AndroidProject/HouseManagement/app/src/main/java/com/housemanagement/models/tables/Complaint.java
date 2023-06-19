package com.housemanagement.models.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.housemanagement.db.DateConverter;

import java.time.LocalDate;
@Entity (tableName = "Complaint")
public class Complaint {
    @PrimaryKey
    private Integer id;
    private Integer idFlat;
    private String status;
    private String description;
    private String photo;

    @Ignore
    private String dateToServer;
        @TypeConverters(DateConverter.class)
        private LocalDate date;

    public Complaint(Integer id, Integer idFlat, String status, String description, String photo, LocalDate date) {
        this.id = id;
        this.idFlat = idFlat;
        this.status = status;
        this.description = description;
        this.photo = photo;
        this.date = date;
    }

    public Complaint(Integer id, Integer idFlat, String status, String description, String photo, String dateToServer) {
        this.id = id;
        this.idFlat = idFlat;
        this.status = status;
        this.description = description;
        this.photo = photo;
        this.dateToServer = dateToServer;
    }
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

    public Complaint() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
