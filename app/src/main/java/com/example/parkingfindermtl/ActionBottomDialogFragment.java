package com.example.parkingfindermtl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ActionBottomDialogFragment extends BottomSheetDialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.markerParking.remove();
                MapsActivity.editor.putBoolean("parked", false).commit();
            }
        });

        view.findViewById(R.id.btnDirection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double lat = MapsActivity.getFloatAsDouble(MapsActivity.sharedpreferences.getFloat("lat", 0));
                double lng = MapsActivity.getFloatAsDouble(MapsActivity.sharedpreferences.getFloat("lng", 0));

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }
}
