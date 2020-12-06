package com.aws.takitour.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;
import com.aws.takitour.models.Notification;
import com.aws.takitour.models.Participant;
import com.aws.takitour.notifications.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class CreateNotification extends AppCompatActivity {
    private static final String TAG = "CreateNotification";
    private Toolbar tbReturn;
    private EditText edtTitle;
    private EditText edtBody;
    private Button btnSendNotification;
    public static String tourId;
    private List<Participant> participantList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_noti_data);
        tbReturn = findViewById(R.id.tb_return_tour_dashboard);
        edtTitle = findViewById(R.id.edt_noti_title);
        edtBody = findViewById(R.id.edt_noti_body);
        btnSendNotification = findViewById(R.id.btn_send_noti);
        tourId = getIntent().getStringExtra("TOUR_ID");

        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });
        btnSendNotification.setOnClickListener(v->{
            String userTitle = edtTitle.getText().toString().trim();
            String userBody = edtBody.getText().toString().trim();
            Log.d(TAG, "Send button is clicked");
            new Thread(()->{
                ValueEventListener participantListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "LOG 1");
                        List<Participant> participants = new ArrayList<>();
                        // Save participants to List
                        for (DataSnapshot data : snapshot.getChildren()) {
                            participants.add(data.getValue(Participant.class));
                        }
                        Boolean sendingCompleted = false;
                        if (participants.size() != 0) {
                            for (Participant participant : participants) {
                                String participantEmail = participant.getEmail();
                                myDBReference.child("users").child(participantEmail.replace(".", ",")).child("token").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String token = snapshot.getValue(String.class);
                                        Log.d(TAG, token);
                                        Notification notificationHandler = new Notification("TourGuide", userTitle, userBody);
                                        notificationHandler.sendNotification(token);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                            sendingCompleted = true;
                            if(sendingCompleted){
                                Intent intentTourDashboard = new Intent(CreateNotification.this, TourDashboard.class);
                                intentTourDashboard.putExtra("TOUR_ID", tourId);
                                startActivity(intentTourDashboard);
                                finish();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                myDBReference.child("tours").child(tourId).child("participants").addValueEventListener(participantListener);
            }).start();
        });
    }
}
