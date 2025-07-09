package com.example.tuibikho.data;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.Period;
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
    private String birthday;
    private String healthStatus;
    private String vaccinationSchedule; // JSON string hoặc custom object

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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getVaccinationSchedule() {
        return vaccinationSchedule;
    }

    public void setVaccinationSchedule(String vaccinationSchedule) {
        this.vaccinationSchedule = vaccinationSchedule;
    }

    public int calculateAge() {
        if (birthday == null || birthday.isEmpty()) return 0;
        try {
            LocalDate birthDate = LocalDate.parse(birthday);
            LocalDate now = LocalDate.now();
            return Period.between(birthDate, now).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
}
