package com.example.salaaplicatie.workout;

public class ExercitiiSet {
    private boolean isConfirmed;
    private double weight;
    private int reps;
    private double rpe;

    public ExercitiiSet(double weight, int reps, double rpe) {
        this.weight = weight;
        this.reps = reps;
        this.rpe = rpe;
        this.isConfirmed=false;
    }

    public double getWeight() {
        return weight;
    }

    public int getReps() {
        return reps;
    }

    public double getRpe() {
        return rpe;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setRpe(double rpe) {
        this.rpe = rpe;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
}
