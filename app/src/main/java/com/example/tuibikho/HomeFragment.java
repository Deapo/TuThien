package com.example.tuibikho;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.tuibikho.viewmodel.PetViewModel;

import com.example.tuibikho.data.ProductEntity;
import com.example.tuibikho.viewmodel.HomeViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.example.tuibikho.databinding.FragmentHomeScreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.example.tuibikho.repository.PetRepository;

public class HomeFragment extends Fragment {
    private FragmentHomeScreenBinding binding;
    private ProductAdapter productAdapter;
    private PetTypeAdapter petTypeAdapter;
    private HomeViewModel viewModel;
    // Thêm biến lưu danh sách thông báo
    private List<String> notificationMessages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewModel.FetchDataFromFirestore();

        observeViewModel();
        setupRecyclerViews();
        setupPetTypeClick();
        setupSearchBar();
        checkPetNotifications();

        binding.btnSeeAll.setOnClickListener(v -> navigateToAllProducts(null, null));
        binding.btnSeeAllExplore.setOnClickListener(v -> navigateToAllProducts(null, null));

    }

    private void setupSearchBar() {
        // Setup search bar click listener - sử dụng View.findViewById thay vì binding
        View searchHintView = getView().findViewById(R.id.tvSearchHint);
        if (searchHintView != null) {
            searchHintView.setOnClickListener(v -> {
                showSearchDialog();
            });
        }
    }

    private void showSearchDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Tìm kiếm sản phẩm");

        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Nhập tên sản phẩm...");
        builder.setView(input);

        builder.setPositiveButton("Tìm kiếm", (dialog, which) -> {
            String query = input.getText().toString().trim();
            if (!query.isEmpty()) {
                navigateToAllProducts(null, query);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.setNeutralButton("Xóa tìm kiếm", (dialog, which) -> {});

        builder.show();
    }

    private void setupRecyclerViews() {
        productAdapter = new ProductAdapter();
        binding.recyclerExplore.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerExplore.setAdapter(productAdapter);

        int spacingPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        binding.recyclerExplore.addItemDecoration(new HorizontalSpaceItemDecoration(spacingPixels));

        productAdapter.setOnItemClickListener(product -> {
            ProductDetailFragment detailFragment = ProductDetailFragment.newInstance(product);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // PetType Adapter giữ nguyên
        binding.recyclerPetType.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        List<PetType> petTypeList = new ArrayList<>();
        petTypeList.add(new PetType(R.drawable.img_dog, "Dog"));
        petTypeList.add(new PetType(R.drawable.img_cat, "Cat"));
        petTypeAdapter = new PetTypeAdapter(petTypeList);
        binding.recyclerPetType.setAdapter(petTypeAdapter);
    }

    private void setupPetTypeClick() {
        petTypeAdapter.setOnItemClickListener(petType -> navigateToAllProducts(petType.getName(), null));
    }

    private void navigateToAllProducts(String filter, String searchQuery) {
        AllProductsFragment allProductsFragment = new AllProductsFragment();
        Bundle bundle = new Bundle();
        if (filter != null) {
            bundle.putString("PET_TYPE_FILTER", filter);
        }
        if (searchQuery != null) {
            bundle.putString("SEARCH_QUERY", searchQuery);
        }
        if (filter != null || searchQuery != null) {
            allProductsFragment.setArguments(bundle);
        }
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_container, allProductsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToProfile(ProductEntity product) {
        ProfileFragment profileFragment = new ProfileFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_container, profileFragment)
                .addToBackStack(null);

    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if(products != null && !products.isEmpty()) {
                List<ProductEntity> randomProducts = new ArrayList<>(products);
                Collections.shuffle(randomProducts); // tron ngau nhien danh sach san pham
                if (randomProducts.size() > 5) {
                    randomProducts = randomProducts.subList(0, 5); //lay 5 san pham dau tien
                }
                productAdapter.submitList(randomProducts); //gan danh sach san pham vao adapter (<-> diffutil)
                binding.recyclerExplore.setVisibility(View.VISIBLE);
                binding.emptyTrending.setVisibility(View.GONE); // an empty state
            } else {
                productAdapter.submitList(null);
                binding.recyclerExplore.setVisibility(View.GONE);
                // Hiện empty state
                binding.emptyTrending.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkPetNotifications() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        PetRepository petRepository = new PetRepository();
        petRepository.getAllPets(userId).observe(getViewLifecycleOwner(), petsMap -> {
            boolean hasBirthday = false;
            boolean hasVaccination = false;
            notificationMessages.clear();
            LocalDate today = LocalDate.now();
            if (petsMap != null) {
                for (Map.Entry<String, Map<String, Object>> entry : petsMap.entrySet()) {
                    Map<String, Object> pet = entry.getValue();
                    String name = (String) pet.get("name");
                    //kiểm tra sinh nhật
                    String birthday = (String) pet.get("birthday");
                    if (birthday != null && !birthday.isEmpty()) {
                        try {
                            LocalDate birthDate = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            if (birthDate.getMonth() == today.getMonth() && birthDate.getDayOfMonth() == today.getDayOfMonth()) {
                                hasBirthday = true;
                                notificationMessages.add("Hôm nay là sinh nhật của " + name + "! 🎂");
                            }
                        } catch (Exception ignored) {}
                    }
                    //kiểm tra vaccine
                    Object vaccinationObj = pet.get("vaccinationSchedule");
                    String vaccinationSchedule = "";
                    if (vaccinationObj instanceof String) {
                        vaccinationSchedule = (String) vaccinationObj;
                    } else if (vaccinationObj instanceof Map) {
                        Map<String, Object> vMap = (Map<String, Object>) vaccinationObj;
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, Object> entry2 : vMap.entrySet()) {
                            sb.append(entry2.getKey()).append(": ").append(entry2.getValue()).append("; ");
                        }
                        vaccinationSchedule = sb.toString();
                    }
                    if (vaccinationSchedule != null && !vaccinationSchedule.isEmpty()) {
                        String[] schedules = vaccinationSchedule.split(";");
                        for (String schedule : schedules) {
                            String[] parts = schedule.split(":");
                            if (parts.length == 2) {
                                String dateStr = parts[1].replaceAll("[^0-9/]", "").trim();
                                try {
                                    LocalDate vDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                    if (vDate.equals(today) || vDate.plusMonths(3).equals(today)) {
                                        hasVaccination = true;
                                        notificationMessages.add("Đến lịch tiêm phòng cho " + name + " vào ngày " + dateStr);
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }
            View notiBtn = getView().findViewById(R.id.btnNotification);
            if (notiBtn != null) {
                notiBtn.setVisibility(View.VISIBLE);
                notiBtn.setActivated(hasBirthday || hasVaccination);
                notiBtn.setOnClickListener(v -> showNotificationPopup());
            }
        });
    }

    private void showNotificationPopup() {
        if (notificationMessages.isEmpty()) {
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Thông báo")
                .setMessage("Không có thông báo nào hôm nay.")
                .setPositiveButton("OK", null)
                .show();
        } else {
            StringBuilder message = new StringBuilder();
            for (String msg : notificationMessages) {
                message.append("• ").append(msg).append("\n");
            }
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Thông báo hôm nay")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
