package com.example.parkingfindermtl.models;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Polyline extends RealmObject {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private boolean allYear;

    private double startLat;

    private double startLng;

    private double endLat;

    private double endLng;

    private String vile;

    private String rue;

    private RealmList<Day> days;

    public Polyline() {
    }

    public Polyline(boolean allYear, double startLat, double startLng, double endLat, double endLng, String vile, String rue) {
        this.allYear = allYear;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.vile = vile;
        this.rue = rue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAllYear() {
        return allYear;
    }

    public void setAllYear(boolean allYear) {
        this.allYear = allYear;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public RealmList<Day> getDays() {
        return days;
    }

    public void setDays(RealmList<Day> days) {
        this.days = days;
    }

    public String getVile() {
        return vile;
    }

    public void setVile(String vile) {
        this.vile = vile;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }
}
