package com.aws.takitour.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;

public class Announcement extends AppCompatActivity {
    private static final String TAG = "CreateAnnoucement";
    private Toolbar tbReturn;
    private EditText edtAnnoucement;
    private Button btnSendNotification;
    public static String tourId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_annoucement);
        tbReturn = findViewById(R.id.tb_return_tour_dashboard_2);
        edtAnnoucement = findViewById(R.id.edt_announcement);
        btnSendNotification = findViewById(R.id.btn_announce);

        tourId = getIntent().getStringExtra("TOUR_ID");

        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });
        btnSendNotification.setOnClickListener(v ->{

        });
    }
}
