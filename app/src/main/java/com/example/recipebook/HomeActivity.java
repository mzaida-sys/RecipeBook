package com.example.recipebook;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgProfile, imgClearSearch;
    private EditText etSearch;
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgProfile = findViewById(R.id.imgHomeProfile);
        etSearch = findViewById(R.id.etSearch);
        imgClearSearch = findViewById(R.id.imgClearSearch);
        tabLayout = findViewById(R.id.tabLayoutCategories);
        fabAdd = findViewById(R.id.fabAddRecipe);
        recyclerView = findViewById(R.id.recyclerViewRecipes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecipeAdapter(this, recipeList);
        recyclerView.setAdapter(adapter);

        imgProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        setupTabs();
        fetchRecipes();
        loadProfileImage();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterBySearch(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        imgClearSearch.setOnClickListener(v -> etSearch.setText(""));
        fabAdd.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AddRecipeActivity.class)));
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Sweets"));
        tabLayout.addTab(tabLayout.newTab().setText("Meals"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.filterByCategory(tab.getText().toString());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchRecipes() {
        FirebaseDatabase.getInstance().getReference("Recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Recipe recipe = ds.getValue(Recipe.class);
                    if (recipe != null) recipeList.add(recipe);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadProfileImage() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("profileImageUrl")) {
                        String img = snapshot.child("profileImageUrl").getValue(String.class);
                        if (img != null && !img.isEmpty()) {
                            Glide.with(HomeActivity.this).load(img).into(imgProfile);
                        }
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}