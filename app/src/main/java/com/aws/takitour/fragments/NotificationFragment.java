package com.aws.takitour.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.adapters.NotificationRVAdapter;
import com.aws.takitour.adapters.TourRVAdapter;
import com.aws.takitour.models.Tour;

import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView notiRV;
    private List<Tour> notiList;
    private NotificationRVAdapter adapter;
    private final Handler handler = new Handler();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container,false);
        notiRV = view.findViewById(R.id.rv_list_notification);

        return view;
    }
}
