package com.example.tuibikho;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseAuth.getInstance().signOut();

        Button btnLogin = findViewById(R.id.btnLinkLogin);
        Button btnRegister = findViewById(R.id.btnLinkRegister);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        });

    }

//    private void checkUserStatus() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            // User is signed in, go to HomeActivity
//            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
//        } else {
//            // No user is signed in, go to AuthActivity
//            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
//        }
//        finish();
//    }
}