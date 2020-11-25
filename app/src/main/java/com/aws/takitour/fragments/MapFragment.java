package com.aws.takitour.fragments;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.aws.takitour.models.Participant;
import com.aws.takitour.models.Tour;
import com.aws.takitour.models.UserReview;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

//    public MapFragment(String tourId) {
//        this.tourId = tourId;
//        getMapAsync(this);
//    }

    public MapFragment() {
        getMapAsync(this);
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
                                temp.setAvailable(data.child("available").getValue(Boolean.class));
                                temp.setName(data.child("name").getValue(String.class));
                                temp.setId(data.child("id").getValue(String.class));
                                temp.setDescription(data.child("description").getValue(String.class));
                                temp.setTourGuide(data.child("tourGuide").getValue(String.class));
                                temp.setStartDate(data.child("startDate").getValue(String.class));
                                temp.setHost(data.child("host").getValue(String.class));
                                temp.setPrice(data.child("price").getValue(String.class));
                                temp.setEndDate(data.child("endDate").getValue(String.class));
                                temp.setOverallRating(data.child("overallRating").getValue(Float.class));

                                List<String> coverImage = new ArrayList<>();
                                for (DataSnapshot dataCoverImage : data.child("coverImage").getChildren()) {
                                    coverImage.add(dataCoverImage.getValue(String.class));
                                }
                                temp.setCoverImage(coverImage);

                                List<Participant> participants = new ArrayList<>();
                                for (DataSnapshot dataParticipants : data.child("participants").getChildren()) {
                                    participants.add(dataParticipants.getValue(Participant.class));
                                }
                                temp.setParticipants(participants);

                                List<UserReview> userReviews = new ArrayList<>();
                                for (DataSnapshot dataUserReviews : data.child("userReviewList").getChildren()) {
                                    userReviews.add(dataUserReviews.getValue(UserReview.class));
                                }
                                temp.setUserReviewList(userReviews);
                                tourList.add(temp);
                            }
                            Tour currentTour = null;
                            for (Tour tour : tourList) {
                                if (tour.getId().equals(tourId)) {
                                    currentTour = tour;
                                    break;
                                }
                            }
                            List<Participant> participantList = currentTour.getParticipants();
                            handler.post(() -> {
                                for (Participant participant : participantList) {
                                    map.addMarker(new MarkerOptions().position(new LatLng(Float.parseFloat(participant.getLatitude()), Float.parseFloat(participant.getLongitude()))).title(participant.getName()));
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
