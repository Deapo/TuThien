package com.example.tuibikho;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.Locale;

public class CartAdapter extends ListAdapter<CartAdapter.CartDisplayItem, CartAdapter.CartViewHolder> {
    public static class CartDisplayItem {
        public String productId;
        public String productName;
        public String productImage;
        public double productPrice;
        public int quantity;
        public CartDisplayItem(String productId, String productName, String productImage, double productPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.productImage = productImage;
            this.productPrice = productPrice;
            this.quantity = quantity;
        }
    }

    private OnCartItemClickListener listener;

    public interface OnCartItemClickListener {
        void onQuantityChanged(CartDisplayItem cartItem, int newQuantity);
        void onRemoveItem(CartDisplayItem cartItem);
    }

    public void setOnCartItemClickListener(OnCartItemClickListener listener) {
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<CartDisplayItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CartDisplayItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartDisplayItem oldItem, @NonNull CartDisplayItem newItem) {
            return oldItem.productId.equals(newItem.productId);
        }
        @Override
        public boolean areContentsTheSame(@NonNull CartDisplayItem oldItem, @NonNull CartDisplayItem newItem) {
            return oldItem.quantity == newItem.quantity && oldItem.productPrice == newItem.productPrice;
        }
    };

    public CartAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartDisplayItem cartItem = getItem(position);
        holder.bind(cartItem);
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView productName;
        private TextView productPrice;
        private TextView txtQuantity;
        private ImageButton btnMinus;
        private ImageButton btnPlus;
        private ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(CartDisplayItem cartItem) {
            productName.setText(cartItem.productName);
            productPrice.setText(formatPrice(cartItem.productPrice));
            txtQuantity.setText(String.valueOf(cartItem.quantity));
            
            // Debug logging
            android.util.Log.d("CartAdapter", "Binding item - Name: " + cartItem.productName);
            android.util.Log.d("CartAdapter", "Binding item - Image: " + cartItem.productImage);
            
            if (cartItem.productImage != null && !cartItem.productImage.isEmpty()) {
                android.util.Log.d("CartAdapter", "Loading image with Glide: " + cartItem.productImage);
                Glide.with(itemView.getContext())
                        .load(cartItem.productImage)
                        .placeholder(R.drawable.img_cat_food_royal1)
                        .error(R.drawable.img_cat_food_royal1)
                        .into(imgProduct);
            } else {
                android.util.Log.w("CartAdapter", "No image URL provided for product: " + cartItem.productName);
                imgProduct.setImageResource(R.drawable.img_cat_food_royal1);
            }
            btnMinus.setOnClickListener(v -> {
                int newQuantity = cartItem.quantity - 1;
                if (newQuantity >= 0 && listener != null) {
                    listener.onQuantityChanged(cartItem, newQuantity);
                }
            });
            btnPlus.setOnClickListener(v -> {
                int newQuantity = cartItem.quantity + 1;
                if (listener != null) {
                    listener.onQuantityChanged(cartItem, newQuantity);
                }
            });
            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(cartItem);
                }
            });
        }
        private String formatPrice(double price) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return formatter.format(price);
        }
    }
}
