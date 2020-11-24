package com.aws.takitour.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;
import com.aws.takitour.models.Tour;
import com.google.firebase.auth.FirebaseAuth;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.aws.takitour.utilities.RandomString;
import static com.aws.takitour.views.LoginActivity.myDBReference;

public class TourCreate extends AppCompatActivity {

    private EditText edtTourName;
    private EditText edtTourShortDescription;
    private EditText edtTourPrice;
    private EditText edtTourStartDate;
    private EditText edtTourEndDate;

    private Button btnUploadImage;
    private Button btnCreateTour;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);
        linkElements();
        List<String> coverImage = new ArrayList<>();
        btnCreateTour.setOnClickListener(v -> {
            String tourName = edtTourName.getText().toString();
            String tourShortDescription = edtTourShortDescription.getText().toString();
            String tourPrice = edtTourPrice.getText().toString();
            String tourEndDate = edtTourEndDate.getText().toString();
            String tourStartDate = edtTourStartDate.getText().toString();

            RandomString generator = new RandomString(6, new SecureRandom());
            String tourId = generator.nextString();
            Log.e("somethingchanged", tourId);
            firebaseAuth = FirebaseAuth.getInstance();
            String tourGuideEmail = firebaseAuth.getCurrentUser().getEmail();
            if(coverImage.isEmpty()){
                coverImage.add("https://firebasestorage.googleapis.com/v0/b/taki-tour.appspot.com/o/default-tour-image.jpg?alt=media&token=e424bece-bc5b-46bd-b218-db439b3d430c");
            }
            Tour newTour = new Tour(tourName, tourId, tourShortDescription, coverImage, tourGuideEmail, tourPrice, tourStartDate, tourEndDate);

            myDBReference.child("tours").child(tourId).setValue(newTour);
        });

    }
    private void linkElements() {
        edtTourName = findViewById(R.id.edt_tour_name);
        edtTourShortDescription = findViewById(R.id.edt_tour_short_description);
        edtTourPrice = findViewById(R.id.edt_tour_price);
        edtTourStartDate = findViewById(R.id.edt_tour_start_date);
        edtTourEndDate = findViewById(R.id.edt_tour_end_date);

        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnCreateTour = findViewById(R.id.btn_create_tour);
    }
}
