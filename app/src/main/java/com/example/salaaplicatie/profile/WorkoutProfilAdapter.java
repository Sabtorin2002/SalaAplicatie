package com.example.salaaplicatie.profile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.salaaplicatie.R;
import com.example.salaaplicatie.profile.AntrenamentProfil;
import com.example.salaaplicatie.profile.RecordTracker;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WorkoutProfilAdapter extends RecyclerView.Adapter<WorkoutProfilAdapter.ViewHolder> {
    private List<AntrenamentProfil> workouts;
    private RecordTracker recordTracker;

    public WorkoutProfilAdapter(List<AntrenamentProfil> workouts, RecordTracker recordTracker) {
        this.workouts = workouts;
        this.recordTracker = recordTracker;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.antrenament_profil, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AntrenamentProfil workout = workouts.get(position);
        holder.bind(workout, recordTracker);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkoutDate, tvWorkoutDuration, tvWorkoutVolume, tvWorkoutProfilExercises;
        ImageView imgMedal;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWorkoutDate = itemView.findViewById(R.id.tvWorkoutDate);
            tvWorkoutDuration = itemView.findViewById(R.id.tvWorkoutDuration);
            tvWorkoutVolume = itemView.findViewById(R.id.tvWorkoutVolume);
            tvWorkoutProfilExercises = itemView.findViewById(R.id.tvWorkoutProfilExercices);
            imgMedal = itemView.findViewById(R.id.imgMedal);
        }

        public void bind(AntrenamentProfil workout, RecordTracker recordTracker) {
            Timestamp timestamp = workout.getTimestamp();
            if (timestamp != null) {
                Date date = timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                String dateString = sdf.format(date);
                tvWorkoutDate.setText(dateString);
            } else {
                tvWorkoutDate.setText("N/A");
            }

            tvWorkoutDuration.setText("Duration: " + workout.getDuration() + " mins");
            tvWorkoutVolume.setText("Volume: " + workout.getVolume() + " kg");

            StringBuilder exercisesBuilder = new StringBuilder();
            boolean hasNewRecord = false;
            List<Map<String, Object>> exercises = workout.getExercises();
            if (exercises != null) {
                for (Map<String, Object> exercise : exercises) {
                    String exerciseName = (String) exercise.get("exerciseName");
                    String equipment = (String) exercise.get("equipment");
                    List<Map<String, Object>> sets = (List<Map<String, Object>>) exercise.get("sets");
                    exercisesBuilder.append(exerciseName).append(" (").append(equipment).append(")\n");

                    if (sets != null) {
                        for (Map<String, Object> set : sets) {
                            float weight = ((Number) set.get("weight")).floatValue();
                            int reps = ((Number) set.get("reps")).intValue();
                            float volume = weight * reps;
                            float rpe = ((Number) set.get("rpe")).floatValue();

                            exercisesBuilder.append("  - Reps: ").append(reps)
                                    .append(", Weight: ").append(weight).append(" kg\n")
                                    .append("  RPE: ").append(rpe).append("\n");



                            boolean isNewWeightRecord = workout.isNewWeightRecord(exerciseName, weight);
                            boolean isNewVolumeRecord = workout.isNewVolumeRecord(exerciseName, volume);


                            Log.d("ViewHolder", "Weight record for " + exerciseName + ": " + isNewWeightRecord);
                            Log.d("ViewHolder", "Volume record for " + exerciseName + ": " + isNewVolumeRecord);

                            if (isNewWeightRecord || isNewVolumeRecord) {
                                hasNewRecord = true;
                                if (isNewWeightRecord) {
                                    exercisesBuilder.append("  * New Weight Record!\n");
                                }
                                if (isNewVolumeRecord) {
                                    exercisesBuilder.append("  * New Volume Record!\n");
                                }
                            }

                            recordTracker.updateRecords(exerciseName, weight, volume);
                        }
                    }
                }
            } else {
                exercisesBuilder.append("No exercises data available");
            }
            tvWorkoutProfilExercises.setText(exercisesBuilder.toString());

            Log.d("ViewHolder", "Has new record: " + hasNewRecord);
            imgMedal.setVisibility(hasNewRecord ? View.VISIBLE : View.GONE);

            Log.d("ViewHolder", "Exercises: " + exercisesBuilder.toString());
            Log.d("ViewHolder", "Medal visibility: " + (hasNewRecord ? "VISIBLE" : "GONE"));
        }


    }
}