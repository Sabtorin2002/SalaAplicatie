package com.example.salaaplicatie.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.salaaplicatie.R;
import com.example.salaaplicatie.workout.LogWorkout;
import com.google.android.material.snackbar.Snackbar;

public class NavigationHelper {
    public static void showSnackBar(View view, Context context, int anchorViewId){
        Snackbar snackbar = Snackbar.make(view,"Workout in progress?",Snackbar.LENGTH_INDEFINITE)
                .setAction("Resume Workout", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, LogWorkout.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent);
                    }
                });

        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setAnchorView(anchorViewId);

        int backgroundColor= ContextCompat.getColor(context, R.color.granitegray);
        snackbar.setBackgroundTint(backgroundColor);

        snackbar.show();
    }
}
