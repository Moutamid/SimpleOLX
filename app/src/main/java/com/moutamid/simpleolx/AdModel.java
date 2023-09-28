package com.moutamid.simpleolx;

import java.util.ArrayList;
import java.util.List;

public class AdModel {
    public String adId;
    public String title;
    public String category;
    public String description;
    public String contact;
    public String city;
    public String province;
    private String SellerUid;
    private List<String> images;
    private String approved;
    String Host, Company, New_category, From_date, To_date, Time;

    public AdModel(String adId, String title, String category, String description, String contact, String sellerUid, List<String> images, String approved, String host, String company, String new_category, String from_date, String to_date, String time, String city, String province) {
        this.adId = adId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.contact = contact;
        SellerUid = sellerUid;
        this.images = images;
        this.approved = approved;
        Host = host;
        Company = company;
        New_category = new_category;
        From_date = from_date;
        To_date = to_date;
        Time = time;
        this.city = city;
        this.province = province;
    }

    public AdModel() {
    }

    public AdModel(String adId, String title, String category, String description, String contact, String SellerUid, List<String> images, String approved) {
        this.adId = adId;
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.SellerUid = SellerUid;
        this.images = (ArrayList<String>) images;
        this.category = category;
        this.approved = approved;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContact() {
        return contact;
    }

    public List<String> getImages() {
        return images;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImages(List<String> images) {
        this.images = (ArrayList<String>) images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String isApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getSellerUid() {
        return SellerUid;
    }

    public void setSellerUid(String sellerUid) {
        this.SellerUid = sellerUid;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getNew_category() {
        return New_category;
    }

    public void setNew_category(String new_category) {
        New_category = new_category;
    }

    public String getFrom_date() {
        return From_date;
    }

    public void setFrom_date(String from_date) {
        From_date = from_date;
    }

    public String getTo_date() {
        return To_date;
    }

    public void setTo_date(String to_date) {
        To_date = to_date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}

