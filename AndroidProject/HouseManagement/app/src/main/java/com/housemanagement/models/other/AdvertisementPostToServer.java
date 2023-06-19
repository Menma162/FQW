package com.housemanagement.models.other;

import java.util.ArrayList;

public class AdvertisementPostToServer {
    private Integer id;
    private String description;
    private ArrayList<Integer> idHouses;
    private String date;

    public AdvertisementPostToServer() {
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

    public ArrayList<Integer> getIdHouses() {
        return idHouses;
    }

    public void setIdHouses(ArrayList<Integer> idHouse) {
        this.idHouses = idHouse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AdvertisementPostToServer(Integer id, String description, ArrayList<Integer> idHouses, String date) {
        this.id = id;
        this.description = description;
        this.idHouses = idHouses;
        this.date = date;
    }
}
