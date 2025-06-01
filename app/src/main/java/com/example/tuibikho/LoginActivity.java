package com.example.tuibikho;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        //Set up button back
        ImageButton back = findViewById(R.id.arrowBack);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent);
        });

        //Navigate to Register
        LinearLayout btnToNavigate = findViewById(R.id.linkRegister);
        btnToNavigate.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

}
