package com.aws.takitour.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aws.takitour.R;
import com.aws.takitour.models.Tour;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class TourEdit extends AppCompatActivity {
    private String tourId;
    private ImageButton imgBtnTourFinnish;

    private EditText edtTourName;
    private EditText edtTourDescription;
    private EditText edtTourPrice;
    private EditText edtTourStartDate;
    private EditText edtTourEndDate;

    private ImageView ivTourCoverImage;

    private Button btnTourCoverImageUpload;
    private Button btnConfirmChanges;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 22;

    private FirebaseAuth firebaseAuth;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private void getDate(EditText edtDate) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(TourEdit.this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            edtDate.setText(simpleDateFormat.format(calendar.getTime()));
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_edit);
        linkElements();
        Intent intent = getIntent();
        tourId = intent.getStringExtra("TOUR_ID");
        final String[] currentCoverImage = new String[1];

        Log.d("Edit tour", tourId);

        List<String> coverImage = new ArrayList<>();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        myDBReference.child("tours")
                .child(tourId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        String tourName = snapshot.child("name").getValue(String.class);
                        String description = snapshot.child("description").getValue(String.class);
                        String price = snapshot.child("price").getValue(String.class);
                        String startDate = snapshot.child("startDate").getValue(String.class);
                        String endDate = snapshot.child("endDate").getValue(String.class);
                        currentCoverImage[0] = snapshot.child("coverImage").child("0").getValue(String.class);

                        if (currentCoverImage[0] != null && getApplicationContext() != null) {
                            Glide.with(getApplicationContext()).load(currentCoverImage[0]).into(ivTourCoverImage);
                        }

                        edtTourName.setText(tourName);
                        edtTourDescription.setText(description);
                        edtTourPrice.setText(price);
                        edtTourStartDate.setText(startDate);
                        edtTourEndDate.setText(endDate);
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });

        imgBtnTourFinnish.setOnClickListener(v->{
            myDBReference.child("tours")
                    .child(tourId)
                    .child("available")
                    .setValue(false);
            finish();
        });

        btnTourCoverImageUpload.setOnClickListener(v->{
            chooseImage();
        });

        edtTourStartDate.setKeyListener(null);
        edtTourStartDate.setOnClickListener(v->{
            getDate(edtTourStartDate);
        });
        edtTourEndDate.setKeyListener(null);
        edtTourEndDate.setOnClickListener(v->{
            getDate(edtTourEndDate);
        });

        btnConfirmChanges.setOnClickListener(v -> {
            String tourName = edtTourName.getText().toString();
            String tourShortDescription = edtTourDescription.getText().toString();
            String tourPrice = edtTourPrice.getText().toString();
            String tourEndDate = edtTourEndDate.getText().toString();
            String tourStartDate = edtTourStartDate.getText().toString();

            firebaseAuth = FirebaseAuth.getInstance();

            String tourGuideEmail = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

            // Upload image to FireStore and add imageLink to ArrayList, then upload Tour to firebase RealTime
            if (filePath != null) {
                StorageReference oldImageRef = storage.getReferenceFromUrl(currentCoverImage[0]);
                oldImageRef.delete().addOnSuccessListener(aVoid -> {
                    // File deleted successfully
                    Log.d("TourEdit", "onSuccess: deleted old image");
                }).addOnFailureListener(exception -> {
                    // Uh-oh, an error occurred!
                    Log.d("TourEdit", "onFailure: did not delete file");
                });

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Đang tải lên...");
                progressDialog.show();
                StorageReference ref = storageReference.child("images/tours/" + tourId + "/" + UUID.randomUUID().toString());
                ref.putFile(filePath)
                        .addOnSuccessListener(taskSnapshot -> {
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) {
                                Log.d("URL", "Waiting to complete");
                            }
                            Uri url = uri.getResult();
                            assert url != null;
                            String imageLink = url.toString();

                            if (coverImage.isEmpty()) {
                                coverImage.add(imageLink);
                            }
                            DatabaseReference currentTourRef = myDBReference.child("tours").child(tourId);
                            currentTourRef.child("name").setValue(tourName);
                            currentTourRef.child("description").setValue(tourShortDescription);
                            currentTourRef.child("coverImage").setValue(coverImage);
                            currentTourRef.child("price").setValue(tourPrice);
                            currentTourRef.child("startDate").setValue(tourStartDate);
                            currentTourRef.child("endDate").setValue(tourEndDate);

                            progressDialog.dismiss();
                            Toast.makeText(TourEdit.this, "Đã cập nhật thay đổi.", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id.tour_create_activity), "Tải lên thất bại.", Snackbar.LENGTH_SHORT).show();
                        })
                        .addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Đã tải " + (int) progress + "%");
                        });
            }else{
                Tour newTour = new Tour(tourName, tourId, tourShortDescription, Arrays.asList(currentCoverImage), tourGuideEmail, tourPrice, tourStartDate, tourEndDate);
                myDBReference.child("tours").child(tourId).setValue(newTour);
                Toast.makeText(TourEdit.this, "Đã cập nhật thay đổi.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void linkElements(){
        imgBtnTourFinnish = findViewById(R.id.img_btn_tour_end);

        edtTourName = findViewById(R.id.edt_edit_tour_name);
        edtTourDescription = findViewById(R.id.edt_edit_tour_short_description);
        edtTourPrice = findViewById(R.id.edt_edit_tour_price);
        edtTourStartDate = findViewById(R.id.edt_edit_tour_start_date);
        edtTourEndDate = findViewById(R.id.edt_edit_tour_end_date);

        ivTourCoverImage = findViewById(R.id.iv_edit_tour_background);

        btnTourCoverImageUpload = findViewById(R.id.btn_edit_upload_image);
        btnConfirmChanges = findViewById(R.id.btn_edit_confirm_changes);
    }

    private void chooseImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Chọn hình ảnh"),
                PICK_IMAGE_REQUEST);
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
                ivTourCoverImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}