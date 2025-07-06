package com.example.tuibikho.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveUser(String userId, String email, String imgAvatar, String userName, Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("imgAvatar", imgAvatar);
        user.put("userName", userName);
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.run();
                });
    }

    public void updateUserName(String userId, String newUserName, Runnable onSuccess, Runnable onFailure) {
        db.collection("users").document(userId)
                .update("userName", newUserName)
                .addOnSuccessListener(aVoid -> { if (onSuccess != null) onSuccess.run(); })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.run(); });
    }

    public void updateImgAvatar(String userId, String newImgAvatar, Runnable onSuccess, Runnable onFailure) {
        db.collection("users").document(userId)
                .update("imgAvatar", newImgAvatar)
                .addOnSuccessListener(aVoid -> { if (onSuccess != null) onSuccess.run(); })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.run(); });
    }
}
