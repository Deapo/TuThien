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
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCartItem(CartEntity cartItem);

    @Update
    void updateCartItem(CartEntity cartItem);

    @Delete
    void deleteCartItem(CartEntity cartItem);

    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<CartEntity>> getAllCartItems(String userId);

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    CartEntity getCartItemByUserAndProduct(String userId, String productId);

    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    LiveData<Integer> getCartItemCount(String userId);

    @Query("SELECT SUM(productPrice * quantity) FROM cart_items WHERE userId = :userId")
    LiveData<Double> getTotalPrice(String userId);

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    void clearCart(String userId);

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    void removeCartItemByUserAndProduct(String userId, String productId);
} 