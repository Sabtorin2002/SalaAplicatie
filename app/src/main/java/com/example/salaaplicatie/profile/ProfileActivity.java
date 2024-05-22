package com.example.salaaplicatie.profile;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salaaplicatie.helpers.FirebaseHelper;
import com.example.salaaplicatie.helpers.NavigationHelper;
import com.example.salaaplicatie.home.HomeActivity;
import com.example.salaaplicatie.R;
import com.example.salaaplicatie.workout.WorkoutActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private TextView tvUsername;
    private TextView tvNumarAntrenamente;
    private ImageView imagineProfil;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth mAuth;
    private  FirebaseFirestore db;

    private ActivityResultLauncher<String>getContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomProfile);

        imagineProfil=findViewById(R.id.imgProfil);
        tvUsername=findViewById(R.id.tvUsernameProfil);
        tvNumarAntrenamente=findViewById(R.id.tvNumarAntrenamente);

        firebaseHelper=new FirebaseHelper();
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        imagineProfil.setOnClickListener(v->chooseImage());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (R.id.bottomHome==item.getItemId()){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
                if(R.id.bottomDumbell==item.getItemId()){
                    Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);
                    startActivity(intent);
                }
                if(R.id.bottomProfile==item.getItemId()){
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        getContent=registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                            imagineProfil.setImageURI(uri);
                        }
                });

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("Users")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String username = document.getString("username");
                                if (username != null) {
                                    tvUsername.setText(username);
                                } else {
                                    tvUsername.setText("Unknown User");
                                    Log.e("ProfileActivity", "Username field is null for user ID: " + userId);
                                }
                            } else {
                                Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                Log.e("ProfileActivity", "User document does not exist for user ID: " + userId);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                            Log.e("ProfileActivity", "Failed to fetch user data: ", task.getException());
                        }
                    });

            // Fetch the total number of workouts
            db.collection("workouts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                int workoutCount = querySnapshot.size();
                                tvNumarAntrenamente.setText("Workouts: " + workoutCount);
                            } else {
                                tvNumarAntrenamente.setText("Workouts: 0");
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to load workouts", Toast.LENGTH_SHORT).show();
                            Log.e("ProfileActivity", "Failed to fetch workouts: ", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
        }
    }


    private void chooseImage() {
        getContent.launch("image/*");
    }

}