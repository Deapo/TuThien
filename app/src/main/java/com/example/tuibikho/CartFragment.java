package com.example.tuibikho;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.tuibikho.data.CartEntity;
import com.example.tuibikho.databinding.FragmentCartBinding;
import com.example.tuibikho.viewmodel.CartViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import androidx.browser.customtabs.CustomTabsIntent;
import android.net.Uri;
import android.content.Intent;
import android.app.AlertDialog;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private CartViewModel viewModel;
    private CartAdapter cartAdapter;
    private String userId;
    private FirebaseFirestore db;
    private double totalAmount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        setupRecyclerView();
        setupUI();
        observeViewModel();
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter();
        binding.recyclerCartItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCartItems.setAdapter(cartAdapter);
        
        cartAdapter.setOnCartItemClickListener(new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onQuantityChanged(CartAdapter.CartDisplayItem cartItem, int newQuantity) {
                viewModel.updateQuantity(userId, cartItem.productId, newQuantity, null, null);
            }

            @Override
            public void onRemoveItem(CartAdapter.CartDisplayItem cartItem) {
                viewModel.removeFromCart(userId, cartItem.productId, null, null);
                Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Clear cart button
        binding.btnClearCart.setOnClickListener(v -> {
            viewModel.clearCart(userId, null, null);
            Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show();
        });

        // Checkout button (chỉ hiển thị tổng tiền, không gọi API)
        binding.btnCheckout.setOnClickListener(v -> {
            double total = getCurrentTotal();
            showDialog("Tổng tiền: " + formatPrice(total));
        });
    }

    private void observeViewModel() {
        viewModel.getCart(userId).observe(getViewLifecycleOwner(), cartMap -> {
            if (cartMap == null || cartMap.isEmpty()) {
                cartAdapter.submitList(new ArrayList<>());
                updateEmptyState(new ArrayList<>());
                binding.totalPrice.setText("0 VND");
                binding.btnCheckout.setEnabled(false);
                totalAmount = 0;
                return;
            }
            ArrayList<CartAdapter.CartDisplayItem> displayItems = new ArrayList<>();
            final double[] total = {0};
            final int[] loaded = {0};
            for (Map.Entry<String, Integer> entry : cartMap.entrySet()) {
                String productId = entry.getKey();
                int quantity = entry.getValue();
                db.collection("products").document(productId).get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String imageURL = doc.getString("imageURL");
                        Double price = doc.getDouble("price");
                        CartAdapter.CartDisplayItem item = new CartAdapter.CartDisplayItem(
                                productId,
                                name != null ? name : "",
                                imageURL != null ? imageURL : "",
                                price != null ? price : 0,
                                quantity
                        );
                        displayItems.add(item);
                        total[0] += (price != null ? price : 0) * quantity;
                    }
                    loaded[0]++;
                    if (loaded[0] == cartMap.size()) {
                        cartAdapter.submitList(displayItems);
                        updateEmptyState(displayItems);
                        binding.totalPrice.setText(formatPrice(total[0]));
                        binding.btnCheckout.setEnabled(!displayItems.isEmpty());
                        totalAmount = total[0];
                    }
                }).addOnFailureListener(e -> {
                    loaded[0]++;
                    if (loaded[0] == cartMap.size()) {
                        cartAdapter.submitList(displayItems);
                        updateEmptyState(displayItems);
                        binding.totalPrice.setText(formatPrice(total[0]));
                        binding.btnCheckout.setEnabled(!displayItems.isEmpty());
                        totalAmount = total[0];
                    }
                });
            }
        });
    }

    private void updateEmptyState(java.util.List<CartAdapter.CartDisplayItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            binding.recyclerCartItems.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.GONE);
        } else {
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.recyclerCartItems.setVisibility(View.VISIBLE);
            binding.bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(price);
    }

    private double getCurrentTotal() {
        double total = 0;
        List<CartAdapter.CartDisplayItem> items = cartAdapter.getCurrentList();
        for (CartAdapter.CartDisplayItem item : items) {
            total += item.productPrice * item.quantity;
        }
        return total;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(getContext())
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
} 