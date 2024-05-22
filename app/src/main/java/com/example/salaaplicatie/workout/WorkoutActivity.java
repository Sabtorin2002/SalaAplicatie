package com.example.salaaplicatie.workout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.salaaplicatie.helpers.NavigationHelper;
import com.example.salaaplicatie.home.HomeActivity;
import com.example.salaaplicatie.R;
import com.example.salaaplicatie.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class WorkoutActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> logWorkoutLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomHome);

        bottomNavigationView.setSelectedItemId(R.id.bottomDumbell);

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

        FrameLayout startEmptyWorkout=findViewById(R.id.flStartEmptyWorkout);
        startEmptyWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutActivity.this, LogWorkout.class);
                intent.putExtra("startTimer",true);
                startActivity(intent);
            }
        });

        View view = findViewById(R.id.WorkoutActivity);
        NavigationHelper.showSnackBar(view,this,R.id.bottomNavigationView);
    }
}