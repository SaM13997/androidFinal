package com.example.decorshop;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DecorItem {
    private String name;
    private String image;
    private String price;
    public DecorItem(QueryDocumentSnapshot document) {
        this.name = document.getString("Name");
        this.price = document.getString("Price");
        this.image = document.getString("Image");
    }
    public DecorItem(String name, String price, String image) {
        this.name = name;
        this.image = image;
        this.price = price;
    }
    public String getName() {
        return name;
    }
    public String getPrice() {
        return price;
    }
    public String getImage() {
        return image;
    }
}
