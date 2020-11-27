package com.aws.takitour.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aws.takitour.R;
import com.aws.takitour.models.Tour;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.aws.takitour.views.LoginActivity.myDBReference;
import static com.aws.takitour.views.MainActivity.tourList;

public class TourDashboard extends AppCompatActivity {
    private String tourId;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int PERMISSION_ID = 44;

    private Toolbar tbReturn;
    private TextView tvYourName;
    private ImageButton imgbtnLocate;
    private ImageButton imgbtnCall;
    private ImageButton imgbtnTimeline;
    private ImageButton imgbtnLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_dashboard);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        tourId = intent.getStringExtra("TOUR_ID");
        String tourName = getIntent().getStringExtra("TOUR_NAME");

        ((TextView)(findViewById(R.id.your_name))).setText(tourName);

        tbReturn = findViewById(R.id.tb_return_dashboard);
        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });

        imgbtnLocate = findViewById(R.id.imgbtn_locate);
        imgbtnCall = findViewById(R.id.imgbtn_call);
        imgbtnTimeline = findViewById(R.id.imgbtn_timeline);
        imgbtnLibrary = findViewById(R.id.imgbtn_library);

        imgbtnLocate.setOnClickListener(v->{
            Intent intentMaps = new Intent(TourDashboard.this, Maps.class);
            intentMaps.putExtra("TOUR_ID", tourId);
            startActivity(intentMaps);
        });

        imgbtnCall.setOnClickListener(v->{
            new Thread(()->{
                myDBReference.child("tours")
                        .child(tourId)
                        .child("tourGuide")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userPath = snapshot.getValue(String.class).replace(".", ",");
                                myDBReference.child("users")
                                        .child(userPath)
                                        .child("telephone")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String telephone = snapshot.getValue(String.class);
                                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telephone)));
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }).start();
        });

        imgbtnTimeline.setOnClickListener(v->{

        });

        imgbtnLibrary.setOnClickListener(v->{

        });


        //Check if tour is available and get location from user
        new Thread(()->{
            myDBReference.child("tours")
                    .child(tourId)
                    .child("available")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try{
                                if(snapshot.getValue(Boolean.class)){
                                    getLastLocation();
                                }
                            }catch (NullPointerException e){
                                Log.d("Get isAvailable", "failed");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }).start();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient
                        .getLastLocation()
                        .addOnCompleteListener(task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                new Thread(()->{
                                    myDBReference.child("tours")
                                            .child(tourId)
                                            .child("participants")
                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replace(".", ","))
                                            .child("latitude")
                                            .setValue(String.valueOf(location.getLatitude()));

                                    myDBReference.child("tours")
                                            .child(tourId)
                                            .child("participants")
                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replace(".", ","))
                                            .child("longitude")
                                            .setValue(String.valueOf(location.getLongitude()));
                                }).start();
                            }
                        });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            myDBReference.child("tours")
                    .child(tourId)
                    .child("participants")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replace(".", ","))
                    .child("latitude")
                    .setValue(String.valueOf(mLastLocation.getLatitude()));

            myDBReference.child("tours")
                    .child(tourId)
                    .child("participants")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replace(".", ","))
                    .child("longitude")
                    .setValue(String.valueOf(mLastLocation.getLongitude()));
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}