package com.example.salaaplicatie.profile;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;



public class RecordTracker {
    private Map<String, Float> weightRecords = new HashMap<>();
    private Map<String, Float> volumeRecords = new HashMap<>();
    public boolean isNewExercise(String exerciseName) {
        return !weightRecords.containsKey(exerciseName) && !volumeRecords.containsKey(exerciseName);
    }

    public boolean isNewWeightRecord(String exerciseName, float weight) {
        Float currentRecord = weightRecords.get(exerciseName);
        Log.d("RecordTracker", "Checking weight record for: " + exerciseName + " Current: " + currentRecord + " New: " + weight);
        return currentRecord == null || weight >= currentRecord;
    }


    public boolean isNewVolumeRecord(String exerciseName, float volume) {
        Float currentRecord = volumeRecords.get(exerciseName);
        Log.d("RecordTracker", "Checking volume record for: " + exerciseName + " Current: " + currentRecord + " New: " + volume);
        return currentRecord == null || volume >= currentRecord;
    }


    public void updateRecords(String exerciseName, float weight, float volume) {

        boolean updated=false;

        if (isNewWeightRecord(exerciseName,weight)) {
            weightRecords.put(exerciseName, weight);
            updated=true;
            Log.d("RecordTracker", "Updated weight record for: " + exerciseName + " to: " + weight);
        }

        if (isNewVolumeRecord(exerciseName,volume)) {
            volumeRecords.put(exerciseName, volume);
            updated=true;
            Log.d("RecordTracker", "Updated volume record for: " + exerciseName + " to: " + volume);
        }

        if (!updated) {
            Log.d("RecordTracker", "No new records for: " + exerciseName);
        }

        Log.d("RecordTracker", "After updating: " + exerciseName + " Weight record: " + weightRecords.get(exerciseName));
        Log.d("RecordTracker", "After updating: " + exerciseName + " Volume record: " + volumeRecords.get(exerciseName));
    }
}
