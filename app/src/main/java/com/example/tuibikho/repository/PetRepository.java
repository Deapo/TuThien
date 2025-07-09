package com.example.tuibikho.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PetRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PetRepository() {
    }

    public void addOrUpdatePet(String userId, String petKey, String name, int age, String gender, String imgAvatar, String birthday, String healthStatus, String vaccinationSchedule, Runnable onSuccess, Runnable onFailure) {
        DocumentReference petRef = db.collection("your_pet").document(userId);
        petRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> pets = (Map<String, Object>) documentSnapshot.getData();
            if (pets == null) pets = new HashMap<>();
            Map<String, Object> pet = new HashMap<>();
            pet.put("name", name);
            pet.put("age", age);
            pet.put("gender", gender);
            pet.put("imgAvatar", imgAvatar);
            pet.put("birthday", birthday);
            pet.put("healthStatus", healthStatus);
            pet.put("vaccinationSchedule", vaccinationSchedule); // dạng JSON string hoặc Map
            pets.put(petKey, pet);
            petRef.set(pets)
                .addOnSuccessListener(aVoid -> { if (onSuccess != null) onSuccess.run(); })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.run(); });
        });
    }

    public void removePet(String userId, String petKey, Runnable onSuccess, Runnable onFailure) {
        DocumentReference petRef = db.collection("your_pet").document(userId);
        petRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> pets = (Map<String, Object>) documentSnapshot.getData();
            if (pets != null && pets.containsKey(petKey)) {
                pets.remove(petKey);
                petRef.set(pets)
                    .addOnSuccessListener(aVoid -> { if (onSuccess != null) onSuccess.run(); })
                    .addOnFailureListener(e -> { if (onFailure != null) onFailure.run(); });
            }
        });
    }

    public LiveData<Map<String, Map<String, Object>>> getAllPets(String userId) {
        MutableLiveData<Map<String, Map<String, Object>>> petsLiveData = new MutableLiveData<>();
        db.collection("your_pet").document(userId)
            .addSnapshotListener((documentSnapshot, e) -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Map<String, Object> pets = documentSnapshot.getData();
                    Map<String, Map<String, Object>> result = new HashMap<>();
                    if (pets != null) {
                        for (Map.Entry<String, Object> entry : pets.entrySet()) {
                            Object value = entry.getValue();
                            if (value instanceof Map) {
                                Map<String, Object> pet = (Map<String, Object>) value;
                                result.put(entry.getKey(), pet);
                            }
                        }
                    }
                    petsLiveData.setValue(result);
                } else {
                    petsLiveData.setValue(new HashMap<>());
                }
            });
        return petsLiveData;
    }
}
