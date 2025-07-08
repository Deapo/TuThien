package com.example.tuibikho;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.tuibikho.data.AppDatabase;
import com.firebase.ui.auth.BuildConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            // Lấy một instance của database và gọi getOpenHelper() để buộc nó mở kết nối.
            // Điều này sẽ giữ cho database "mở" để App Inspector có thể truy cập.
            Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .getOpenHelper() // Lệnh quan trọng để giữ kết nối
                    .getWritableDatabase(); // Hoặc .getReadableDatabase();
        }
        bottomNavigationView = findViewById(R.id.bottomNavigate);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                // Handle home navigation
                return true;
            } else if (itemId == R.id.explore) {
                // Handle explore navigation
                return true;
            } else if (itemId == R.id.cart) {
                // Handle cart navigation
                return true;
            }
            else if (itemId == R.id.profile) {
                // Handle profile navigation
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if (data != null && "yourapp".equals(data.getScheme()) && "vnpay_return".equals(data.getHost())) {
            String rspCode = data.getQueryParameter("vnp_ResponseCode");
            String message;
            if ("00".equals(rspCode)) {
                message = "Thanh toán thành công!";
            } else {
                message = "Thanh toán thất bại hoặc bị huỷ!";
            }
            showDialog(message);
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
}