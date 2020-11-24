package com.aws.takitour.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.aws.takitour.R;
import com.aws.takitour.adapters.ViewPagerAdapter;
import com.aws.takitour.models.Tour;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class MainActivity extends AppCompatActivity {

    public static List<Tour> tourList;

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        setupViewPager();

        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId())
            {
                case R.id.item_search:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.item_map:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.item_notification:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.item_profile:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });

//        new Thread(() -> {
//            myDBReference.child("tours")
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            tourList = new ArrayList<>();
//                            tourList.clear();
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                tourList.add(data.getValue(Tour.class));
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.e("Firebase", "Cannot get tour list");
//                        }
//                    });
//        }).start();
//
//
//        startActivity(new Intent(MainActivity.this, Maps.class));
//        startActivity(new Intent(MainActivity.this, TourCreate.class));

    }

    private void setupViewPager() {
        ViewPagerAdapter viewPagerAdapter  = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter((viewPagerAdapter));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.item_search).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.item_map).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.item_notification).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.item_profile).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}