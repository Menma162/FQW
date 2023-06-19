package com.housemanagement.models.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.housemanagement.db.DateConverter;

import java.time.LocalDate;

@Entity(tableName = "FlatOwner")
public class FlatOwner {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Integer idHouse;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FlatOwner() {
    }

    public FlatOwner(Integer id, String fullName, String phoneNumber, String email, Integer idHouse) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.idHouse = idHouse;
    }

    public Integer getIdHouse() {
        return idHouse;
    }

    public void setIdHouse(Integer idHouse) {
        this.idHouse = idHouse;
    }
}
