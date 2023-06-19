package com.housemanagement.models.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SettingsService")
public class SettingsService {
    @PrimaryKey
    private Integer id;
    private Integer idService;
    private boolean paymentStatus;
    private boolean typeIMD;
    private Double valueRate;
    private Double valueNormative;
    private Boolean haveCounter;
    private Integer startDateTransfer;
    private Integer endDateTransfer;
    private String paymentPeriod;

    public SettingsService(Integer id, Integer idService, boolean paymentStatus, boolean typeIMD, Double valueRate, Double valueNormative, boolean haveCounter, Integer startDateTransfer, Integer endDateTransfer, String paymentPeriod) {
        this.id = id;
        this.idService = idService;
        this.paymentStatus = paymentStatus;
        this.typeIMD = typeIMD;
        this.valueRate = valueRate;
        this.valueNormative = valueNormative;
        this.haveCounter = haveCounter;
        this.startDateTransfer = startDateTransfer;
        this.endDateTransfer = endDateTransfer;
        this.paymentPeriod = paymentPeriod;
    }

    public SettingsService() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public Double getValueRate() {
        return valueRate;
    }

    public void setValueRate(Double valueRate) {
        this.valueRate = valueRate;
    }

    public Double getValueNormative() {
        return valueNormative;
    }

    public void setValueNormative(Double valueNormative) {
        this.valueNormative = valueNormative;
    }

    public Boolean isHaveCounter() {
        return haveCounter;
    }

    public void setHaveCounter(Boolean haveCounter) {
        this.haveCounter = haveCounter;
    }

    public Integer getStartDateTransfer() {
        return startDateTransfer;
    }

    public void setStartDateTransfer(Integer startDateTransfer) {
        this.startDateTransfer = startDateTransfer;
    }

    public Integer getEndDateTransfer() {
        return endDateTransfer;
    }

    public void setEndDateTransfer(Integer endDateTransfer) {
        this.endDateTransfer = endDateTransfer;
    }

    public String getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(String paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    public Integer getIdService() {
        return idService;
    }

    public void setIdService(Integer idService) {
        this.idService = idService;
    }

    public boolean isTypeIMD() {
        return typeIMD;
    }

    public void setTypeIMD(boolean typeIMD) {
        this.typeIMD = typeIMD;
    }
}
