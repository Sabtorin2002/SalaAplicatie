package com.example.salaaplicatie.profile;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntrenamentProfil {
    private static final String TAG = "AntrenamentProfil";
    private String userId;
    private Timestamp timestamp;
    private float volume;
    private int duration;
    private List<Map<String, Object>> exercises;
    private Map<String, Float> maxWeightRecords = new HashMap<>();
    private Map<String, Float> maxVolumeRecords = new HashMap<>();

    public AntrenamentProfil() {
        // Constructor gol
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Map<String, Object>> getExercises() {
        return exercises;
    }

    public void setExercises(List<Map<String, Object>> exercises) {
        this.exercises = exercises;
        calculateVolume();
    }

    public float getVolume() {
        return volume;
    }

    public Map<String, Float> getMaxWeightRecords() {
        return maxWeightRecords;
    }

    public Map<String, Float> getMaxVolumeRecords() {
        return maxVolumeRecords;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private void calculateVolume() {
        float totalVolume = 0;
        if (exercises != null) {
            for (Map<String, Object> exercise : exercises) {
                List<Map<String, Object>> sets = (List<Map<String, Object>>) exercise.get("sets");
                if (sets != null) {
                    for (Map<String, Object> set : sets) {
                        float weight = ((Number) set.get("weight")).floatValue();
                        int reps = ((Number) set.get("reps")).intValue();
                        totalVolume += weight * reps;
                    }
                }
            }
        }
        this.volume = totalVolume;
    }


//    public void updateGlobalRecords(RecordTracker recordTracker) {
//        if (exercises != null) {
//            for (Map<String, Object> exercise : exercises) {
//                String exerciseName = (String) exercise.get("exerciseName");
//                List<Map<String, Object>> sets = (List<Map<String, Object>>) exercise.get("sets");
//                if (sets != null) {
//                    for (Map<String, Object> set : sets) {
//                        float weight = ((Number) set.get("weight")).floatValue();
//                        int reps = ((Number) set.get("reps")).intValue();
//                        float volume = weight * reps;
//
//                        boolean newWeightRecord = recordTracker.isNewWeightRecord(exerciseName, weight);
//                        boolean newVolumeRecord = recordTracker.isNewVolumeRecord(exerciseName, volume);
//
//                        Log.d("AntrenamentProfil", "Before update: Exercise: " + exerciseName + " Weight: " + weight + " New Weight Record: " + newWeightRecord);
//                        Log.d("AntrenamentProfil", "Before update: Exercise: " + exerciseName + " Volume: " + volume + " New Volume Record: " + newVolumeRecord);
//
//                        recordTracker.updateRecords(exerciseName, weight, volume);
//
//                        boolean updatedWeightRecord = recordTracker.isNewWeightRecord(exerciseName, weight);
//                        boolean updatedVolumeRecord = recordTracker.isNewVolumeRecord(exerciseName, volume);
//
//                        Log.d("AntrenamentProfil", "After update: Exercise: " + exerciseName + " Weight: " + weight + " New Weight Record: " + updatedWeightRecord);
//                        Log.d("AntrenamentProfil", "After update: Exercise: " + exerciseName + " Volume: " + volume + " New Volume Record: " + updatedVolumeRecord);
//
//                        if (newWeightRecord) {
//                            maxWeightRecords.put(exerciseName, weight);
//                        }
//                        if (newVolumeRecord) {
//                            maxVolumeRecords.put(exerciseName, volume);
//                        }
//
//                        Log.d(TAG, "Updated global records for: " + exerciseName + " Weight: " + weight + " Volume: " + volume);
//                    }
//                }
//            }
//        }
//    }

    public void updateGlobalRecords(RecordTracker recordTracker) {
        if (exercises != null) {
            for (Map<String, Object> exercise : exercises) {
                String exerciseName = (String) exercise.get("exerciseName");
                List<Map<String, Object>> sets = (List<Map<String, Object>>) exercise.get("sets");
                if (sets != null) {
                    for (Map<String, Object> set : sets) {
                        float weight = ((Number) set.get("weight")).floatValue();
                        int reps = ((Number) set.get("reps")).intValue();
                        float volume = weight * reps;

                        boolean newWeightRecord = recordTracker.isNewWeightRecord(exerciseName, weight);
                        boolean newVolumeRecord = recordTracker.isNewVolumeRecord(exerciseName, volume);

                        Log.d("AntrenamentProfil", "Before update: Exercise: " + exerciseName + " Weight: " + weight + " New Weight Record: " + newWeightRecord);
                        Log.d("AntrenamentProfil", "Before update: Exercise: " + exerciseName + " Volume: " + volume + " New Volume Record: " + newVolumeRecord);

                        recordTracker.updateRecords(exerciseName, weight, volume);

                        if (newWeightRecord) {
                            maxWeightRecords.put(exerciseName, weight);
                        }
                        if (newVolumeRecord) {
                            maxVolumeRecords.put(exerciseName, volume);
                        }

                        Log.d(TAG, "Updated global records for: " + exerciseName + " Weight: " + weight + " Volume: " + volume);
                    }
                }
            }
        }
    }


    public boolean isNewWeightRecord(String exerciseName, float weight) {
        return weight > maxWeightRecords.getOrDefault(exerciseName, 0.0f);
    }

    public boolean isNewVolumeRecord(String exerciseName, float volume) {
        return volume > maxVolumeRecords.getOrDefault(exerciseName, 0.0f);
    }
}
