package com.example.tuibikho;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.tuibikho.data.ProductEntity;
import com.example.tuibikho.databinding.FragmentAllProductsBinding;
import com.example.tuibikho.viewmodel.HomeViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllProductsFragment extends Fragment {
    private FragmentAllProductsBinding binding;
    private ProductAdapter productAdapter;
    private HomeViewModel viewModel;
    private String currentFilter = null;
    private List<ProductEntity> allProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            currentFilter = getArguments().getString("PET_TYPE_FILTER");
        }

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        setupRecyclerView();
        observeViewModel();
        setupFilterChips();
        viewModel.FetchDataFromFirestore();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter();
        binding.recyclerAllProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerAllProducts.setAdapter(productAdapter);

        //chuyen doi dp sang px
        int spacingPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        binding.recyclerAllProducts.addItemDecoration(new GridSpacing(spacingPixels, 2, true));
        productAdapter.setOnItemClickListener(product -> {
            // Mở ProductDetailFragment với thông tin sản phẩm đầy đủ
            ProductDetailFragment detailFragment = ProductDetailFragment.newInstance(product);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            allProducts.clear();
            allProducts.addAll(products);
            filterAndDisplayProducts();
        });
    }

    private void setupFilterChips() {
        updateChipSelection();
        binding.chipAll.setOnClickListener(v -> {
            currentFilter = null;
            filterAndDisplayProducts();
        });
        binding.chipDog.setOnClickListener(v -> {
            currentFilter = "Dog";
            filterAndDisplayProducts();
        });
        binding.chipCat.setOnClickListener(v -> {
            currentFilter = "Cat";
            filterAndDisplayProducts();
        });
    }

    private void filterAndDisplayProducts() {
        if (currentFilter == null || currentFilter.equalsIgnoreCase("All")) {
            productAdapter.submitList(allProducts);
        } else {
            List<ProductEntity> filteredList = allProducts.stream()
                    .filter(p -> p.getType() != null && currentFilter.equalsIgnoreCase(p.getType()))
                    .collect(Collectors.toList());
            productAdapter.submitList(filteredList);
        }
        updateChipSelection();
    }

    private void updateChipSelection() {
        binding.chipAll.setChecked("All".equalsIgnoreCase(currentFilter) || currentFilter == null);
        binding.chipDog.setChecked("Dog".equalsIgnoreCase(currentFilter));
        binding.chipCat.setChecked("Cat".equalsIgnoreCase(currentFilter));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 