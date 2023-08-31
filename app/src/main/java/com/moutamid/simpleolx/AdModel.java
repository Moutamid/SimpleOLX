package com.moutamid.simpleolx;

import java.util.ArrayList;
import java.util.List;

public class AdModel {
    private String adId;
    private String title;
    private String category;
    private String description;
    private String contact;
    private String SellerUid;
    private ArrayList<String> images;
    private boolean approved;

    public AdModel() {
    }

    public AdModel(String adId, String title, String category, String description, String contact, String SellerUid, List<String> images, boolean approved) {
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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
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
}

