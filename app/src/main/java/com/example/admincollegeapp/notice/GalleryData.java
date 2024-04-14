package com.example.admincollegeapp.notice;

public class GalleryData {
    private String imageUrl;
    private String category;

    public GalleryData() {
        // Default constructor required for Firebase
    }

    public GalleryData(String imageUrl, String category) {
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
