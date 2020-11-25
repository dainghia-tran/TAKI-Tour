package com.aws.takitour.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.adapters.TourRVAdapter;
import com.aws.takitour.models.Tour;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class ExploreFragment extends Fragment {
    private RecyclerView tourRV;
    private List<Tour> tourList;
    private TourRVAdapter adapter;
    private final Handler handler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container,false);
        tourRV = view.findViewById(R.id.rv_list_explore);

        new Thread(() -> {
            myDBReference.child("tours")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            tourList = new ArrayList<>();
                            tourList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                tourList.add((Tour) data.getValue());
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
}
