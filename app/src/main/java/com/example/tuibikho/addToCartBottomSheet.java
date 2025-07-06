package com.example.tuibikho;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addToCartBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_PRICE = "price";
    private static final String ARG_IMAGE = "image";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_PRODUCT_ID = "productId";

    private CartViewModel cartViewModel;

    public static addToCartBottomSheet newInstance(String productId, String name, double price, String imageUrl, String amount) {
        addToCartBottomSheet fragment = new addToCartBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        args.putString(ARG_NAME, name);
        args.putDouble(ARG_PRICE, price);
        args.putString(ARG_IMAGE, imageUrl);
        args.putString(ARG_AMOUNT, amount);
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
            String amount = args.getString(ARG_AMOUNT, "");

            TextView productName = view.findViewById(R.id.productName);
            TextView productPrice = view.findViewById(R.id.productPrice);
            TextView productAmount = view.findViewById(R.id.productAmount);
            ImageView imgProduct = view.findViewById(R.id.imgProduct);

            productName.setText(name);
            productPrice.setText(String.format("%.0f VND", price));
            productAmount.setText(getString(R.string.productAmount) + amount);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).placeholder(R.drawable.img_cat_food_royal1).into(imgProduct);
            }

            Button btnAddToCart = view.findViewById(R.id.addCartAction);
            btnAddToCart.setOnClickListener(v -> {
                if (cartViewModel != null) {
                    int quantity = Integer.parseInt(amount);
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
                                quantities.set(index, quantities.get(index) + quantity);
                            } else {// Thêm sản phẩm mới
                                productIDs.add(productId);
                                quantities.add((int) quantity);
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
                            quantities.add((long) quantity);
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
}
