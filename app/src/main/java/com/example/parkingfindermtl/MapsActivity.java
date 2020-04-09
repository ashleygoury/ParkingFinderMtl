package com.example.parkingfindermtl;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, CompoundButton.OnCheckedChangeListener, LocationListener {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private CameraPosition mCameraPosition;
    private ImageView msgBtn;
    private Button saveBtn;
    private Switch userTrack;
    private String apiKey;
    private boolean followme = false;
    private SharedPreferences sharedpreferences;
    boolean parked;
    SharedPreferences.Editor editor;
    Marker marker;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //   The geographical location where the device is currently located. That is, the last-known
//   location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    private final LatLng mDefaultLocation = new LatLng(45.551079, -73.553547);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final long MIN_TIME = 1000;
    private static final float MIN_DISTANCE = 0;
    private boolean mLocationPermissionGranted;
    private static final String TAG = MapsActivity.class.getSimpleName();

    //   Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);

//        Initialise fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

//        Initialise api key
        apiKey = getString(R.string.google_maps_key);

//        Initialise views
        msgBtn = findViewById(R.id.btn_msg);
        saveBtn = findViewById(R.id.btnSaveParking);
        userTrack = findViewById(R.id.switchFollow);

//        Initialise map
        mapFragment.getMapAsync(this);

        sharedpreferences = getSharedPreferences("com.example.parkingfindermtl.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);

//        Change color on click
        msgBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeBtnActif(v, event, R.drawable.highlightbutton, R.drawable.roundedbutton);
                return true;
            }
        });
        saveBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeBtnActif(v, event, R.drawable.highlightsemiroundedbutton, R.drawable.semiroundedbutton);
                return false;
            }
        });

//        Event handler on switch
        userTrack.setOnCheckedChangeListener(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

//        Initialise Google places api
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

//        Autocomplete textView
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng result = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(result, DEFAULT_ZOOM);
                mMap.animateCamera(loc);
            }

            @Override
            public void onError(Status status) {
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parked = sharedpreferences.getBoolean("parked", false);
                if (parked) {
                    double lat = getFloatAsDouble(sharedpreferences.getFloat("lat", 0));
                    double lng = getFloatAsDouble(sharedpreferences.getFloat("lng", 0));

                    LatLng result = new LatLng(lat, lng);
                    CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(result, DEFAULT_ZOOM);
                    mMap.animateCamera(loc);
                } else {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                            .title("You parked here!"));
                    editor.putBoolean("parked", true);
                    editor.putFloat("lat", (float) mLastKnownLocation.getLatitude());
                    editor.putFloat("lng", (float) mLastKnownLocation.getLongitude());
                    editor.commit();
                }
            }
        });
    }

    public static double getFloatAsDouble(float value) {
        return Double.valueOf(Float.valueOf(value).toString()).doubleValue();
    }

    //      Saves the state of the map when the activity is paused.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    public void changeBtnActif(View v, MotionEvent event, int highlight, int normal) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundResource(highlight);
                break;
            case MotionEvent.ACTION_UP:
                v.setBackgroundResource(normal);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        parked = sharedpreferences.getBoolean("parked", false);
        editor = sharedpreferences.edit();

        if (parked) {
            double lat = getFloatAsDouble(sharedpreferences.getFloat("lat", 0));
            double lng = getFloatAsDouble(sharedpreferences.getFloat("lng", 0));

            Toast.makeText(MapsActivity.this, "True in createOn", Toast.LENGTH_SHORT).show();

            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title("You parked here!"));
        } else {
            Toast.makeText(MapsActivity.this, "False in createOn", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        followme = isChecked;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (followme) {
            CameraPosition.Builder positionBuilder = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(17f)
                    .tilt(30);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(positionBuilder.build()));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
