package com.aws.takitour.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.aws.takitour.R;
import com.aws.takitour.models.Participant;
import com.aws.takitour.models.Tour;
import com.aws.takitour.models.UserReview;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.aws.takitour.views.LoginActivity.myDBReference;
import static com.aws.takitour.views.MainActivity.tourList;
import static com.aws.takitour.views.Maps.tourId;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private GoogleMap map;
    private final Handler handler = new Handler();

    public MapFragment() {
        getMapAsync(this);
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(getActivity()!=null) {
                @SuppressLint("UseCompatLoadingForDrawables") VectorDrawable vectorDrawable = (VectorDrawable) getActivity().getDrawable(id);

                int h = vectorDrawable.getIntrinsicHeight();
                int w = vectorDrawable.getIntrinsicWidth();

                vectorDrawable.setBounds(0, 0, w, h);

                Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm);
                vectorDrawable.draw(canvas);

                return BitmapDescriptorFactory.fromBitmap(bm);
            }else{
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            }
        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        new Thread(() -> {
            myDBReference.child("tours")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            tourList = new ArrayList<>();
                            tourList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Tour temp = new Tour();
                                temp.setTourGuide(data.child("tourGuide").getValue(String.class));
                                temp.setId(data.child("id").getValue(String.class));

                                List<Participant> participants = new ArrayList<>();
                                for (DataSnapshot dataParticipants : data.child("participants").getChildren()) {
                                    participants.add(dataParticipants.getValue(Participant.class));
                                }
                                temp.setParticipants(participants);

                                tourList.add(temp);
                            }
                            Tour currentTour = new Tour();
                            for (Tour tour : tourList) {
                                if (tour.getId().equals(tourId)) {
                                    currentTour = tour;
                                    break;
                                }
                            }
                            List<Participant> participantList = currentTour.getParticipants();
                            Tour finalCurrentTour = currentTour;
                            handler.post(() -> {
                                for (Participant participant : participantList) {
                                    if (participant.getEmail().equals(finalCurrentTour.getTourGuide())){
                                        map.addMarker(new MarkerOptions().icon(getBitmapDescriptor(R.drawable.ic_baseline_location_on_24)).position(new LatLng(Float.parseFloat(participant.getLatitude()), Float.parseFloat(participant.getLongitude()))).title(participant.getName()));
                                    } else {
                                        map.addMarker(new MarkerOptions().position(new LatLng(Float.parseFloat(participant.getLatitude()), Float.parseFloat(participant.getLongitude()))).title(participant.getName()));
                                    }
                                }
                                float zoomLevel = 16.0f; //This goes up to 21
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Float.parseFloat(participantList.get(0).getLatitude()), Float.parseFloat(participantList.get(0).getLongitude())), zoomLevel));
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Cannot get tour list");
                        }
                    });
        }).start();
    }
}
