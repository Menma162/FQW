package com.housemanagement.models.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Flat")
public class Flat {
    @PrimaryKey
    private Integer id;
    private String personalAccount;
    private String flatNumber;
    private Double totalArea;
    private Double usableArea;
    private Integer entranceNumber;
    private Integer numberOfRooms;
    private Integer numberOfRegisteredResidents;
    private Integer numberOfOwners;
    private Integer idHouse;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(String personalAccount) {
        this.personalAccount = personalAccount;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public Double getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Double totalArea) {
        this.totalArea = totalArea;
    }

    public Double getUsableArea() {
        return usableArea;
    }

    public void setUsableArea(Double usableArea) {
        this.usableArea = usableArea;
    }

    public Integer getEntranceNumber() {
        return entranceNumber;
    }

    public void setEntranceNumber(Integer entranceNumber) {
        this.entranceNumber = entranceNumber;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getNumberOfRegisteredResidents() {
        return numberOfRegisteredResidents;
    }

    public void setNumberOfRegisteredResidents(int numberOfRegisteredResidents) {
        this.numberOfRegisteredResidents = numberOfRegisteredResidents;
    }

    public int getNumberOfOwners() {
        return numberOfOwners;
    }

    public void setNumberOfOwners(int numberOfOwners) {
        this.numberOfOwners = numberOfOwners;
    }

    public Flat() {
    }

    public Flat(Integer id, String personalAccount, String flatNumber, Double totalArea, Double usableArea, Integer entranceNumber, Integer numberOfRooms, Integer numberOfRegisteredResidents, Integer numberOfOwners, Integer idHouse) {
        this.id = id;
        this.personalAccount = personalAccount;
        this.flatNumber = flatNumber;
        this.totalArea = totalArea;
        this.usableArea = usableArea;
        this.entranceNumber = entranceNumber;
        this.numberOfRooms = numberOfRooms;
        this.numberOfRegisteredResidents = numberOfRegisteredResidents;
        this.numberOfOwners = numberOfOwners;
        this.idHouse = idHouse;
    }

    public Integer getIdHouse() {
        return idHouse;
    }

    public void setIdHouse(Integer idHouse) {
        this.idHouse = idHouse;
    }
}
