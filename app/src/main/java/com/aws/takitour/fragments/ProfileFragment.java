package com.aws.takitour.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aws.takitour.R;
import com.aws.takitour.models.User;
import com.aws.takitour.views.LoginActivity;
import com.aws.takitour.views.TourCreate;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class ProfileFragment extends Fragment {
    private  ImageView imageView;
    private TextView name;
    private TextView email;
    private TextView phoneNumber;
    private TextView introduction;
    private TextView infoProfile;
    private TextView addTour;
    private Button logout;
    
    private final Handler handler = new Handler();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);

        imageView = view.findViewById(R.id.img_image_profile);
        name = view.findViewById(R.id.tv_name_profile);
        email = view.findViewById(R.id.tv_email_profile);
        phoneNumber = view.findViewById(R.id.tv_phone_number_profile);
        introduction = view.findViewById(R.id.tv_introduction_profile);
        infoProfile = view.findViewById(R.id.tv_information_profile);
        addTour = view.findViewById(R.id.tv_add_tour_profile);
        logout = view.findViewById(R.id.btn_logout);


        myDBReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User currentUser = new User();
                        
                        currentUser.setName(snapshot.child("name").getValue(String.class));
                        currentUser.setDescription(snapshot.child("description").getValue(String.class));
                        currentUser.setEmail(snapshot.child("email").getValue(String.class));
                        currentUser.setTelephone(snapshot.child("telephone").getValue(String.class));
                        currentUser.setType(snapshot.child("type").getValue(Integer.class));
                        currentUser.setProfileImage(snapshot.child("profileImage").getValue(String.class));

                        Glide.with(getContext()).load(currentUser.getProfileImage()).into(imageView);
                        name.setText(currentUser.getName());
                        email.setText(currentUser.getEmail());
                        phoneNumber.setText(currentUser.getTelephone());
                        introduction.setText(currentUser.getDescription());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        infoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDBReference.child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                        .child("type").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue(Integer.class) == 0){
                            handler.post(()->{
                                Toast.makeText(getContext(), "Bạn không phải hướng dẫn viên", Toast.LENGTH_SHORT).show();
                            });
                        }
                        else 
                            startActivity(new Intent(getContext(), TourCreate.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }
}
