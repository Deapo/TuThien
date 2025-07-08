package com.example.tuibikho;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.tuibikho.data.UserEntity;
import com.example.tuibikho.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    private TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private ImageButton back;
    private TextView linkLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ExecutorService executorService;
    private UserRepository userRepository;

    private final String sendEmail = "nguyenphiphung2004@gmail.com";
    private final String sendPassword = "mqlv epmg ksot dzjn"; // Thay thế bằng App Password 16 ký tự

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_screen, container, false);

        // Initialize executor service for background tasks
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        back = view.findViewById(R.id.btnBack);
        view.findViewById(R.id.registerButton).setOnClickListener(v -> registerUser());
        linkLogin = view.findViewById(R.id.linkLogin);

        linkLogin.setOnClickListener(v -> {
            ((AuthActivity) requireActivity()).showLoginFragment(true);
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UserRepository
        userRepository = new UserRepository();

        // Set up back button
        back.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Set up register button

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        // Lưu user Firestore
                        userRepository.saveUser(userId, email, "", email, () -> {
                            // Tạo document giỏ hàng rỗng
                            Map<String, Object> cart = new HashMap<>();
                            cart.put("productID", new ArrayList<String>());
                            cart.put("quantity", new ArrayList<Integer>());
                            db.collection("cart_items").document(userId).set(cart);
                            // Tạo document pet rỗng (map)
                            db.collection("your_pet").document(userId).set(new HashMap<>());
                            // Gửi email xác nhận như cũ
                            executorService.execute(() -> {
                                try {
                                    MailSend mailSender = new MailSend(sendEmail, sendPassword);
                                    mailSender.sendMail(
                                        email,
                                        "Đăng ký thành công",
                                        "Chào mừng bạn đến với ứng dụng của chúng tôi!\n\n" +
                                        "Tài khoản của bạn đã được tạo thành công.\n" +
                                        "Email: " + email + "\n\n" +
                                        "Cảm ơn bạn đã đăng ký!"
                                    );
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(),
                                            "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                        ((AuthActivity) requireActivity()).navigateToHome();
                                    });
                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(),
                                            "Đăng ký thành công nhưng không thể gửi email xác nhận",
                                            Toast.LENGTH_SHORT).show();
                                        ((AuthActivity) requireActivity()).showRegisterFragment(true);
                                    });
                                }
                            });
                        }, () -> {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Lưu user thất bại", Toast.LENGTH_SHORT).show();
                            });
                        });
                    }
                });
    }
}
