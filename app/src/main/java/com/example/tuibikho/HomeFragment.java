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

import com.example.tuibikho.data.ProductEntity;
import com.example.tuibikho.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.tuibikho.databinding.FragmentHomeScreenBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeScreenBinding binding;
    private ProductAdapter productAdapter;
    private PetTypeAdapter petTypeAdapter;
    private HomeViewModel viewModel;


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

        binding.btnSeeAll.setOnClickListener(v -> navigateToAllProducts(null));
        binding.btnSeeAllExplore.setOnClickListener(v -> navigateToAllProducts(null));
    }

    private void setupRecyclerViews() {
        // Product Adapter
        productAdapter = new ProductAdapter();
        binding.recyclerExplore.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerExplore.setAdapter(productAdapter);
        productAdapter.setOnItemClickListener(product -> {
            // Mở ProductDetailFragment với thông tin sản phẩm đầy đủ
            ProductDetailFragment detailFragment = ProductDetailFragment.newInstance(product);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // PetType Adapter
        binding.recyclerPetType.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        List<PetType> petTypeList = new ArrayList<>();
        petTypeList.add(new PetType(R.drawable.img_dog, "Dog"));
        petTypeList.add(new PetType(R.drawable.img_cat, "Cat"));
        petTypeAdapter = new PetTypeAdapter(petTypeList);
        binding.recyclerPetType.setAdapter(petTypeAdapter);
    }

    private void setupPetTypeClick() {
        petTypeAdapter.setOnItemClickListener(petType -> navigateToAllProducts(petType.getName()));
    }

    private void navigateToAllProducts(String filter) {
        AllProductsFragment allProductsFragment = new AllProductsFragment();
        if (filter != null) {
            Bundle bundle = new Bundle();
            bundle.putString("PET_TYPE_FILTER", filter);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
