package com.example.tuibikho;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);

        TextView btnToNavigate = findViewById(R.id.btnToNavigateLogin);
        btnToNavigate.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //Set up button back
        ImageButton back = findViewById(R.id.arrowBack);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
            startActivity(intent);
        });
    }
}
