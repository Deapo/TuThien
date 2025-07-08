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
    private String currentSearchQuery = null;
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
            currentSearchQuery = getArguments().getString("SEARCH_QUERY");
        }

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        setupRecyclerView();
        setupSearchBar();
        observeViewModel();
        setupFilterChips();
        viewModel.FetchDataFromFirestore();
    }

    private void setupSearchBar() {
        // Setup search bar click listener
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
        if (currentSearchQuery != null) {
            input.setText(currentSearchQuery);
        }
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Tìm kiếm", (dialog, which) -> {
            String query = input.getText().toString().trim();
            if (!query.isEmpty()) {
                currentSearchQuery = query;
                filterAndDisplayProducts();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.setNeutralButton("Xóa tìm kiếm", (dialog, which) -> {
            currentSearchQuery = null;
            filterAndDisplayProducts();
        });

        builder.show();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter();
        int spanCount = 2;
        binding.recyclerAllProducts.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        binding.recyclerAllProducts.setAdapter(productAdapter);

        int spacingPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        binding.recyclerAllProducts.addItemDecoration(new GridSpacing(spacingPixels, spanCount, true));

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
        List<ProductEntity> filteredList = new ArrayList<>(allProducts);

        // Apply pet type filter
        if (currentFilter != null && !currentFilter.equalsIgnoreCase("All")) {
            filteredList = filteredList.stream()
                    .filter(p -> p.getType() != null && currentFilter.equalsIgnoreCase(p.getType()))
                    .collect(Collectors.toList());
        }

        // Apply search query filter
        if (currentSearchQuery != null && !currentSearchQuery.trim().isEmpty()) {
            String searchLower = currentSearchQuery.toLowerCase().trim();
            filteredList = filteredList.stream()
                    .filter(p -> {
                        boolean nameMatch = p.getName() != null && 
                                          p.getName().toLowerCase().contains(searchLower);
                        boolean descriptionMatch = p.getDescription() != null && 
                                                 p.getDescription().toLowerCase().contains(searchLower);
                        boolean typeMatch = p.getType() != null && 
                                          p.getType().toLowerCase().contains(searchLower);
                        return nameMatch || descriptionMatch || typeMatch;
                    })
                    .collect(Collectors.toList());
        }

        productAdapter.submitList(filteredList);
        updateChipSelection();
        updateSearchHint();
        
        // Show empty state if no results
        if (filteredList.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
        }
    }

    private void showEmptyState() {
        // You can add an empty state view to your layout and show it here
        Toast.makeText(requireContext(), "Không tìm thấy sản phẩm phù hợp", Toast.LENGTH_SHORT).show();
    }

    private void hideEmptyState() {
        // Hide empty state view if you have one
    }

    private void updateSearchHint() {
        View searchHintView = getView().findViewById(R.id.tvSearchHint);
        if (searchHintView != null) {
            if (currentSearchQuery != null && !currentSearchQuery.trim().isEmpty()) {
                ((android.widget.TextView) searchHintView).setText("Tìm kiếm: " + currentSearchQuery);
            } else {
                ((android.widget.TextView) searchHintView).setText(getString(R.string.placeholder_search));
            }
        }
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