package com.aws.takitour.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.adapters.TourRVAdapter;
import com.aws.takitour.models.Tour;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class ToursFragment extends Fragment {
    private RecyclerView tourRV;
    private List<Tour> tourList;
    private List<String> tourCode;
    private TourRVAdapter adapter;
    private final Handler handler = new Handler();
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tours, container,false);
        tourRV = view.findViewById(R.id.rv_list_tours);
        firebaseAuth = FirebaseAuth.getInstance();

        new Thread(() -> {
            myDBReference.child("users")
                    .child(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()).replace(".", ","))
                    .child("tourList")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            tourCode = new ArrayList<>();
                            tourCode.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                tourCode.add(data.getValue(String.class));
                                Log.d("Attended tour", data.getValue(String.class));
                            }
                            Log.d("Number of tours", String.valueOf(tourCode.size()));
                            if(tourCode.isEmpty()){
                                ((TextView)view.findViewById(R.id.tv_attended)).setText("Bạn chưa tham gia tour nào");
                                return;
                            }
                            myDBReference.child("tours")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            tourList = new ArrayList<>();
                                            tourList.clear();
                                            for (DataSnapshot data : snapshot.getChildren()) {
                                                tourList.add(data.getValue(Tour.class));
                                            }
                                            List<Tour> attendedTour = new ArrayList<>();
                                            for(String tourId: tourCode){
                                                for(Tour tour: tourList){
                                                    if(tour.getId().equals(tourId)){
                                                        attendedTour.add(tour);
                                                    }
                                                }
                                            }
                                            handler.post(()->{
                                                adapter = new TourRVAdapter(getContext(), attendedTour);
                                                tourRV.setAdapter(adapter);
                                                tourRV.setLayoutManager(new LinearLayoutManager(getContext()));
                                                tourRV.setHasFixedSize(true);
                                            });
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase", "Cannot get tour list");
                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Cannot get tour list");
                        }
                    });
        }).start();

        return view;
    }
}
