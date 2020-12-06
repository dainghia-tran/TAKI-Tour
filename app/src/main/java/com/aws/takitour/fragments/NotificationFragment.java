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
import com.aws.takitour.adapters.NotificationRVAdapter;
import com.aws.takitour.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class NotificationFragment extends Fragment {
    private final static String TAG ="NotificationFragment";
    private RecyclerView notificationRV;
    private List<Notification> notificationList;
    private NotificationRVAdapter adapter;
    private final Handler handler = new Handler();
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container,false);
        notificationRV = view.findViewById(R.id.rv_list_notification);
        firebaseAuth = FirebaseAuth.getInstance();

        new Thread(()->{
            myDBReference.child("users")
                    .child(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()).replace(".", ","))
                    .child("notifications")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            notificationList = new ArrayList<>();
                            notificationList.clear();
                            for(DataSnapshot data : snapshot.getChildren()){
                                notificationList.add(data.getValue(Notification.class));
                            }

                            Log.d(TAG, String.valueOf(notificationList));
                            handler.post(()->{
                                adapter = new NotificationRVAdapter(getContext(), notificationList);
                                notificationRV.setAdapter(adapter);
                                notificationRV.setLayoutManager(new LinearLayoutManager(getContext()));
                                notificationRV.setHasFixedSize(true);
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Cannot get notification list");
                        }
                    });
        }).start();
        return view;
    }
}
