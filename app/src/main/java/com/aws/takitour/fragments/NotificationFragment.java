package com.aws.takitour.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.adapters.NotificationRVAdapter;
import com.aws.takitour.adapters.TourRVAdapter;
import com.aws.takitour.models.Notification;
import com.aws.takitour.models.Tour;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class NotificationFragment extends Fragment {
    private final static String TAG = "NotificationFragment";
    private RecyclerView notiRV;
    private List<Notification> notificationList;
    List<String> notiCode;
    private NotificationRVAdapter adapter;
    private final Handler handler = new Handler();
    private FirebaseAuth firebaseAuth;
//    private Button btnCreateNoti;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        notiRV = view.findViewById(R.id.rv_list_notification);
//        btnCreateNoti = view.findViewById(R.id.btn_create_noti);
        firebaseAuth = FirebaseAuth.getInstance();
//        btnCreateNoti.setOnClickListener(v -> {
//            String tourId;
//            ValueEventListener userListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    String user = snapshot.getValue(String.class);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            };
//            myDBReference.child("users")
//                    .child(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()).replace(".", ","))
//                    .addValueEventListener(userListener)
//
//            Notification notiHandler = new Notification("hoanglong", "This is body", "This is title", )
//        });
        new Thread(() -> {
            myDBReference.child("users")
                    .child(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()).replace(".", ","))
                    .child("notifications").child("notiId")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            notificationList = new ArrayList<Notification>();
                            notificationList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Notification tempNoti = new Notification();
                                tempNoti.setTitle(data.child("title").getValue(String.class));
                                tempNoti.setBody(data.child("body").getValue(String.class));
                                tempNoti.setUser(data.child("sender").getValue(String.class));
                                notificationList.add(tempNoti);
                            }
                            List<Notification> sentNoti = new ArrayList<>();

                            Log.d(TAG, String.valueOf(notificationList));
                            handler.post(() -> {
                                adapter = new NotificationRVAdapter(getContext(), notificationList);
                                notiRV.setAdapter(adapter);
                                notiRV.setLayoutManager(new LinearLayoutManager(getContext()));
                                notiRV.setHasFixedSize(true);
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
