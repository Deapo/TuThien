package com.example.tuibikho.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addToCart(String userId, String productId, int quantity, Runnable onSuccess, Runnable onFailure) {
        DocumentReference cartRef = db.collection("cart_items").document(userId);
        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            List<String> productIDs = (List<String>) documentSnapshot.get("productID");
            List<Long> quantities = (List<Long>) documentSnapshot.get("quantity");
            if (productIDs == null) productIDs = new ArrayList<>();
            if (quantities == null) quantities = new ArrayList<>();

            //Kiem tra san pham ton tai chua
            int index = productIDs.indexOf(productId);
            if (index >= 0) {
                quantities.set(index, quantities.get(index) + quantity);
            } else {
                productIDs.add(productId);
                quantities.add((long) quantity);
            }
            Map<String, Object> update = new HashMap<>();
            update.put("productID", productIDs);
            update.put("quantity", quantities);
            cartRef.set(update)
                .addOnSuccessListener(aVoid -> { if (onSuccess != null) onSuccess.run(); })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.run(); });
        });
    }

    public void removeFromCart(String userId, String productId, Runnable onSuccess, Runnable onFailure) {
        DocumentReference cartRef = db.collection("cart_items").document(userId);
        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            List<String> productIDs = (List<String>) documentSnapshot.get("productID");
            List<Long> quantities = (List<Long>) documentSnapshot.get("quantity");
            if (productIDs == null || quantities == null) return;
            int index = productIDs.indexOf(productId);
            if (index >= 0) {
                productIDs.remove(index);
                quantities.remove(index);
                Map<String, Object> update = new HashMap<>();
                update.put("productID", productIDs);
                update.put("quantity", quantities);
                cartRef.set(update)
                    .addOnSuccessListener(aVoid -> { if (onSuccess != null) onSuccess.run(); })
                    .addOnFailureListener(e -> { if (onFailure != null) onFailure.run(); });
            }
        });
    }

    public void updateQuantity(String userId, String productId, int newQuantity, Runnable onSuccess, Runnable onFailure) {
        DocumentReference cartRef = db.collection("cart_items").document(userId);
        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            List<String> productIDs = (List<String>) documentSnapshot.get("productID");
            List<Long> quantities = (List<Long>) documentSnapshot.get("quantity");

            if (productIDs == null || quantities == null) return;

            int index = productIDs.indexOf(productId);
            if (index >= 0) {
                if (newQuantity <= 0) {
                    productIDs.remove(index);
                    quantities.remove(index);
                } else {
                    quantities.set(index, (long) newQuantity);
                }
                Map<String, Object> update = new HashMap<>();
                update.put("productID", productIDs);
                update.put("quantity", quantities);
                cartRef.set(update)
                    .addOnSuccessListener(aVoid -> { if (onSuccess != null) onSuccess.run(); })
                    .addOnFailureListener(e -> { if (onFailure != null) onFailure.run(); });
            }
        });
    }

    public LiveData<Map<String, Integer>> getCart(String userId) {
        MutableLiveData<Map<String, Integer>> cartLiveData = new MutableLiveData<>();
        db.collection("cart_items").document(userId)
            .addSnapshotListener((documentSnapshot, e) -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<String> productIDs = (List<String>) documentSnapshot.get("productID");
                    List<Long> quantities = (List<Long>) documentSnapshot.get("quantity");
                    Map<String, Integer> cart = new HashMap<>();
                    if (productIDs != null && quantities != null) {
                        for (int i = 0; i < productIDs.size(); i++) {
                            cart.put(productIDs.get(i), quantities.get(i).intValue());
                        }
                    }
                    cartLiveData.setValue(cart);
                } else {
                    cartLiveData.setValue(new HashMap<>());
                }
            });
        return cartLiveData;
    }

    public void clearCart(String userId, Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> emptyCart = new HashMap<>();
        emptyCart.put("productID", new ArrayList<String>());
        emptyCart.put("quantity", new ArrayList<Integer>());
        db.collection("cart_items").document(userId).set(emptyCart)
            .addOnSuccessListener(aVoid -> { if (onSuccess != null) onSuccess.run(); })
            .addOnFailureListener(e -> { if (onFailure != null) onFailure.run(); });
    }
} 