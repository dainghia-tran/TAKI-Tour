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
import com.aws.takitour.models.Participant;
import com.aws.takitour.models.Tour;
import com.aws.takitour.models.UserReview;
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
                                                for(DataSnapshot dataCoverImage: data.child("coverImage").getChildren()){
                                                    coverImage.add(dataCoverImage.getValue(String.class));
                                                }
                                                temp.setCoverImage(coverImage);

                                                List<Participant> participants = new ArrayList<>();
                                                for(DataSnapshot dataParticipants: data.child("participants").getChildren()){
                                                    participants.add(dataParticipants.getValue(Participant.class));
                                                }
                                                temp.setParticipants(participants);

                                                List<UserReview> userReviews = new ArrayList<>();
                                                for(DataSnapshot dataUserReviews: data.child("userReviewList").getChildren()){
                                                    userReviews.add(dataUserReviews.getValue(UserReview.class));
                                                }
                                                temp.setUserReviewList(userReviews);
                                                tourList.add(temp);
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
