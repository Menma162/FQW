package com.housemanagement.models.other;

public class IndicationToServer {
    private Integer id;
    private Integer idCounter;
    private Integer value;
    private String dateTransfer;

    public IndicationToServer() {
    }

    public IndicationToServer(Integer id, Integer idCounter, Integer value, String date) {
        this.id = id;
        this.idCounter = idCounter;
        this.value = value;
        this.dateTransfer = date;
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDateTransfer() {
        return dateTransfer;
    }

    public void setDateTransfer(String dateTransfer) {
        this.dateTransfer = dateTransfer;
    }
}
