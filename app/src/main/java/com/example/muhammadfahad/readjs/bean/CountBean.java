package com.example.muhammadfahad.readjs.bean;

public class CountBean {

    private int contact,call,sms,sensor,location,device,calendarEvents,battery;

    public int getContact() {
        return contact;
    }

    public void setContact(int contact) {
        this.contact = contact;
    }

    public int getCall() {
        return call;
    }

    public void setCall(int call) {
        this.call = call;
    }

    public int getSms() {
        return sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public int getSensor() {
        return sensor;
    }

    public void setSensor(int sensor) {
        this.sensor = sensor;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public int getCalendarEvents() {
        return calendarEvents;
    }

    public void setCalendarEvents(int calendarEvents) {
        this.calendarEvents = calendarEvents;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    @Override
    public String toString() {
        return "CountBean{" +
                "contact=" + contact +
                ", call=" + call +
                ", sms=" + sms +
                ", sensor=" + sensor +
                ", location=" + location +
                ", device=" + device +
                ", calendarEvents=" + calendarEvents +
                ", battery=" + battery +
                '}';
    }
}