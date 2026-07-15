package com.example.recipebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

public class RecipeDetailsActivity extends AppCompatActivity {

    private TextView tvTitle, tvIngredients, tvSteps, tvCategory;
    private ImageView imgRecipe;
    private Button btnWatchVideo, btnEdit, btnDelete;
    private String recipeId, youtubeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);


        tvTitle = findViewById(R.id.tvRecipeName);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvSteps = findViewById(R.id.tvPreparationSteps);
        tvCategory = findViewById(R.id.tvRecipeCategory);
        imgRecipe = findViewById(R.id.imgRecipeDetails);
        btnWatchVideo = findViewById(R.id.btnWatchVideo);
        btnEdit = findViewById(R.id.btnEditRecipe);
        btnDelete = findViewById(R.id.btnDeleteRecipe);


        recipeId = getIntent().getStringExtra("recipeId");
        String title = getIntent().getStringExtra("title");
        String ingredients = getIntent().getStringExtra("ingredients");
        String steps = getIntent().getStringExtra("steps");
        String category = getIntent().getStringExtra("category");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        youtubeUrl = getIntent().getStringExtra("youtubeUrl");

        tvTitle.setText(title);
        tvIngredients.setText(ingredients);
        tvSteps.setText(steps);
        tvCategory.setText(category);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).error(R.drawable.img_6).into(imgRecipe);
        }


        btnWatchVideo.setOnClickListener(v -> {
            if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl)));
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (recipeId != null) {
                FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            }
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditRecipeActivity.class);
            intent.putExtra("recipeId", recipeId);
            startActivity(intent);
        });
    }
}