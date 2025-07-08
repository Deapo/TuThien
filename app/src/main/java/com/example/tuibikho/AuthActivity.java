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
            String screen = getIntent().getStringExtra("screen");
            if ("register".equals(screen)) {
                showRegisterFragment(false);
            } else {
                showLoginFragment(false);
            }
        }
    }

    public void showLoginFragment(boolean addToBackStack) {
        var transaction = getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.auth_container, new LoginFragment());
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showRegisterFragment(boolean addToBackStack) {
        var transaction = getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.auth_container, new RegisterFragment());
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
