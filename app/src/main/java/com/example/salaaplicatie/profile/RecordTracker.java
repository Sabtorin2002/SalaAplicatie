package com.example.salaaplicatie.profile;

import android.nfc.Tag;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


//public class RecordTracker {
//    private Map<String, Float> weightRecords = new HashMap<>();
//    private Map<String, Float> volumeRecords = new HashMap<>();
//
//    public void updateRecords(String exerciseName, float weight, float volume) {
//        weightRecords.put(exerciseName, Math.max(weightRecords.getOrDefault(exerciseName, 0.0f), weight));
//        volumeRecords.put(exerciseName, Math.max(volumeRecords.getOrDefault(exerciseName, 0.0f), volume));
//    }
//
//    public boolean isNewWeightRecord(String exerciseName, float weight) {
//        return weight > weightRecords.getOrDefault(exerciseName, 0.0f);
//    }
//
//    public boolean isNewVolumeRecord(String exerciseName, float volume) {
//        return volume > volumeRecords.getOrDefault(exerciseName, 0.0f);
//    }
//}
public class RecordTracker {
    private Map<String, Float> weightRecords = new HashMap<>();
    private Map<String, Float> volumeRecords = new HashMap<>();

    public boolean isNewWeightRecord(String exerciseName, float weight) {
        Float currentRecord = weightRecords.get(exerciseName);
        Log.d("RecordTracker", "Checking weight record for: " + exerciseName + " Current: " + currentRecord + " New: " + weight);
        return currentRecord == null || weight > currentRecord;
    }

    public boolean isNewVolumeRecord(String exerciseName, float volume) {
        Float currentRecord = volumeRecords.get(exerciseName);
        Log.d("RecordTracker", "Checking volume record for: " + exerciseName + " Current: " + currentRecord + " New: " + volume);
        return currentRecord == null || volume > currentRecord;
    }

    public void updateRecords(String exerciseName, float weight, float volume) {
        if (isNewWeightRecord(exerciseName,weight)) {
            weightRecords.put(exerciseName, weight);
            Log.d("RecordTracker", "Updated weight record for: " + exerciseName + " to: " + weight);
        }

        if (isNewVolumeRecord(exerciseName,volume)) {
            volumeRecords.put(exerciseName, volume);
            Log.d("RecordTracker", "Updated volume record for: " + exerciseName + " to: " + volume);
        }

        Log.d("RecordTracker", "After updating: " + exerciseName + " Weight record: " + weightRecords.get(exerciseName));
        Log.d("RecordTracker", "After updating: " + exerciseName + " Volume record: " + volumeRecords.get(exerciseName));
    }
}
