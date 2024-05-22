package com.example.salaaplicatie.workout;

public class ExercitiiPrestabilite {
    private String equipment;
    private String imgUrl;
    private String muscles;
    private String nume;

    public ExercitiiPrestabilite(String equipment, String imgUrl, String muscles, String nume) {
        this.equipment = equipment;
        this.imgUrl = imgUrl;
        this.muscles = muscles;
        this.nume = nume;
    }

    public ExercitiiPrestabilite() {
    }

    public String getNume() {
        return nume;
    }

    public String getEquipment() {
        return equipment;
    }

    public String getMuscles() {
        return muscles;
    }

    public String getImgUrl() {
        return imgUrl;
    }

}
