package com.example.tuibikho.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tuibikho.data.NutritionInfo;
import com.example.tuibikho.data.ProductEntity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    private final MutableLiveData<List<ProductEntity>> products = new MutableLiveData<>();
    private final FirebaseFirestore db;

    public ProductRepository(Application application) {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<ProductEntity>> getAllProducts() {
        return products;
    }

    public void ReadData() {
        Log.d(TAG, "Starting to read data from Firestore...");
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Firestore query successful. Documents count: " + task.getResult().size());
                List<ProductEntity> productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, "Processing document: " + document.getId());
                    Log.d(TAG, "Document data: " + document.getData());
                    
                    ProductEntity product = document.toObject(ProductEntity.class);
                    
                    // Đảm bảo product ID được set từ document ID
                    if (product != null) {
                        product.setId(document.getId());
                        Log.d(TAG, "Product created: ID=" + product.getId() + ", Name=" + product.getName() + ", Price=" + product.getPrice());
                        
                        // Debug từng field
                        Log.d(TAG, "Product fields - Name: " + product.getName());
                        Log.d(TAG, "Product fields - Type: " + product.getType());
                        Log.d(TAG, "Product fields - Category: " + product.getCategory());
                        Log.d(TAG, "Product fields - Description: " + product.getDescription());
                        Log.d(TAG, "Product fields - Price: " + product.getPrice());
                        Log.d(TAG, "Product fields - ImageURL: " + product.getImageURL());
                        Log.d(TAG, "Product fields - Benefits: " + product.getBenefits());
                        Log.d(TAG, "Product fields - Ingredients: " + product.getIngredients());
                    } else {
                        Log.e(TAG, "Failed to convert document to ProductEntity: " + document.getId());
                        continue;
                    }

                    formatPrice(product.getPrice());
                    //Convert kieu du lieu
                    List<Map<String, Object>> nutritionList = (List<Map<String, Object>>) document.get("nutritions");
                    if (nutritionList != null) {
                        List<NutritionInfo> nutritionInfoList = new ArrayList<>();
                        for (Map<String, Object> map : nutritionList) {
                            NutritionInfo info = new NutritionInfo();
                            info.setName((String) map.get("name"));
                            info.setValue((String) map.get("value"));
                            nutritionInfoList.add(info);
                        }
                        product.setNutritions(nutritionInfoList);
                        Log.d(TAG, "Added " + nutritionInfoList.size() + " nutrition items");
                    }

                    productList.add(product);
                }
                Log.d(TAG, "Total products loaded: " + productList.size());
                products.setValue(productList);
            } else {
                Log.e(TAG, "Firestore query failed: " + task.getException());
                products.setValue(new ArrayList<>());
            }
        });
    }

    public String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(price);
    }
}
