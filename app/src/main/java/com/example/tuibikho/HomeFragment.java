package com.example.tuibikho;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen, container, false);

        // Trending
        RecyclerView trendingRecycler = view.findViewById(R.id.recyclerTrend);
        List<TrendItem> trendingList = Arrays.asList(
                new TrendItem(R.drawable.banner_trending, "Free puppy socialisation", "FREE"),
                new TrendItem(R.drawable.banner_trending1, "Eco-friendly toys your dog will love", "Shop Green Elk"),
                new TrendItem(R.drawable.banner_trending2, "Free Delicious Dog Food", "FREE")
        );
        TrendAdapter trendAdapter = new TrendAdapter(trendingList);
        trendingRecycler.setAdapter(trendAdapter);
        trendingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Pet Types
        RecyclerView petTypeRecycler = view.findViewById(R.id.recyclerPetType);
        List<PetType> petTypeList = Arrays.asList(
                new PetType(R.drawable.img_dog, "Dog"),
                new PetType(R.drawable.img_cat, "Cat")
                // Thêm các mục khác
        );
        PetTypeAdapter petTypeAdapter = new PetTypeAdapter(petTypeList);
        petTypeRecycler.setAdapter(petTypeAdapter);
        petTypeRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Bottom Navigation
        fragmentManager = getActivity().getSupportFragmentManager();
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigate);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.explore) {
                selectedFragment = new ExploreFragment();
            } else if (itemId == R.id.favorite) {
                selectedFragment = new FavoriteFragment();
            } else if (itemId == R.id.account) {
                selectedFragment = new AccountFragment();
            }

            if (selectedFragment != null) {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
                return true;
            }
            return false;
        });

        return view;
    }
}
