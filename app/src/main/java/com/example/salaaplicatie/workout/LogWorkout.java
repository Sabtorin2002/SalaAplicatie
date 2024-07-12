package com.example.salaaplicatie.workout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.salaaplicatie.R;
import com.example.salaaplicatie.profile.ProfileActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogWorkout extends AppCompatActivity {

    private long startTime;

    private TextView tvTimer;
    private Button addExercise;
    private Button btnFinishSave;

    private Handler timerHandler=new Handler();
    private Runnable timerRunnable=new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis()-startTime;
            int seconds =(int)(millis/1000);
            int minutes = seconds/60;
            seconds=seconds%60;
            int hours=minutes/60;
            minutes=minutes%60;

            String timeFormatted = String.format("%d:%02d:%02d", hours, minutes, seconds);
            tvTimer.setText(timeFormatted);
            timerHandler.postDelayed(this,500);

        }
    };
    private RecyclerView rvAntrenament;
    private String exerciseName;
    private String equipment;
    private List<ExercitiiSet> exerciseSets = new ArrayList<>();
    private CustomAdapterExercitiiSet customAdapterExercitiiSet;

    private ActivityResultLauncher<Intent>selectExerciseLauncher;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_workout);

        tvTimer=findViewById(R.id.tvTimer);
        startTime=System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable,0);

        boolean startTimer = getIntent().getBooleanExtra("startTimer",false);
        if(startTimer){
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable,0);
        }

        ImageButton imgButtonArrowBack = findViewById(R.id.imgBtnArrowBack);
        imgButtonArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogWorkout.this, WorkoutActivity.class);
                startActivity(intent);

            }
        });

        addExercise=findViewById(R.id.btnAddExercise);
        addExercise.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectExercise.class);
            selectExerciseLauncher.launch(intent);
        });


        rvAntrenament=findViewById(R.id.rvAntrenament);
        customAdapterExercitiiSet=new CustomAdapterExercitiiSet(new ArrayList<>());
        rvAntrenament.setAdapter(customAdapterExercitiiSet);
        rvAntrenament.setLayoutManager(new LinearLayoutManager(this));
        customAdapterExercitiiSet.setOnDataChangedListener(this::updateTotalSetsAndVolume);

        selectExerciseLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                String exerciseName = result.getData().getStringExtra("SelectedExNume");
                String equipment = result.getData().getStringExtra("SelectedExEquipment");
                List<ExercitiiSet>newSets=new ArrayList<>();
                newSets.add(new ExercitiiSet(0,0,0));
                ExercitiiAntrenament newExercise = new ExercitiiAntrenament(exerciseName, equipment, newSets);
                customAdapterExercitiiSet.addExercise(newExercise);

                updateTotalSetsAndVolume();
                Toast.makeText(this, "Exercise added: " + exerciseName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to receive data", Toast.LENGTH_LONG).show();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        btnFinishSave=findViewById(R.id.btnFinish);
        btnFinishSave.setOnClickListener(v->{
            if(mAuth.getCurrentUser()!=null){
                saveWorkoutToFirestore();
            }else {
                Toast.makeText(this, "You need to be logged in to save workouts.", Toast.LENGTH_LONG).show();
            }

            Intent intent= new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

    }

    private void saveWorkoutToFirestore() {
        String userId = mAuth.getCurrentUser().getUid();
        String timeText = tvTimer.getText().toString();
        int totalMinutes = convertTimeToMinutes(timeText);
        Map<String, Object> workout = new HashMap<>();
        workout.put("userId", userId);
        workout.put("timestamp", new Timestamp(new Date()));
        List<Map<String, Object>> exercises = new ArrayList<>();
        for (ExercitiiAntrenament exercise : customAdapterExercitiiSet.getExercitiiAntrenament()) {
            Map<String, Object> exerciseDetails = new HashMap<>();
            exerciseDetails.put("exerciseName", exercise.getExerciseName());
            exerciseDetails.put("equipment", exercise.getEquipment());

            List<Map<String,Object>> sets = new ArrayList<>();
            for (ExercitiiSet set : exercise.getSets()){
                Map<String, Object> setDetails=new HashMap<>();
                setDetails.put("weight",set.getWeight());
                setDetails.put("reps", set.getReps());
                setDetails.put("rpe", set.getRpe());
                setDetails.put("isConfirmed", set.isConfirmed());

                sets.add(setDetails);
            }
            exerciseDetails.put("sets", sets);
            exercises.add(exerciseDetails);
        }
        workout.put("exercises",exercises);
        workout.put("duration",totalMinutes);
        db.collection("workouts").add(workout)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(LogWorkout.this, "Workout saved successfully with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LogWorkout.this, "Failed to save workout: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private int convertTimeToMinutes(String timeText) {
        int minutes = 0;
        String[] units = timeText.split(":");
        if (units.length == 3) {
            minutes = Integer.parseInt(units[0]) * 60 +
                    Integer.parseInt(units[1]) +
                    Integer.parseInt(units[2]) / 60;
        } else if (units.length == 2) {
            minutes = Integer.parseInt(units[0]) +
                    Integer.parseInt(units[1]) / 60;
        }
        return minutes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void updateTotalSetsAndVolume() {
        int totalSets = 0;
        int totalVolume = 0;

        for (ExercitiiAntrenament exercise : customAdapterExercitiiSet.getExercitiiAntrenament()) {
            for (ExercitiiSet set : exercise.getSets()) {
                totalSets++;
                totalVolume += set.getWeight() * set.getReps();
            }
        }

        TextView tvSets = findViewById(R.id.tvSets);
        TextView tvVolume = findViewById(R.id.tvVolume);

        tvSets.setText(String.valueOf(totalSets));
        tvVolume.setText(totalVolume + " kg");
    }


    private void fetchPreviousWorkouts(FirebaseCallback callback) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("workouts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        callback.onCallback(documents);
                    } else {
                        Toast.makeText(LogWorkout.this, "Failed to fetch previous workouts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private interface FirebaseCallback {
        void onCallback(List<DocumentSnapshot> documents);
    }


    private void compareAndIdentifyRecords(List<DocumentSnapshot> previousWorkouts, Map<String, Object> currentWorkout) {
        List<Map<String, Object>> currentExercises = (List<Map<String, Object>>) currentWorkout.get("exercises");
        Map<String, Float> maxWeightRecords = new HashMap<>();
        Map<String, Float> maxVolumeRecords = new HashMap<>();

        // Procesăm antrenamentele anterioare pentru a găsi recordurile maxime
        for (DocumentSnapshot document : previousWorkouts) {
            List<Map<String, Object>> previousExercises = (List<Map<String, Object>>) document.get("exercises");
            for (Map<String, Object> exercise : previousExercises) {
                String exerciseName = (String) exercise.get("exerciseName");
                List<Map<String, Object>> sets = (List<Map<String, Object>>) exercise.get("sets");
                for (Map<String, Object> set : sets) {
                    float weight = ((Number) set.get("weight")).floatValue();
                    int reps = ((Number) set.get("reps")).intValue();
                    float volume = weight * reps;

                    maxWeightRecords.putIfAbsent(exerciseName, weight);
                    if (weight > maxWeightRecords.get(exerciseName)) {
                        maxWeightRecords.put(exerciseName, weight);
                    }

                    maxVolumeRecords.putIfAbsent(exerciseName, volume);
                    if (volume > maxVolumeRecords.get(exerciseName)) {
                        maxVolumeRecords.put(exerciseName, volume);
                    }
                }
            }
        }

        // Comparam exercițiile curente cu recordurile găsite
        for (Map<String, Object> exercise : currentExercises) {
            String exerciseName = (String) exercise.get("exerciseName");
            List<Map<String, Object>> sets = (List<Map<String, Object>>) exercise.get("sets");
            for (Map<String, Object> set : sets) {
                float weight = ((Number) set.get("weight")).floatValue();
                int reps = ((Number) set.get("reps")).intValue();
                float volume = weight * reps;

                if (!maxWeightRecords.containsKey(exerciseName) || weight > maxWeightRecords.get(exerciseName)) {
                    set.put("newWeightRecord", true);
                    maxWeightRecords.put(exerciseName, weight);
                }

                if (!maxVolumeRecords.containsKey(exerciseName) || volume > maxVolumeRecords.get(exerciseName)) {
                    set.put("newVolumeRecord", true);
                    maxVolumeRecords.put(exerciseName, volume);
                }
            }
        }
    }

}