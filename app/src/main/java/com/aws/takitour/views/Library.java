package com.aws.takitour.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toolbar;

import com.aws.takitour.R;
import com.aws.takitour.adapters.LibraryRVAdapter;
import com.aws.takitour.models.Picture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class Library extends AppCompatActivity {
    private RecyclerView pictureRV;
    private List<Picture> photoList;
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
                            photoList = new ArrayList<>();
                            photoList.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                String name = data.child("name").getValue(String.class);
                                for (DataSnapshot photos : data.child("photos").getChildren())
                                {
                                    List<String> userPhotos = new ArrayList<>();
                                    userPhotos.add(photos.getValue(String.class));

                                    Picture pic = new Picture();
                                    pic.setOwner(name);
                                    pic.setPic(userPhotos);

                                    photoList.add(pic);
                                }
                            }
                            handler.post(() -> {
                                adapter = new LibraryRVAdapter(Library.this, photoList);
                                pictureRV.setAdapter(adapter);

                                pictureRV.setLayoutManager(new GridLayoutManager(getBaseContext(),3));
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