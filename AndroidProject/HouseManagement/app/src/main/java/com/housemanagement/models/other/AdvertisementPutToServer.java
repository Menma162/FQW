package com.housemanagement.models.other;

public class AdvertisementPutToServer {
    private Integer id;
    private String description;
    private Integer idHouse;
    private String date;

    public AdvertisementPutToServer() {
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

    public Integer getIdHouse() {
        return idHouse;
    }

    public void setIdHouse(Integer idHouse) {
        this.idHouse = idHouse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AdvertisementPutToServer(Integer id, String description, Integer idHouse, String date) {
        this.id = id;
        this.description = description;
        this.idHouse = idHouse;
        this.date = date;
    }
}
