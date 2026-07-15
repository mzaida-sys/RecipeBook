package com.example.recipebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddRecipeActivity extends AppCompatActivity {
    private EditText etTitle, etIngredients, etSteps, etCategory, etYoutubeUrl;
    private ImageView imgSelected;
    private Button btnPost;
    private ProgressBar progressBar;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);


        etTitle = findViewById(R.id.etRecipeTitle);
        etIngredients = findViewById(R.id.etIngredients);
        etSteps = findViewById(R.id.etSteps);
        etCategory = findViewById(R.id.etCategory);
        etYoutubeUrl = findViewById(R.id.etYoutubeUrl);
        imgSelected = findViewById(R.id.imgRecipePlaceholder);
        btnPost = findViewById(R.id.btnPostRecipe);
        progressBar = findViewById(R.id.progressBar);

        imgSelected.setOnClickListener(v -> openGallery());

        btnPost.setOnClickListener(v -> postRecipe());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgSelected.setImageURI(imageUri);
        }
    }

    private void postRecipe() {
        String title = etTitle.getText().toString();
        String finalImageUrl = "Place the direct image link here";

        if (title.isEmpty()) {
            Toast.makeText(this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Recipes").push();
        String id = dbRef.getKey();

        Recipe recipe = new Recipe(id, title, etIngredients.getText().toString(),
                etSteps.getText().toString(), etCategory.getText().toString(),
                etYoutubeUrl.getText().toString(), finalImageUrl);

        dbRef.setValue(recipe).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                finish();
            } else {
                Toast.makeText(this, "Lifting failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}