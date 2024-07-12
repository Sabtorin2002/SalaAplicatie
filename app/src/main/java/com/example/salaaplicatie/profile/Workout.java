package com.example.salaaplicatie.profile;

import java.util.List;
import java.util.Map;

public class Workout {
    private float duration;
    private float volume;

    public Workout(Map<String, Object> map) {
        if (map.containsKey("duration")) {
            this.duration = ((Number) map.get("duration")).floatValue();
        }
        this.volume = calculateVolume(map);
    }

    private float calculateVolume(Map<String, Object> map) {
        float totalVolume = 0;
        if (map.containsKey("exercises")) {
            List<Map<String, Object>> exercises = (List<Map<String, Object>>) map.get("exercises");
            for (Map<String, Object> exercise : exercises) {
                if (exercise.containsKey("sets")) {
                    List<Map<String, Object>> sets = (List<Map<String, Object>>) exercise.get("sets");
                    for (Map<String, Object> set : sets) {
                        if (set.containsKey("weight") && set.containsKey("reps")) {
                            float weight = ((Number) set.get("weight")).floatValue();
                            int reps = ((Number) set.get("reps")).intValue();
                            totalVolume += weight * reps;
                        }
                    }
                }
            }
        }
        return totalVolume;
    }

    public float getDuration() {
        return duration;
    }

    public float getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "duration=" + duration +
                ", volume=" + volume +
                '}';
    }
}
