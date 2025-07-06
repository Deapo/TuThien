package com.example.tuibikho;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.auth_container, new LoginFragment())
                .commit();
        }
    }

    public void showRegisterFragment() {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.auth_container, new RegisterFragment())
            .addToBackStack(null)
            .commit();
    }

    public void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
