package com.aws.takitour.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;
import com.aws.takitour.models.Notification;
import com.aws.takitour.models.Participant;
import com.aws.takitour.models.Tour;
import com.aws.takitour.utilities.RandomString;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.annotations.NonNull;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class CreateSOS extends AppCompatActivity {
    private static final String TAG = "CreateSOS";
    private Toolbar tbReturn;
    private EditText edtSOSBody;
    private Button btnUploadImage;
    private ImageView ivSOSImage;
    private Button btnSendSOS;
    public static String tourId;
    private List<Participant> participantList;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sos_notification);
        tourId = getIntent().getStringExtra("TOUR_ID");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Link UI with code
        linkElements();
        // Exit current intent
        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });
        // Upload image SOS from user's device
        btnUploadImage.setOnClickListener(v -> {
            chooseImage();
        });
        // Submit the form
        btnSendSOS.setOnClickListener(v -> {
            String sosBody = edtSOSBody.getText().toString().trim();

            // Upload image to FireStore and add imageLink to ArrayList, then upload Tour to firebase RealTime
            if (filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Đang tải lên...");
                progressDialog.show();
                StorageReference ref = storageReference.child("images/sos/" + tourId + "/" + UUID.randomUUID().toString());
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

                                // Send SOS to all user in tour
                                sendSOSMessage(sosBody, imageLink);

                                // Exit intent
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Snackbar.make(findViewById(R.id.tour_create_activity), "Tải lên thất bại.", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Đã tải " + (int) progress + "%");
                            }
                        });
            }
            else{
                sendSOSMessage(sosBody, "");
                // Exit intent
                finish();
            }
        });

    }

    private void sendSOSMessage(String sosBody, String imageUrl){
        new Thread(()->{
            ValueEventListener participantListener = new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    List<Participant> participants = new ArrayList<>();
                    // Save participants to List
                    for (DataSnapshot data : snapshot.getChildren()) {
                        participants.add(data.getValue(Participant.class));
                    }
                    Boolean sentSOS = false;
                    if (participants.size() != 0) {
                        for (Participant participant : participants) {
                            String participantEmail = participant.getEmail();
                            if (!participantEmail.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {
                                myDBReference.child("users").child(participantEmail.replace(".", ",")).child("token").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                                        String token = snapshot.getValue(String.class);
                                        Notification notificationHandler = new Notification(tourId, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),"TIN KHẨN CẤP!", sosBody, imageUrl);
                                        notificationHandler.sendNotification(token);
                                    }

                                    @Override
                                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                        sentSOS = true;
                        if (sentSOS) {
                            Toast.makeText(CreateSOS.this, "Thông báo khẩn của bạn đã được gửi", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                }
            };
            myDBReference.child("tours").child(tourId).child("participants").addValueEventListener(participantListener);
        }).start();
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
    private void linkElements() {
        tbReturn = findViewById(R.id.tb_return_tour_dashboard);
        edtSOSBody = findViewById(R.id.edt_sos_body);
        ivSOSImage = findViewById(R.id.iv_sos_image);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnSendSOS = findViewById(R.id.btn_send_sos);
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
                ivSOSImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}
