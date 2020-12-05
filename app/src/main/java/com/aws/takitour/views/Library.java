package com.aws.takitour.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import com.aws.takitour.R;
import com.aws.takitour.adapters.LibraryRVAdapter;
import com.aws.takitour.adapters.TourRVAdapter;
import com.aws.takitour.models.Participant;
import com.aws.takitour.models.Picture;
import com.aws.takitour.models.Tour;
import com.aws.takitour.models.UserReview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class Library extends AppCompatActivity {
    private RecyclerView pictureRV;
    private List<Picture> pictureList;
    private LibraryRVAdapter adapter;
    private final Handler handler = new Handler();

    private Toolbar tbReturn;
    public static String tourId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        tourId = getIntent().getStringExtra("TOUR_ID");

        tbReturn = findViewById(R.id.tb_return_library);
        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });

        pictureRV = findViewById(R.id.rv_list_picture);
        new Thread(() -> {
            myDBReference.child("tours")
                    .child(tourId)
                    .child("participants")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pictureList = new ArrayList<>();
                            pictureList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                String name = data.child("name").getValue(String.class);
                                for (DataSnapshot image : data.child("images").getChildren())
                                {
                                    List<String> picture = new ArrayList<>();
                                    picture.add(image.getValue(String.class));

                                    Picture pic = new Picture();
                                    pic.setOwner(name );
                                    pic.setPic(picture);

                                    pictureList.add(pic);
                                }
                            }
                            handler.post(() -> {
                                adapter = new LibraryRVAdapter(Library.this, pictureList);
                                pictureRV.setAdapter(adapter);

                                pictureRV.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
                                pictureRV.setHasFixedSize(true);
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                        });
        }).start();
    }
}