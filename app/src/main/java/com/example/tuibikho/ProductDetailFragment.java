package com.example.tuibikho;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.tuibikho.data.ProductEntity;
import com.example.tuibikho.databinding.FragmentProductDetailBinding;
import com.example.tuibikho.viewmodel.HomeViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class ProductDetailFragment extends Fragment {
    private static final String TAG = "ProductDetailFragment";
    private FragmentProductDetailBinding binding;
    private HomeViewModel viewModel;
    private ProductEntity currentProduct;
    private int quantity = 1;
    private String requestedProductId;

    public static final String ARG_PRODUCT_ID = "id";
    public static final String ARG_PRODUCT_NAME = "name";
    public static final String ARG_PRODUCT_PRICE = "price";
    public static final String ARG_PRODUCT_IMAGE = "image";

    // Overloaded method để truyền thêm thông tin cơ bản
    public static ProductDetailFragment newInstance(ProductEntity product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, product.getId());
        args.putString(ARG_PRODUCT_NAME, product.getName());
        args.putDouble(ARG_PRODUCT_PRICE, product.getPrice());
        args.putString(ARG_PRODUCT_IMAGE, product.getImageURL());
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Lấy thông tin từ arguments
        if (getArguments() != null) {
            requestedProductId = getArguments().getString(ARG_PRODUCT_ID);
            Log.d(TAG, "Requested Product ID: " + requestedProductId);
            
            // Hiển thị thông tin
            displayBasicInfo();
        }
        
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        
        // Setup UI
        setupUI();
        
        // Observe data
        observeViewModel();
        
        // Load data nếu cần
        if (viewModel.getProducts().getValue() == null) {
            Log.d(TAG, "No data available, fetching from Firestore...");
            viewModel.FetchDataFromFirestore();
        } else {
            Log.d(TAG, "Data already available, processing...");
            processAvailableData();
        }
    }

    private void displayBasicInfo() {
        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString(ARG_PRODUCT_NAME);
            Double price = args.getDouble(ARG_PRODUCT_PRICE, 0.0);
            String imageUrl = args.getString(ARG_PRODUCT_IMAGE);
            
            if (name != null) {
                binding.productNameDetail.setText(name);
            }

            if (price > 0) {
                binding.productPrice.setText(formatPrice(price));
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .error(R.drawable.img_cat_food_royal1)
                        .into(binding.imgProductDetail);
            }
        }
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Quantity controls
        binding.btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityDisplay();
            }
        });

        binding.btnPlus.setOnClickListener(v -> {
            quantity++;
            updateQuantityDisplay();
        });

        // Add to cart button
        binding.btnAddToCart.setOnClickListener(v -> {
            addToCart();
            updateTotalPrice();
        });
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            Log.d(TAG, "Data received from ViewModel. Products count: " + (products != null ? products.size() : 0));
            if (products != null && !products.isEmpty()) {
                processAvailableData();
            } else {
                Log.w(TAG, "No products available, showing test data");
                // Hiển thị dữ liệu test nếu không có dữ liệu từ Firebase
                displayTestData();
            }
        });
    }

    private void processAvailableData() {
        List<ProductEntity> products = viewModel.getProducts().getValue();
        if (products == null || products.isEmpty()) {
            Log.w(TAG, "No products to process");
            return;
        }

        Log.d(TAG, "Processing " + products.size() + " products");
        
        // Debug: In ra tất cả product IDs
        for (ProductEntity product : products) {
            Log.d(TAG, "Available product: ID=" + product.getId() + ", Name=" + product.getName());
        }

        if (requestedProductId != null) {
            currentProduct = findProductById(products, requestedProductId);
            if (currentProduct != null) {
                Log.d(TAG, "Found product: " + currentProduct.getName());
                displayProductDetails(currentProduct);
            } else {
                Log.e(TAG, "Product not found with ID: " + requestedProductId);
                // Giữ lại thông tin cơ bản đã hiển thị
                Toast.makeText(requireContext(), "Some details may not be available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "No product ID requested");
        }
    }

    private ProductEntity findProductById(List<ProductEntity> products, String productId) {
        Log.d(TAG, "Searching for product with ID: " + productId);
        for (ProductEntity product : products) {
            Log.d(TAG, "Comparing with: " + product.getId() + " (equals: " + productId.equals(product.getId()) + ")");
            if (productId.equals(product.getId())) {
                return product;
            }
        }
        return null;
    }

    private void displayProductDetails(ProductEntity product) {
        Log.d(TAG, "Displaying product details for: " + product.getName());
        
        try {
            // Hiển thị tên sản phẩm
            if (product.getName() != null && binding.productNameDetail.getText().toString().isEmpty()) {
                binding.productNameDetail.setText(product.getName());
            }
            
            // Hiển thị giá
            if (product.getPrice() > 0) {
                binding.productPrice.setText(formatPrice(product.getPrice()));
            }
            
            // Hiển thị hình ảnh
            if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
                Log.d(TAG, "Loading image: " + product.getImageURL());
                Glide.with(this)
                        .load(product.getImageURL())
                        .placeholder(R.drawable.img_cat_food_royal1)
                        .error(R.drawable.img_cat_food_royal1)
                        .into(binding.imgProductDetail);
            }
            
            // Hiển thị mô tả
            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                binding.txtProductDetail.setText(product.getDescription());
            } else {
                binding.txtProductDetail.setText("No description available");
            }
            
            // Hiển thị benefits
            if (product.getBenefits() != null && !product.getBenefits().isEmpty()) {
                StringBuilder benefitsText = new StringBuilder();
                for (String benefit : product.getBenefits()) {
                    benefitsText.append("•\t").append(benefit).append("\n");
                }
                binding.txtProductBenefit.setText(benefitsText.toString());
            } else {
                binding.txtProductBenefit.setText("No benefits information available");
            }
            
            // Hiển thị thông tin dinh dưỡng
            if (product.getNutritions() != null && !product.getNutritions().isEmpty()) {
                StringBuilder nutritionText = new StringBuilder();
                for (int i = 0; i < product.getNutritions().size(); i++) {
                    var nutrition = product.getNutritions().get(i);
                    nutritionText.append(nutrition.getName())
                               .append(": ")
                               .append(nutrition.getValue());
                    if (i < product.getNutritions().size() - 1) {
                        nutritionText.append("\n");
                    }
                }
                binding.txtProductNutrition.setText(nutritionText.toString());
            } else {
                binding.txtProductNutrition.setText("No nutritional information available");
            }
            
            // Hiển thị stock
            if (product.getStock() != null && !product.getStock().isEmpty()) {
                binding.productWeightDetail.setText("Stock: " + product.getStock());
            } else {
                binding.productWeightDetail.setText("Stock: Available");
            }
            
            Log.d(TAG, "Product details displayed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error displaying product details", e);
            Toast.makeText(requireContext(), "Error displaying product details", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuantityDisplay() {
        binding.txtQuantity.setText(String.valueOf(quantity));
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = currentProduct.getPrice() * quantity;
        binding.productPrice.setText(formatPrice(total));
    }


    private void addToCart() {
        if (currentProduct != null) {
            String productId = currentProduct.getId();
            String name = currentProduct.getName();
            double price = currentProduct.getPrice();
            String imageUrl = currentProduct.getImageURL();
            addToCartBottomSheet bottomSheet = addToCartBottomSheet.newInstance(productId, name, price, imageUrl, quantity);
            bottomSheet.show(getChildFragmentManager(), "AddToCartBottomSheet");
        } else {
            String productName = binding.productNameDetail.getText().toString();
            String message = String.format("Added %d x %s to cart", quantity, productName);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayTestData() {
        Log.d(TAG, "Displaying test data");
        
        // Hiển thị thông tin test nếu chưa có thông tin cơ bản
        if (binding.productNameDetail.getText().toString().isEmpty()) {
            binding.productNameDetail.setText("Royal Canin Cat Food");
        }
        
        if (binding.productPrice.getText().toString().equals("1.99")) {
            binding.productPrice.setText(formatPrice(25.99));
        }
        
        binding.txtProductDetail.setText("Premium cat food with high-quality ingredients. Perfect for adult cats aged 1-7 years.");
        binding.txtProductBenefit.setText("• Promotes healthy digestion\n• Supports immune system\n• Maintains healthy weight\n• Improves coat condition\n• Reduces hairballs");
        binding.txtProductNutrition.setText("Protein: 32%\nFat: 18%\nFiber: 3%\nMoisture: 8%\nCalcium: 1.2%\nPhosphorus: 0.9%");
        binding.productWeightDetail.setText("Stock: 50 units available");
        
        Toast.makeText(requireContext(), "Using test data - server data unavailable", Toast.LENGTH_LONG).show();
    }

    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(price);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
