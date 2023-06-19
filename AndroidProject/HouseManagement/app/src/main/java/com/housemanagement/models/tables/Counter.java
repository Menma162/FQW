package com.housemanagement.models.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.housemanagement.db.DateConverter;

import java.time.LocalDate;
@Entity(tableName = "Counter")
public class Counter {
    @PrimaryKey
    private Integer id;
    private String number;
    private Boolean used;
    private Integer idFlat;
    private Integer idService;
    private Integer idHouse;
    private Boolean IMDOrGHMD;
    @TypeConverters(DateConverter.class)
    private LocalDate dateLastVerification;
    @TypeConverters(DateConverter.class)
    private LocalDate dateNextVerification;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Integer getIdFlat() {
        return idFlat;
    }

    public void setIdFlat(Integer idFlat) {
        this.idFlat = idFlat;
    }

    public Integer getIdService() {
        return idService;
    }

    public void setIdService(Integer idService) {
        this.idService = idService;
    }

    public Boolean getIMDOrGHMD() {
        return IMDOrGHMD;
    }

    public void setIMDOrGHMD(Boolean IMDOrGHMD) {
        this.IMDOrGHMD = IMDOrGHMD;
    }

    public LocalDate getDateLastVerification() {
        return dateLastVerification;
    }

    public void setDateLastVerification(LocalDate dateLastVerification) {
        this.dateLastVerification = dateLastVerification;
    }

    public LocalDate getDateNextVerification() {
        return dateNextVerification;
    }

    public void setDateNextVerification(LocalDate dateNextVerification) {
        this.dateNextVerification = dateNextVerification;
    }

    public Counter(Integer id, String number, Boolean used, Integer idFlat, Integer idService, Integer idHouse, Boolean IMDOrGHMD, LocalDate dateLastVerification, LocalDate dateNextVerification) {
        this.id = id;
        this.number = number;
        this.used = used;
        this.idFlat = idFlat;
        this.idService = idService;
        this.idHouse = idHouse;
        this.IMDOrGHMD = IMDOrGHMD;
        this.dateLastVerification = dateLastVerification;
        this.dateNextVerification = dateNextVerification;
    }

    public Counter() {
    }

    public Integer getIdHouse() {
        return idHouse;
    }

    public void setIdHouse(Integer idHouse) {
        this.idHouse = idHouse;
    }
}
