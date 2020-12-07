package com.aws.takitour.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

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
import com.aws.takitour.views.TourDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static com.aws.takitour.views.LoginActivity.myDBReference;

public class ExploreFragment extends Fragment {
    private EditText edtCode;

    private RecyclerView tourRV;
    private List<Tour> tourList;
    private TourRVAdapter adapter;
    private final Handler handler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container,false);
        tourRV = view.findViewById(R.id.rv_list_explore);
        edtCode = view.findViewById(R.id.edt_code);
        edtCode.setOnEditorActionListener((v, actionId, event)->{
            String userInputCode = edtCode.getText().toString();
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                myDBReference.child("tours")
                        .child(userInputCode)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Participant> participants = new ArrayList<>();
                                for (DataSnapshot data : snapshot.child("participants").getChildren()) {
                                    participants.add(data.getValue(Participant.class));
                                }
                                String tourName = snapshot.child("name").getValue(String.class);
                                boolean attended = false;
                                String tourGuideEmail = snapshot.child("tourGuide").getValue(String.class);
                                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                if(tourGuideEmail == null){
                                    handler.post(()->{
                                       Toast.makeText(getActivity(), "Không tồn tại mã tour này.", Toast.LENGTH_SHORT).show();
                                    });
                                    return;
                                }
                                if (tourGuideEmail.equals(currentUserEmail)) {
                                    handler.post(() -> {
                                        Intent intent = new Intent(getActivity(), TourDashboard.class);
                                        intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.putExtra("TOUR_ID", userInputCode);
                                        intent.putExtra("TOUR_NAME", tourName);
                                        Objects.requireNonNull(getActivity()).startActivity(intent);
                                    });
                                    return;
                                }

                                if (participants.size() != 0) {
                                    for (Participant participant : participants) {
                                        String participantEmail = participant.getEmail();
                                        if (participantEmail != null && participantEmail.equals(currentUserEmail)) {
                                            handler.post(() -> {
                                                Intent intent = new Intent(getActivity(), TourDashboard.class);
                                                intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.putExtra("TOUR_ID", userInputCode);
                                                intent.putExtra("TOUR_NAME", tourName);
                                                Objects.requireNonNull(getActivity()).startActivity(intent);
                                            });
                                            return;
                                        }
                                    }
                                }
                                Intent tourDashboard = new Intent(getActivity(), TourDashboard.class);
                                tourDashboard.putExtra("TOUR_ID", userInputCode);
                                tourDashboard.putExtra("TOUR_NAME", tourName);
                                tourDashboard.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                tourDashboard.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                handler.post(() -> {
                                    Objects.requireNonNull(getActivity()).startActivity(tourDashboard);
                                    updateUserTourList(userInputCode);
                                });                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                return true;
            }
            return false;
        });

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
                            handler.post(()->{
                                adapter = new TourRVAdapter(getContext(), tourList);
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
        }).start();

        return view;
    }

    public void updateUserTourList(String tourId) {

        new Thread(() -> {
            myDBReference.child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String newParticipantToken = snapshot.child("token").getValue(String.class);
                            Participant newParticipant = new Participant(FirebaseAuth.getInstance().getCurrentUser().getEmail(), snapshot.child("name").getValue(String.class));
                            myDBReference.child("tours")
                                    .child(tourId)
                                    .child("participants")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                                    .setValue(newParticipant);
                            myDBReference.child("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                                    .child("tourList")
                                    .child(tourId)
                                    .setValue(tourId);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }).start();
    }
}
