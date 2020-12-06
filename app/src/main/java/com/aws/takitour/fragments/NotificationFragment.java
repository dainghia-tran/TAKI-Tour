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
import com.aws.takitour.notifications.Data;
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
    private List<Data> notificationList;
    private NotificationRVAdapter adapter;
    private final Handler handler = new Handler();
    private FirebaseAuth firebaseAuth;
//    private Button btnCreateNoti;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        btnCreateNoti = view.findViewById(R.id.btn_create_noti);

        View view = inflater.inflate(R.layout.fragment_notification, container,false);
        notificationRV = view.findViewById(R.id.rv_list_notification);
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
                    .child("notifications")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            notificationList = new ArrayList<>();
                            notificationList.clear();

                            for(DataSnapshot data : snapshot.getChildren()){
                                notificationList.add(data.getValue(Data.class));                            }

                            Log.d(TAG, String.valueOf(notificationList));
                            handler.post(() -> {
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
