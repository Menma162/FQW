package com.housemanagement.models.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.housemanagement.db.DateConverter;

import java.time.LocalDate;

@Entity(tableName = "Advertisement")
public class Advertisement {
    @PrimaryKey
    private Integer id;
    @ColumnInfo
    private String description;
    @TypeConverters(DateConverter.class)
    private LocalDate date;
    @ColumnInfo
    private  Integer idHouse;

    public Advertisement(Integer id, String description, LocalDate date, Integer idHouse) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.idHouse = idHouse;
    }

    public Advertisement() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getIdHouse() {
        return idHouse;
    }

    public void setIdHouse(Integer idHouse) {
        this.idHouse = idHouse;
    }
}
