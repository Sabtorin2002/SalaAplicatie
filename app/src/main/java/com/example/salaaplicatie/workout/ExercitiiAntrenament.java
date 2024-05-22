package com.example.salaaplicatie.workout;

import java.util.List;

public class ExercitiiAntrenament {
    private String exerciseName;
    private String equipment;
    List<ExercitiiSet> sets;

    public ExercitiiAntrenament(String exerciseName, String equipment, List<ExercitiiSet> sets) {
        this.exerciseName = exerciseName;
        this.equipment = equipment;
        this.sets = sets;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getEquipment() {
        return equipment;
    }

    public List<ExercitiiSet> getSets() {
        return sets;
    }
}
