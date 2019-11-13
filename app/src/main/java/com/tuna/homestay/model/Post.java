package com.tuna.homestay.model;

import com.google.firebase.Timestamp;

public class Post extends Model {
    private String idUser, imgHomestay, phone, commune, district, province, longitude, latitude, description;
    private long price, acreage;
    private Timestamp date;

    public Post() {
    }

    public Post(String idUser, String imgHomestay, String phone, String commune, String district, String province, String longitude, String latitude, String description, long price, long acreage, Timestamp date) {
        this.idUser = idUser;
        this.imgHomestay = imgHomestay;
        this.phone = phone;
        this.commune = commune;
        this.district = district;
        this.province = province;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.price = price;
        this.acreage = acreage;
        this.date = date;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getImgHomestay() {
        return imgHomestay;
    }

    public void setImgHomestay(String imgHomestay) {
        this.imgHomestay = imgHomestay;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getAcreage() {
        return acreage;
    }

    public void setAcreage(long acreage) {
        this.acreage = acreage;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
