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

        Toolbar toolbarReturn = findViewById(R.id.tb_return_maps);
        toolbarReturn.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbarReturn.setNavigationOnClickListener(v -> {
            finish();
        });

        tourId = getIntent().getStringExtra("TOUR_ID");
//        Log.d("received tour id", tourId);
//        mapFragment = new MapFragment(tourId);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.fragment_map);
    }
}