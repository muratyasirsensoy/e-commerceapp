package com.example.dgpaysproje.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("price")
    public Object price;

    @SerializedName("description")
    public String description;

    @SerializedName("images")
    public List<String> images;

    public ProductModel(String title, Object price, String description, String imageUrl) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.images = List.of(imageUrl);
    }
}

