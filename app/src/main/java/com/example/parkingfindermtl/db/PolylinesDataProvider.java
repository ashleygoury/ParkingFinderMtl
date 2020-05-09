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

        addPolyline(new Polyline(false, 45.530086
                        , -73.599516
                        , 45.530645
                        , -73.598608
                        , "Rue Marmier", 5825),
                new Day(1,
                        new Time(19, 0, 0, 0)));
    }

    private static void addPolyline(Polyline polyline, Day... days) {
        if (days.length > 0) {
            polyline.setDays((new RealmList<Day>()));
            polyline.getDays().addAll(Arrays.asList(days));
        }
        parkingList.add(polyline);
    }
}
