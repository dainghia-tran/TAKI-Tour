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
import com.aws.takitour.views.ChangeInformation;
import com.aws.takitour.views.LoginActivity;
import com.aws.takitour.views.TourCreate;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class ProfileFragment extends Fragment {
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPhoneNumber;
    private TextView tvDescription;
    private TextView tvEditProfile;
    private TextView tvAddTour;
    private Button btnLogout;

    private List<String> pendingMessage;


    private final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfileImage = view.findViewById(R.id.img_image_profile);
        tvName = view.findViewById(R.id.tv_name_profile);
        tvEmail = view.findViewById(R.id.tv_email_profile);
        tvPhoneNumber = view.findViewById(R.id.tv_phone_number_profile);
        tvDescription = view.findViewById(R.id.tv_introduction_profile);
        tvEditProfile = view.findViewById(R.id.tv_information_profile);
        tvAddTour = view.findViewById(R.id.tv_add_tour_profile);
        btnLogout = view.findViewById(R.id.btn_logout);


        myDBReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User currentUser = new User();

                        currentUser.setName(snapshot.child("name").getValue(String.class));
                        currentUser.setDescription(snapshot.child("description").getValue(String.class));
                        currentUser.setEmail(snapshot.child("email").getValue(String.class));
                        currentUser.setTelephone(snapshot.child("telephone").getValue(String.class));
                        currentUser.setProfileImage(snapshot.child("profileImage").getValue(String.class));
                        if (snapshot.child("type").getValue(Integer.class) == null) {
                            currentUser.setType(0);
                        } else {
                            currentUser.setType(snapshot.child("type").getValue(Integer.class));
                        }
                        if (getActivity() != null) {
                            Glide.with(getContext()).load(currentUser.getProfileImage()).into(ivProfileImage);
                            tvName.setText(currentUser.getName());
                            tvEmail.setText(currentUser.getEmail());
                            tvPhoneNumber.setText(currentUser.getTelephone());
                            tvDescription.setText(currentUser.getDescription());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        tvEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ChangeInformation.class));
        });

        tvAddTour.setOnClickListener(v -> myDBReference.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                .child("type").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int userType = snapshot.getValue(Integer.class);
                        if (userType == 1 || userType == 3) {
                            if (getActivity() != null) {
                                startActivity(new Intent(getActivity(), TourCreate.class));
                            }
                        } else
                            handler.post(() -> {
                                Toast.makeText(getContext(), "Bạn không phải hướng dẫn viên", Toast.LENGTH_SHORT).show();
                            });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        return view;
    }
}
