package com.aws.takitour.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.aws.takitour.R;
import com.aws.takitour.fragments.MapFragment;

public class Maps extends AppCompatActivity {

    private MapFragment mapFragment;
    public static String tourId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tourId = getIntent().getStringExtra("TOUR_ID");

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.fragment_map);
    }
}