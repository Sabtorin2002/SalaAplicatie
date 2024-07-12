package com.example.salaaplicatie.profile;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salaaplicatie.R;
import com.example.salaaplicatie.profile.WorkoutProfilAdapter;
import com.example.salaaplicatie.home.HomeActivity;
import com.example.salaaplicatie.helpers.FirebaseHelper;
import com.example.salaaplicatie.workout.WorkoutActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private TextView tvUsername;
    private TextView tvNumarAntrenamente;
    private ImageView imagineProfil;
    private FirebaseHelper firebaseHelper;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityResultLauncher<String> getContent;
    private LinearLayout chartLayout;
    private Spinner chartSelector;
    private Grafice grafice;
    private Button buttonDuration;
    private Button buttonVolume;
    private RecyclerView rvAntrenamentProfil;
    private WorkoutProfilAdapter adapter;
    private List<AntrenamentProfil> workoutList = new ArrayList<>();
    private RecordTracker recordTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomProfile);

        imagineProfil = findViewById(R.id.imgProfil);
        tvUsername = findViewById(R.id.tvUsernameProfil);
        tvNumarAntrenamente = findViewById(R.id.tvNumarAntrenamente);
        chartLayout = findViewById(R.id.chartContainter);
        chartSelector = findViewById(R.id.chartSelector);
        rvAntrenamentProfil = findViewById(R.id.rvAntrenamentProfil);

        firebaseHelper = new FirebaseHelper();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonDuration = findViewById(R.id.buttonDuration);
        buttonVolume = findViewById(R.id.buttonVolume);

        imagineProfil.setOnClickListener(v -> chooseImage());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (R.id.bottomHome == item.getItemId()) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
                if (R.id.bottomDumbell == item.getItemId()) {
                    Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);
                    startActivity(intent);
                }
                if (R.id.bottomProfile == item.getItemId()) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        imagineProfil.setImageURI(uri);
                    }
                });

        recordTracker = new RecordTracker();
        rvAntrenamentProfil.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkoutProfilAdapter(workoutList, recordTracker);
        rvAntrenamentProfil.setAdapter(adapter);

        loadUserData();
        loadWorkouts();
        initializeCharts();

        buttonDuration.setOnClickListener(v -> grafice.updateChartType("duration"));
        buttonVolume.setOnClickListener(v -> grafice.updateChartType("volume"));
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


    private void loadWorkouts() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("workouts")
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                workoutList.clear();
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    AntrenamentProfil workout = document.toObject(AntrenamentProfil.class);
                                    if (workout != null) {
                                        workout.updateGlobalRecords(recordTracker);
                                        workoutList.add(workout);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to load workouts", Toast.LENGTH_SHORT).show();
                            Log.e("ProfileActivity", "Failed to fetch workouts: ", task.getException());
                        }
                    });
        }
    }



    private void chooseImage() {
        getContent.launch("image/*");
    }

    private void initializeCharts() {
        grafice = new Grafice(this, chartLayout, chartSelector);
    }
}
