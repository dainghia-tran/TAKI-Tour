package com.aws.takitour.fragments;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.aws.takitour.models.Participant;
import com.aws.takitour.models.Tour;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.stream.Collectors;

import static com.aws.takitour.views.MainActivity.tourList;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private String tourId;
    private GoogleMap map;
    private final Handler handler = new Handler();

    public MapFragment() {
        getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Tour currentTour = tourList.stream().filter(tour -> tour.getId().equals(tourId)).collect(Collectors.toList()).get(0);
        List<Participant> participantList = currentTour.getParticipants();
        for(Participant participant:participantList){
            map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(participant.getLatitude()), Double.parseDouble(participant.getLongitude())))).setTitle(participant.getName());
        }
    }
}
