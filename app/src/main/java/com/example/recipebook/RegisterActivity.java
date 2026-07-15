package com.example.recipebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterName, etRegisterEmail, etRegisterPassword;
    private Spinner spCountry;
    private Button btnRegister;
    private ImageView imgUserPhoto;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        spCountry = findViewById(R.id.spCountry);
        btnRegister = findViewById(R.id.btnRegister);
        imgUserPhoto = findViewById(R.id.imgUserPhoto);

        imgUserPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnRegister.setOnClickListener(v -> registerNewUser());
    }

    private void registerNewUser() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            String userId = authResult.getUser().getUid();

            new Thread(() -> uploadToSupabase(userId)).start();
        }).addOnFailureListener(e -> {
            btnRegister.setEnabled(true);
            Toast.makeText(RegisterActivity.this, "Registration error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void uploadToSupabase(String userId) {
        try {
            android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            String fileName = userId + ".jpg";
            File file = new File(getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            String imageUrl = "https://msbbampvdwbuwuoqnyiw.supabase.co/storage/v1/object/public/recipe-images/" + fileName;

            runOnUiThread(() -> saveToFirebase(userId, imageUrl));

        } catch (Exception e) {
            runOnUiThread(() -> {
                btnRegister.setEnabled(true);
                Toast.makeText(this, "Image processing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveToFirebase(String userId, String imageUrl) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", etRegisterName.getText().toString());
        userMap.put("email", etRegisterEmail.getText().toString());
        userMap.put("country", spCountry.getSelectedItem().toString());
        userMap.put("profileImageUrl", imageUrl);

        mDatabase.child(userId).setValue(userMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                finish();
            } else {
                btnRegister.setEnabled(true);
                Toast.makeText(this, "Data saving failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgUserPhoto.setImageURI(imageUri);
        }
    }
}