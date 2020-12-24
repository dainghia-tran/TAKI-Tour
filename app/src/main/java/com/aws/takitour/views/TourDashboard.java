package com.aws.takitour.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.aws.takitour.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class TourDashboard extends AppCompatActivity {
    private String tourId;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int PERMISSION_ID = 44;

    private static final int PERMISSION_TO_SELECT_IMAGE_FROM_GALLERY = 100;
    private static final int PICK_IMAGE_MULTIPLE = 200;


    private Toolbar tbReturn;
    private Toolbar tbEditTour;

    private ImageButton imgBtnLocate;
    private ImageButton imgBtnCall;
    private ImageButton ingBtnAnnouncement;
    private ImageButton imgBtnLibrary;
    private ImageButton imgBtnAddPhotos;
    private ImageButton imgBtnCreateNoti;

    private StorageReference storageReference;

    private final Handler handler = new Handler();

    public void linkElements() {
        imgBtnLocate = findViewById(R.id.imgbtn_locate);
        imgBtnCall = findViewById(R.id.imgbtn_call);
//        ingBtnAnnouncement = findViewById(R.id.imgbtn_annoucement);
        imgBtnLibrary = findViewById(R.id.imgbtn_library);
        imgBtnAddPhotos = findViewById(R.id.imgbtn_add_photos);
        imgBtnCreateNoti = findViewById(R.id.imgbtn_create_noti);

        tbReturn = findViewById(R.id.tb_return_dashboard);
        tbEditTour = findViewById(R.id.tb_edit_tour);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_dashboard);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        tourId = intent.getStringExtra("TOUR_ID");
        String tourName = getIntent().getStringExtra("TOUR_NAME");

        linkElements();

        ((TextView) (findViewById(R.id.tour_name))).setText(tourId + " - " + tourName);

        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });

        tbEditTour.setNavigationOnClickListener(v -> {
            myDBReference.child("tours")
                    .child(tourId)
                    .child("tourGuide")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tourGuideEmail = snapshot.getValue(String.class);
                            assert tourGuideEmail != null;
                            if (tourGuideEmail.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {
                                Intent intentEdit = new Intent(TourDashboard.this, TourEdit.class);
                                intentEdit.putExtra("TOUR_ID", tourId);
                                startActivity(intentEdit);
                            } else {
                                handler.post(() -> {
                                    Toast.makeText(TourDashboard.this, "Bạn không có quyền chỉnh sửa tour này.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });


        imgBtnLocate.setOnClickListener(v -> {
            Intent intentMaps = new Intent(TourDashboard.this, Maps.class);
            intentMaps.putExtra("TOUR_ID", tourId);
            startActivity(intentMaps);
        });

        imgBtnCall.setOnClickListener(v -> {
            new Thread(() -> {
                myDBReference.child("tours")
                        .child(tourId)
                        .child("tourGuide")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userPath = Objects.requireNonNull(snapshot.getValue(String.class)).replace(".", ",");
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

//        ingBtnAnnouncement.setOnClickListener(v -> {
//            Intent intentAnnouncement = new Intent(TourDashboard.this, Announcement.class);
//            intentAnnouncement.putExtra("TOUR_ID", tourId);
//            startActivity(intentAnnouncement);
//        });

        imgBtnLibrary.setOnClickListener(v -> {
            Intent intentLibrary = new Intent(TourDashboard.this, Library.class);
            intentLibrary.putExtra("TOUR_ID", tourId);
            startActivity(intentLibrary);
        });

        imgBtnAddPhotos.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_TO_SELECT_IMAGE_FROM_GALLERY);
            } else {
                Intent choosePhotosIntent = new Intent();
                choosePhotosIntent.setType("image/*");
                choosePhotosIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                choosePhotosIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(choosePhotosIntent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        imgBtnCreateNoti.setOnClickListener(v -> {
            Intent intentCreateNoti = new Intent(TourDashboard.this, CreateNotification.class);
            intentCreateNoti.putExtra("TOUR_ID", tourId);
            startActivity(intentCreateNoti);
        });

        //Check if tour is available and get location from user
        new Thread(() -> {
            myDBReference.child("tours")
                    .child(tourId)
                    .child("available")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                if (snapshot.getValue(Boolean.class)) {
                                    getLastLocation();
                                } else {
                                    handler.post(() -> {
                                        Toast.makeText(TourDashboard.this, "Tour đã kết thúc.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } catch (NullPointerException e) {
                                Log.d("Get isAvailable", "failed");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }).start();
        //Check if tour is out of date
        new Thread(() -> {
            myDBReference.child("tours")
                    .child(tourId)
                    .child("endDate")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Calendar today = Calendar.getInstance();
                    Calendar endDate = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy");
                    try {
                        endDate.setTime(format.parse(snapshot.getValue(String.class)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    endDate.add(Calendar.DATE, 1);
                    if(today.after(endDate)){
                        myDBReference.child("tours")
                                .child(tourId)
                                .child("available")
                                .setValue(false);
                    }else{
                        Log.d("Tour", "is not expired");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                List<Uri> mArrayUri = new ArrayList<>();

                if (data.getData() != null) {         //on Single image selected
                    mArrayUri.add(data.getData());
                } else {                              //on multiple image selected
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            mArrayUri.add(item.getUri());
                        }
                        Log.v("TourDashboard", "Selected Images" + mArrayUri.size());
                    }
                }
                for (int i = 0; i < mArrayUri.size(); i++) {
                    StorageReference ref = storageReference.child("images/tours/" + tourId + "/userPhotos/" + UUID.randomUUID().toString());
                    ref.putFile(mArrayUri.get(i))
                            .addOnSuccessListener(taskSnapshot -> {
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uri.isComplete()) {
                                    Log.d("URL", "Waiting to complete");
                                }
                                Uri url = uri.getResult();
                                assert url != null;
                                String imageLink = url.toString();

                                myDBReference.child("tours").child(tourId)
                                        .child("participants")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                                        .child("photos")
                                        .child(UUID.randomUUID().toString())
                                        .setValue(imageLink);
                            })
                            .addOnFailureListener(e -> {
                                Snackbar.make(findViewById(R.id.tour_dashboard_activity), "Tải lên thất bại.", Snackbar.LENGTH_SHORT).show();
                            });
                }
                Snackbar.make(findViewById(R.id.tour_dashboard_activity), "Hình ảnh đã được tải lên.", Snackbar.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, "Bạn chưa chọn hình.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Đã có lỗi xảy ra.", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                                new Thread(() -> {
                                    DatabaseReference currentParticipantRef = myDBReference.child("tours")
                                            .child(tourId)
                                            .child("participants")
                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).replace(".", ","));

                                    currentParticipantRef.child("latitude").setValue(String.valueOf(location.getLatitude()));
                                    currentParticipantRef.child("longitude").setValue(String.valueOf(location.getLongitude()));
                                }).start();
                            }
                        });
            } else {
                Toast.makeText(this, "Vui lòng bật định vị...", Toast.LENGTH_LONG).show();

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