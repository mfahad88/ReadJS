package com.example.muhammadfahad.readjs.bean;

public class PlaceBean {
    private String placeId;
    private String placeName;
    private String address;
    private String placeLocation;
    private String attributions;
    private String viewPort;
    private String phoneNumber;
    private String placeTypes;
    private String priceLevel;
    private String rating;
    private String websiteUri;



    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceLocation() {
        return placeLocation;
    }

    public void setPlaceLocation(String placeLocation) {
        this.placeLocation = placeLocation;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public String getViewPort() {
        return viewPort;
    }

    public void setViewPort(String viewPort) {
        this.viewPort = viewPort;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(String placeTypes) {
        this.placeTypes = placeTypes;
    }

    public String getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(String priceLevel) {
        this.priceLevel = priceLevel;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    @Override
    public String toString() {
        return "PlaceBean{" +
                "placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", address='" + address + '\'' +
                ", placeLocation='" + placeLocation + '\'' +
                ", attributions='" + attributions + '\'' +
                ", viewPort='" + viewPort + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", placeTypes='" + placeTypes + '\'' +
                ", priceLevel='" + priceLevel + '\'' +
                ", rating='" + rating + '\'' +
                ", websiteUri='" + websiteUri + '\'' +
                '}';
    }
}