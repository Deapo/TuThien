package com.example.tuibikho.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.tuibikho.repository.PetRepository;
import java.util.Map;

public class PetViewModel extends AndroidViewModel {
    private final PetRepository petRepository;

    public PetViewModel(@NonNull Application application) {
        super(application);
        petRepository = new PetRepository();
    }

    public void addOrUpdatePet(String userId, String petKey, String name, int age, String gender, String imgAvatar, String birthday, String healthStatus, String vaccinationSchedule, Runnable onSuccess, Runnable onFailure) {
        petRepository.addOrUpdatePet(userId, petKey, name, age, gender, imgAvatar, birthday, healthStatus, vaccinationSchedule, onSuccess, onFailure);
    }

    public void removePet(String userId, String petKey, Runnable onSuccess, Runnable onFailure) {
        petRepository.removePet(userId, petKey, onSuccess, onFailure);
    }

    public LiveData<Map<String, Map<String, Object>>> getAllPets(String userId) {
        return petRepository.getAllPets(userId);
    }
}
