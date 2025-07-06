package com.example.tuibikho.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPet(PetEntity pet);

    @Update
    void updatePet(PetEntity pet);

    @Delete
    void deletePet(PetEntity pet);

    @Query("SELECT * FROM your_pet ORDER BY petId ASC")
    LiveData<List<PetEntity>> getAllPets();

    @Query("SELECT COUNT(*) FROM your_pet")
    LiveData<Integer> getPetCount();

    @Query("SELECT * FROM your_pet WHERE petId = :petId LIMIT 1")
    PetEntity getPetById(int petId);
} 