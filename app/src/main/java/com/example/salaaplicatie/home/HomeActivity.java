package com.example.salaaplicatie.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.salaaplicatie.firstSteps.LoginActivity;
import com.example.salaaplicatie.helpers.NavigationHelper;
import com.example.salaaplicatie.profile.ProfileActivity;
import com.example.salaaplicatie.R;
import com.example.salaaplicatie.workout.WorkoutActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private TextView tvAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomHome);

        tvAboutUs=findViewById(R.id.tvHome);
        
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

        setAboutUsText();
    }

    private void setAboutUsText() {
        String aboutUsText = "Welcome to our application, your ultimate companion for tracking and optimizing your workout routines! Our application is designed to provide you with a comprehensive and intuitive experience to help you achieve your fitness goals efficiently.\n\n" +
                "Features of our application:\n\n" +
                "• Personalized Workout Tracking: Easily log and monitor your workouts with detailed information on exercises, equipment used, sets, reps, weight, and volume. Our app ensures you have all the data you need at your fingertips.\n" +
                "• Record Tracking: Celebrate your progress with our record tracking feature. The app highlights new personal records for weight and volume, motivating you to push your limits and achieve new milestones.\n" +
                "• Visual Progress Charts: Visualize your progress over time with dynamic charts that display your workout duration and volume. Compare your performance across different periods - weekly, monthly, and yearly - to see how far you've come.\n" +
                " We believe in the power of data-driven fitness. Our goal is to empower you with the tools and insights you need to make informed decisions about your workouts and achieve your fitness aspirations. Join our community of fitness enthusiasts and take the first step towards a healthier, stronger you!\n" +
                "Get Started Today!\n\n";
        tvAboutUs.setText(aboutUsText);
    }

}