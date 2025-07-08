package com.example.tuibikho.data;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "your_pet")
public class PetEntity {
    @PrimaryKey
    private int petId;
    private String petName;
    private String petImage;
    private String petAge;
    private String petGender;

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }
    public void setPetName(String petName) {
        this.petName = petName;
    }
    public String getPetImage() {
        return petImage;
    }
    public void setPetImage(String petImage) {
        this.petImage = petImage;

    }
    public String getPetAge() {
        return petAge;
    }
    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }
    public String getPetGender() {
        return petGender;
    }
    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }
}
