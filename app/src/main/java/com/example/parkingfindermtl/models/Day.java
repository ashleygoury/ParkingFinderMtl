package com.example.parkingfindermtl.models;

import java.util.Arrays;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Day extends RealmObject {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private int day;

    private RealmList<Time> time;

    public Day() {
    }

    public Day(int day, Time... time) {
        this.day = day;
        if(time.length > 0 ) {
            setTime((new RealmList<Time>()));
            getTime().addAll(Arrays.asList(time));
        }
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public RealmList<Time> getTime() {
        return time;
    }

    public void setTime(RealmList<Time> time) {
        this.time = time;
    }
}
