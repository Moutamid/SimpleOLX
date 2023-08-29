package com.moutamid.simpleolx;

import java.util.List;

public class AdModel {
    private String title;

    private String category;
    private String description;
    private String contact;
    private List<String> images;
    private boolean approved;

    public AdModel() {
    }

    public AdModel(String title, String category, String description, String contact, List<String> images, boolean approved) {
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.images = images;
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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}

