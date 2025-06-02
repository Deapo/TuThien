package com.example.tuibikho;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_screen, container, false);

        // Find views
        ImageView splashImage = view.findViewById(R.id.splashImage);
        TextView textView = view.findViewById(R.id.titleApp);
        MaterialButton btnLogin = view.findViewById(R.id.textlinkLogin);
        MaterialButton btnRegister = view.findViewById(R.id.textlinkRegister);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

        // Apply animations
        splashImage.startAnimation(fadeIn);
        textView.startAnimation(slideUp);

        // Navigate to Login
        btnLogin.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
        });

        //Navigate to Register
        btnRegister.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
        });

        return view;
    }
}