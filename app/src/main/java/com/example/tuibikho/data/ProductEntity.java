package com.example.tuibikho.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Entity(tableName = "products")
@IgnoreExtraProperties
public class ProductEntity {
    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private String type;
    private String category;
    private String description;
    private double price;
    private String stock;
    private String imageURL;
    private List<String> benefits;
    private List<String> ingredients;
    private List<NutritionInfo> nutritions;
    @Ignore
    private Timestamp createdAt;
    @Ignore
    private Timestamp updatedAt;

    public ProductEntity() {}


    public String getId() {return id;}
    public void setId(String id){this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}

    public String getStock() {return stock;}
    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getImageURL() {return imageURL;}
    public void setImageURL(String imageURL){this.imageURL = imageURL;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public List<String> getIngredients() {return ingredients;}
    public void setIngredients(List<String> ingredients) {this.ingredients = ingredients;}

    public List<String> getBenefits() {return benefits;}
    public void setBenefits(List<String> benefits) {this.benefits = benefits;}

    public List<NutritionInfo> getNutritions() {return nutritions;}
    public void setNutritions(List<NutritionInfo> nutritions) {this.nutritions = nutritions;}

}


