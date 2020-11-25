package com.aws.takitour.views;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;
import com.aws.takitour.models.Tour;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.aws.takitour.utilities.RandomString;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import io.reactivex.annotations.NonNull;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class TourCreate extends AppCompatActivity {

    private EditText edtTourName;
    private EditText edtTourShortDescription;
    private EditText edtTourPrice;
    private EditText edtTourStartDate;
    private EditText edtTourEndDate;
    private ImageView ivTourBackground;
    private Button btnUploadImage;
    private Button btnCreateTour;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    private FirebaseAuth firebaseAuth;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private String imageLinkOfCureentProject;

    final Calendar myCalendar = Calendar.getInstance();
    public void getCurrentDate(EditText edtDate) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(TourCreate.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                edtDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        linkElements();
        List<String> coverImage = new ArrayList<>();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnUploadImage.setOnClickListener(v -> {
            chooseImage();
        });


        edtTourStartDate.setKeyListener(null);
        edtTourStartDate.setOnClickListener(v->{
            getCurrentDate(edtTourStartDate);
        });
        edtTourEndDate.setKeyListener(null);
        edtTourEndDate.setOnClickListener(v->{
            getCurrentDate(edtTourEndDate);
        });
        btnCreateTour.setOnClickListener(v -> {
            String tourName = edtTourName.getText().toString();
            String tourShortDescription = edtTourShortDescription.getText().toString();
            String tourPrice = edtTourPrice.getText().toString();
            String tourEndDate = edtTourEndDate.getText().toString();
            String tourStartDate = edtTourStartDate.getText().toString();

            RandomString generator = new RandomString(6, new SecureRandom());
            String tourId = generator.nextString();

            firebaseAuth = FirebaseAuth.getInstance();
            String tourGuideEmail = firebaseAuth.getCurrentUser().getEmail();
            String tourImageId = "";
            if (filePath != null) {
                tourImageId = uploadImage(tourId);
            }
            storageReference.child("images/tours/" + tourId + "/" + tourImageId+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("imageLink", uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("imageLinkNONONONO", "Dame Wrong");
                }
            });
            if (coverImage.isEmpty()) {
                coverImage.add("abc");
            }
            Tour newTour = new Tour(tourName, tourId, tourShortDescription, coverImage, tourGuideEmail, tourPrice, tourStartDate, tourEndDate);

            myDBReference.child("tours").child(tourId).setValue(newTour);
        });

    }

    private void getImageLink(String imageLink) {
        imageLinkOfCureentProject = imageLink;
    }

    private void chooseImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE_REQUEST);
    }

    private void linkElements() {
        edtTourName = findViewById(R.id.edt_tour_name);
        edtTourShortDescription = findViewById(R.id.edt_tour_short_description);
        edtTourPrice = findViewById(R.id.edt_tour_price);
        edtTourStartDate = findViewById(R.id.edt_tour_start_date);
        edtTourEndDate = findViewById(R.id.edt_tour_end_date);
        ivTourBackground = findViewById(R.id.iv_tour_background);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnCreateTour = findViewById(R.id.btn_create_tour);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();

            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                ivTourBackground.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private String uploadImage(String tourId) {
        String randomString = UUID.randomUUID().toString();
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("images/tours/" + tourId + "/" + randomString);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) {
                                Log.d("URL", "Waiting to complete");
                            }
                            Uri url = uri.getResult();
                            assert url != null;
                            String imageLink = url.toString();
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id.tour_create_activity), "Uploaded", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id.tour_create_activity), "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });

        }
        return randomString;
    }
}
