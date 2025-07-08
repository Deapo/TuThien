package com.example.tuibikho;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseAuth.getInstance().signOut();

        Button btnLogin = findViewById(R.id.btnLinkLogin);
        Button btnRegister = findViewById(R.id.btnLinkRegister);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            intent.putExtra("screen", "login");
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            intent.putExtra("screen", "register");
            startActivity(intent);
        });

    }
}