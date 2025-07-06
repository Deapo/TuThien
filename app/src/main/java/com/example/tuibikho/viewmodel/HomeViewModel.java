package com.example.tuibikho.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tuibikho.data.ProductEntity;
import com.example.tuibikho.repository.ProductRepository;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;
    private final LiveData<List<ProductEntity>> products;
    
    public HomeViewModel(@NonNull Application application){
        super(application);
        productRepository = new ProductRepository(application);
        products = productRepository.getAllProducts();
    }

    public LiveData<List<ProductEntity>> getProducts() {
        return products;
    }

    public void FetchDataFromFirestore() {
        productRepository.ReadData();
    }
}
