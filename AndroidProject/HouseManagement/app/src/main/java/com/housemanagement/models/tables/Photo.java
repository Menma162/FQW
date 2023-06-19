package com.housemanagement.models.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import kotlin.UByteArray;

@Entity(tableName = "Photo")
public class Photo {
    @PrimaryKey
    private Integer id;
    private String array;

    public Photo(Integer id, String array) {
        this.id = id;
        this.array = array;
    }

    public Photo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArray() {
        return array;
    }

    public void setArray(String array) {
        this.array = array;
    }
}
