package com.example.muhammadfahad.readjs.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fahad on 01/03/2018.
 */

public class DataBean {
    private int catId;
    private int recId;
    private String attribute;
    private String value;
    private int status;
    private String mobileIMEI;
    private String recordDate;
    private InfoBean infoBean;

    public DataBean(int catId, int recId, String attribute, String value, int status, String mobileIMEI, String recordDate, InfoBean infoBean) {
        this.catId = catId;
        this.recId = recId;
        this.attribute = attribute;
        this.value = value;
        this.status = status;
        this.mobileIMEI = mobileIMEI;
        this.recordDate = recordDate;
        this.infoBean = infoBean;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMobileIMEI() {
        return mobileIMEI;
    }

    public void setMobileIMEI(String mobileIMEI) {
        this.mobileIMEI = mobileIMEI;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public InfoBean getInfoBean() {
        return infoBean;
    }

    public void setInfoBean(InfoBean infoBean) {
        this.infoBean = infoBean;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "catId=" + catId +
                ", recId=" + recId +
                ", attribute='" + attribute + '\'' +
                ", value='" + value + '\'' +
                ", status=" + status +
                ", mobileIMEI='" + mobileIMEI + '\'' +
                ", recordDate='" + recordDate + '\'' +
                ", infoBean=" + infoBean +
                '}';
    }
}
