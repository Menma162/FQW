package com.housemanagement.models.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.housemanagement.db.DateConverter;

import java.time.LocalDate;

@Entity(tableName = "House")
public class House {
    @PrimaryKey
    private Integer id;
    @ColumnInfo
    private String name;

    public House(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public House() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
