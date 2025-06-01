package com.example.tuibikho;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Find views
        ImageView splashImage = findViewById(R.id.splashImage);
        TextView textView = findViewById(R.id.titleApp);
        Button btnLogin = findViewById(R.id.linkLogin);
        Button btnRegister = findViewById(R.id.linkRegister);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Apply animations
        splashImage.startAnimation(fadeIn);
        textView.startAnimation(slideUp);

        // Set button text
        btnLogin.setText("Đăng nhập");
        btnRegister.setText("Đăng ký");

        // Set click listeners
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}