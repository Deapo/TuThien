package com.example.tuibikho.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products")
    LiveData<List<ProductEntity>> getAllProducts();

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertProduct(ProductEntity product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProducts(List<ProductEntity> products);

    @Delete
    void deleteProduct(ProductEntity product);
}
