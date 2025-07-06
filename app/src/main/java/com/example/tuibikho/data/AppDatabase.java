package com.example.tuibikho.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.tuibikho.data.ProductEntity;
import com.example.tuibikho.data.CartEntity;
import com.example.tuibikho.data.NutritionInfo;
import com.example.tuibikho.data.TypeConvert;
import com.example.tuibikho.data.PetEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ProductEntity.class, CartEntity.class, PetEntity.class}, version = 3, exportSchema = false)
@TypeConverters(TypeConvert.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public abstract ProductDao productDao();
    public abstract CartDao cartDao();
    public abstract PetDao petDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "app_database"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
