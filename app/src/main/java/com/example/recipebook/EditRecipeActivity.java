package com.example.recipebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.Map;

public class EditRecipeActivity extends AppCompatActivity {

    private EditText etTitle, etIngredients, etSteps, etCategory, etYoutube;
    private ImageView imgRecipe;
    private TextView tvChangePhoto;
    private Button btnSave;
    private String recipeId;
    private DatabaseReference recipeRef;


    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        etTitle = findViewById(R.id.etEditRecipeTitle);
        etIngredients = findViewById(R.id.etEditIngredients);
        etSteps = findViewById(R.id.etEditPreparationSteps);
        etCategory = findViewById(R.id.etEditClassification);
        etYoutube = findViewById(R.id.etEditYouTubeURL);
        imgRecipe = findViewById(R.id.imgEditRecipePhoto);
        btnSave = findViewById(R.id.btnSaveChanges);
        tvChangePhoto = findViewById(R.id.tvChangePhoto);

        recipeId = getIntent().getStringExtra("recipeId");
        recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId);

        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etTitle.setText(snapshot.child("title").getValue(String.class));
                    etIngredients.setText(snapshot.child("ingredients").getValue(String.class));
                    etSteps.setText(snapshot.child("preparationSteps").getValue(String.class));
                    etCategory.setText(snapshot.child("classification").getValue(String.class));
                    etYoutube.setText(snapshot.child("youtubeVideoURL").getValue(String.class));

                    String imgUrl = snapshot.child("imageUri").getValue(String.class);
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        Glide.with(EditRecipeActivity.this).load(imgUrl).into(imgRecipe);
                    }
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });


        tvChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });


        btnSave.setOnClickListener(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", etTitle.getText().toString());
            map.put("ingredients", etIngredients.getText().toString());
            map.put("preparationSteps", etSteps.getText().toString());
            map.put("classification", etCategory.getText().toString());
            map.put("youtubeVideoURL", etYoutube.getText().toString());


            if (selectedImageUri != null) {
                map.put("imageUri", selectedImageUri.toString());
            }

            recipeRef.updateChildren(map).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgRecipe.setImageURI(selectedImageUri);
        }
    }
}