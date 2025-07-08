package com.example.tuibikho;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.tuibikho.viewmodel.CartViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class addToCartBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_PRICE = "price";
    private static final String ARG_IMAGE = "image";
    private static final String ARG_PRODUCT_ID = "productId";
    private static final String ARG_QUANTITY = "quantity";

    private CartViewModel cartViewModel;
    private int currentQuantity = 1;

    public static addToCartBottomSheet newInstance(String productId, String name, double price, String imageUrl, int quantity) {
        addToCartBottomSheet fragment = new addToCartBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        args.putString(ARG_NAME, name);
        args.putDouble(ARG_PRICE, price);
        args.putString(ARG_IMAGE, imageUrl);
        args.putInt(ARG_QUANTITY, quantity);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_bottom_sheet_add_to_cart, container, false);

        FragmentActivity activity = getActivity();
        if (activity != null) {
            cartViewModel = new ViewModelProvider(activity).get(CartViewModel.class);
        }

        Bundle args = getArguments();
        if (args != null) {
            String productId = args.getString(ARG_PRODUCT_ID, "");
            String name = args.getString(ARG_NAME, "");
            double price = args.getDouble(ARG_PRICE, 0.0);
            String imageUrl = args.getString(ARG_IMAGE, "");
            currentQuantity = args.getInt(ARG_QUANTITY, 1);

            TextView productName = view.findViewById(R.id.productName);
            TextView productPrice = view.findViewById(R.id.productPrice);
            ImageView imgProduct = view.findViewById(R.id.imgProduct);
            TextView tvQuantity = view.findViewById(R.id.tvQuantity);
            ImageButton btnDecrease = view.findViewById(R.id.btnDecrease);
            ImageButton btnIncrease = view.findViewById(R.id.btnIncrease);

            productName.setText(name);
            tvQuantity.setText(String.valueOf(currentQuantity));
            double totalPrice = price * currentQuantity;
            productPrice.setText(formatPrice(totalPrice));
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).placeholder(R.drawable.img_cat_food_royal1).into(imgProduct);
            }

            // Setup quantity controls
            btnDecrease.setOnClickListener(v -> {
                if (currentQuantity > 1) {
                    currentQuantity--;
                    tvQuantity.setText(String.valueOf(currentQuantity));
                }
            });

            btnIncrease.setOnClickListener(v -> {
                currentQuantity++;
                tvQuantity.setText(String.valueOf(currentQuantity));
            });

            Button btnAddToCart = view.findViewById(R.id.addCartAction);
            btnAddToCart.setOnClickListener(v -> {
                if (cartViewModel != null) {
                    int quantityToAdd = currentQuantity;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference cartRef = db.collection("cart_items").document(userId);

                    cartRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<String> productIDs = (List<String>) documentSnapshot.get("productID");
                            List<Integer> quantities = (List<Integer>) documentSnapshot.get("quantity");
                            if (productIDs == null) productIDs = new ArrayList<>();
                            if (quantities == null) quantities = new ArrayList<>();

                            int index = productIDs.indexOf(productId);
                            if (index >= 0) {
                                // Đã có sản phẩm, tăng số lượng
                                quantities.set(index, quantities.get(index) + quantityToAdd);

                            } else {// Thêm sản phẩm mới
                                productIDs.add(productId);
                                quantities.add(quantityToAdd);
                            }
                            Map<String, Object> update = new HashMap<>();
                            update.put("productID", productIDs);
                            update.put("quantity", quantities);
                            cartRef.update(update);
                        } else {
                            // Tạo mới
                            List<String> productIDs = new ArrayList<>();
                            List<Long> quantities = new ArrayList<>();
                            productIDs.add(productId);
                            quantities.add((long) quantityToAdd);
                            Map<String, Object> cart = new HashMap<>();
                            cart.put("productID", productIDs);
                            cart.put("quantity", quantities);
                            cartRef.set(cart);
                        }
                    });
                    Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        }

        return view;
    }

    public String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(price);
    }
}
