package com.housemanagement.models.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.housemanagement.db.DateConverter;

import java.time.LocalDate;
@Entity(tableName = "Indication")
public class Indication {
    @PrimaryKey
    private Integer id;
    private Integer idCounter;
    @TypeConverters(DateConverter.class)
    private LocalDate dateTransfer;
    private Integer value;

    public Indication() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(Integer idCounter) {
        this.idCounter = idCounter;
    }

    public LocalDate getDateTransfer() {
        return dateTransfer;
    }

    public void setDateTransfer(LocalDate dateTransfer) {
        this.dateTransfer = dateTransfer;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Indication(Integer id, Integer idCounter, LocalDate dateTransfer, Integer value) {
        this.id = id;
        this.idCounter = idCounter;
        this.dateTransfer = dateTransfer;
        this.value = value;
    }
}
