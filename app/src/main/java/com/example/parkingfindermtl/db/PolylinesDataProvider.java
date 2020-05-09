package com.example.parkingfindermtl.db;

import com.example.parkingfindermtl.models.Day;
import com.example.parkingfindermtl.models.Polyline;
import com.example.parkingfindermtl.models.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmList;

public class PolylinesDataProvider {
    public static List<Polyline> parkingList;

    static {
        parkingList = new ArrayList<>();

        addPolyline(new Polyline(true, 45.736301
                        , -73.824717
                        , 45.742785
                        , -73.826616
                        , "Rosemont", 413),
                new Day(1,
                        new Time(7, 8, 30, 30)));
    }

    private static void addPolyline(Polyline polyline, Day... days) {
        if (days.length > 0) {
            polyline.setDays((new RealmList<Day>()));
            polyline.getDays().addAll(Arrays.asList(days));
        }
        parkingList.add(polyline);
    }
}
