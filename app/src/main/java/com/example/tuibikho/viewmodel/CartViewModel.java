package com.example.tuibikho.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.tuibikho.repository.CartRepository;
import java.util.Map;

public class CartViewModel extends AndroidViewModel {
    private CartRepository repository;

    public CartViewModel(@NonNull Application application) {
        super(application);
        repository = new CartRepository();
    }

    public void addToCart(String userId, String productId, int quantity, Runnable onSuccess, Runnable onFailure) {
        repository.addToCart(userId, productId, quantity, onSuccess, onFailure);
    }

    public void removeFromCart(String userId, String productId, Runnable onSuccess, Runnable onFailure) {
        repository.removeFromCart(userId, productId, onSuccess, onFailure);
    }

    public void updateQuantity(String userId, String productId, int newQuantity, Runnable onSuccess, Runnable onFailure) {
        repository.updateQuantity(userId, productId, newQuantity, onSuccess, onFailure);
    }

    public LiveData<Map<String, Integer>> getCart(String userId) {
        return repository.getCart(userId);
    }

    public void clearCart(String userId, Runnable onSuccess, Runnable onFailure) {
        repository.clearCart(userId, onSuccess, onFailure);
    }

} 