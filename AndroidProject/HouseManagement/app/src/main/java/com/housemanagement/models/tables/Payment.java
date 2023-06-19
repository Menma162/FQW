package com.housemanagement.models.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Payment")
public class Payment {
    @PrimaryKey
    private Integer id;
    private Integer idFlat;
    private Integer idService;
    private String period;
    private Double amount;
    private Double penalties;

    public Payment(Integer id, Integer idFlat, Integer idService, String period, Double amount, Double penalties) {
        this.id = id;
        this.idFlat = idFlat;
        this.idService = idService;
        this.period = period;
        this.amount = amount;
        this.penalties = penalties;
    }

    public Payment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPenalties() {
        return penalties;
    }

    public void setPenalties(Double penalties) {
        this.penalties = penalties;
    }
}
