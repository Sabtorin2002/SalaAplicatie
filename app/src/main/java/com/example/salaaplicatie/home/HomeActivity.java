package com.example.salaaplicatie.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.salaaplicatie.firstSteps.LoginActivity;
import com.example.salaaplicatie.helpers.NavigationHelper;
import com.example.salaaplicatie.profile.ProfileActivity;
import com.example.salaaplicatie.R;
import com.example.salaaplicatie.workout.WorkoutActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomHome);

        
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

    }

}