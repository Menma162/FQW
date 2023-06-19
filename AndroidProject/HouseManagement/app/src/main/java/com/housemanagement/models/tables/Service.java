package com.housemanagement.models.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "Service")
public class Service {
    @PrimaryKey
    private Integer id;
    private String nameService;
    private String nameCounter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameService() {
        return nameService;
    }

    public void setNameService(String nameService) {
        this.nameService = nameService;
    }

    public String getNameCounter() {
        return nameCounter;
    }

    public void setNameCounter(String nameCounter) {
        this.nameCounter = nameCounter;
    }

    public Service() {
    }

    public Service(Integer id, String nameService, String nameCounter) {
        this.id = id;
        this.nameService = nameService;
        this.nameCounter = nameCounter;
    }
}
