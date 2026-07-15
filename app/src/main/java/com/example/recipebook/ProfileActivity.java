package com.example.recipebook;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvProfileName, tvProfileEmail, tvProfileCountry;
    private ImageView imgProfilePic;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private RecyclerView rvUserRecipes;
    private RecipeAdapter userRecipesAdapter;
    private List<Recipe> userRecipesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileCountry = findViewById(R.id.tvProfileCountry);
        imgProfilePic = findViewById(R.id.imgProfileUserPhoto);
        btnLogout = findViewById(R.id.btnLogout);
        rvUserRecipes = findViewById(R.id.recyclerViewUserRecipes);

        rvUserRecipes.setLayoutManager(new LinearLayoutManager(this));
        userRecipesAdapter = new RecipeAdapter(this, userRecipesList);
        rvUserRecipes.setAdapter(userRecipesAdapter);

        if (mAuth.getCurrentUser() != null) {
            DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        tvProfileName.setText(snapshot.child("fullName").getValue(String.class));
                        tvProfileEmail.setText(snapshot.child("email").getValue(String.class));
                        tvProfileCountry.setText(snapshot.child("country").getValue(String.class));
                        String imageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(ProfileActivity.this).load(imageUrl).into(imgProfilePic);
                        }
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });

            FirebaseDatabase.getInstance().getReference("Recipes")
                    .orderByChild("userId")
                    .equalTo(mAuth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userRecipesList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                userRecipesList.add(ds.getValue(Recipe.class));
                            }
                            userRecipesAdapter.notifyDataSetChanged();
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
        });
    }
}