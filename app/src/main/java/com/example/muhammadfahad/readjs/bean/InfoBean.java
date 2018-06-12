package com.example.muhammadfahad.readjs.bean;

public class InfoBean {
    private String mobileNo;
    private String cnicNo;
    private String channelId;
    private String income;

    public InfoBean(String mobileNo, String cnicNo, String channelId, String income) {
        this.mobileNo = mobileNo;
        this.cnicNo = cnicNo;
        this.channelId = channelId;
        this.income = income;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCnicNo() {
        return cnicNo;
    }

    public void setCnicNo(String cnicNo) {
        this.cnicNo = cnicNo;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }
}