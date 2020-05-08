package com.example.parkingfindermtl.db;

import android.util.Log;

import com.example.parkingfindermtl.models.Polyline;

import io.realm.Realm;

public class ParkingDataSource {
    private static final String TAG = ParkingDataSource.class.getSimpleName();

    private Realm realm;

    public void open() {
        realm = Realm.getDefaultInstance();
        Log.d(TAG, "open: database opened");
    }

    public void close() {
        realm.close();
        Log.d(TAG, "open: database closed");
    }

    public void createPolyline(final Polyline polyline) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insert(polyline);
            }
        });

        Log.d(TAG, "createPolyline: the id: " + polyline.getId());
    }
}
