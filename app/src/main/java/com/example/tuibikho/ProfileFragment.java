package com.example.tuibikho;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tuibikho.databinding.FragmentProfileBinding;
import com.example.tuibikho.data.PetEntity;
import com.example.tuibikho.viewmodel.PetViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String TAG = "FragmentProfile";
    private FragmentProfileBinding binding;
    private PetViewModel petViewModel;
    private YourPetAdapter petAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        petViewModel = new ViewModelProvider(this).get(PetViewModel.class);
        petAdapter = new YourPetAdapter();
        binding.recyclerInfoPet.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerInfoPet.setAdapter(petAdapter);

        // Lấy thông tin user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference userRef = db.collection("users").document(uid);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("email");
                    String imgAvatar = documentSnapshot.getString("imgAvatar");
                    binding.itemAvatarUser.userName.setText(email);
                    // TODO: load avatar bằng Glide/Picasso nếu imgAvatar != null
                }
            });
        }

        // nút sửa username
        binding.itemAvatarUser.editAvaName.setOnClickListener(v -> showEditUsernameDialog());

        // Danh sách thú cưng
        petViewModel.getAllPets(userId).observe(getViewLifecycleOwner(), petsMap -> {
            ArrayList<PetEntity> pets = new ArrayList<>();
            if (petsMap != null) {
                for (Map.Entry<String, Map<String, Object>> entry : petsMap.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Map) {
                        Map<String, Object> pet = (Map<String, Object>) value;
                        PetEntity petEntity = new PetEntity();
                        petEntity.setPetId(entry.getKey().hashCode());
                        petEntity.setPetName((String) pet.get("name"));
                        petEntity.setPetAge(String.valueOf(pet.get("age")));
                        petEntity.setPetGender((String) pet.get("gender"));
                        petEntity.setPetImage((String) pet.get("imgAvatar"));
                        pets.add(petEntity);
                    }
                }
            }
            petAdapter.submitList(pets);
        });

        //nút thêm thú cưng
        binding.addPet.setOnClickListener(v -> {
            //tối đa 5 pet
            petViewModel.getAllPets(userId).observe(getViewLifecycleOwner(), petsMap -> {
                if (petsMap == null || petsMap.size() < 5) {
                    showAddPetDialog();
                } else {
                    Toast.makeText(getContext(), "Bạn chỉ có thể thêm tối đa 5 thú cưng", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Xử lý nút back
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        ImageButton btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Đăng xuất Firebase
            FirebaseAuth.getInstance().signOut();

            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(requireContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void showAddPetDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_pet, null);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        android.widget.EditText edtPetName = dialogView.findViewById(R.id.edtPetName);
        android.widget.EditText edtPetAge = dialogView.findViewById(R.id.edtPetAge);
        android.widget.RadioGroup radioGroupGender = dialogView.findViewById(R.id.radioGroupGender);
        android.widget.EditText edtPetAvatar = dialogView.findViewById(R.id.edtPetAvatar);
        android.widget.Button btnAddPet = dialogView.findViewById(R.id.btnAddPet);
        android.widget.Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnAddPet.setOnClickListener(v -> {
            String name = edtPetName.getText().toString().trim();
            String ageStr = edtPetAge.getText().toString().trim();
            String imgAvatar = edtPetAvatar.getText().toString().trim();
            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            String gender = "";
            if (selectedGenderId == R.id.radioMale) gender = "Đực";
            else if (selectedGenderId == R.id.radioFemale) gender = "Cái";
            if (name.isEmpty() || ageStr.isEmpty() || gender.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            int age = Integer.parseInt(ageStr);
            String petKey = java.util.UUID.randomUUID().toString();
            petViewModel.addOrUpdatePet(userId, petKey, name, age, gender, imgAvatar, () -> {
                Toast.makeText(getContext(), "Thêm thú cưng thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }, () -> Toast.makeText(getContext(), "Lỗi khi thêm thú cưng", Toast.LENGTH_SHORT).show());
        });
        dialog.show();
    }

    private void showEditUsernameDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_username, null);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        android.widget.EditText edtNewUsername = dialogView.findViewById(R.id.edtNewUsername);
        android.widget.Button btnConfirmEdit = dialogView.findViewById(R.id.btnConfirmEdit);
        android.widget.Button btnCancelEdit = dialogView.findViewById(R.id.btnCancelEdit);

        btnCancelEdit.setOnClickListener(v -> dialog.dismiss());
        btnConfirmEdit.setOnClickListener(v -> {
            String newUsername = edtNewUsername.getText().toString().trim();
            if (newUsername.isEmpty()) {
                Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            // Cập nhật Firestore
            db.collection("users").document(userId)
                .update("email", newUsername)
                .addOnSuccessListener(aVoid -> {
                    binding.itemAvatarUser.userName.setText(newUsername);
                    Toast.makeText(getContext(), "Đổi tên thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi đổi tên", Toast.LENGTH_SHORT).show());
        });
        dialog.show();
    }
}
