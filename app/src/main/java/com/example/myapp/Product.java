package com.example.myapp;

public class Product {
    private String name;
    private String imgUrl;
    private String price;
    private String category;
    private String details;

    public Product(String name, String imgUrl, String price, String category, String details) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.price = price;
        this.category = category;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
