package com.aws.takitour.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aws.takitour.R;

public class ProfileFragment extends Fragment {
    private  ImageView imageView;
    private TextView name;
    private TextView email;
    private TextView phoneNumber;
    private TextView introduction;
    private TextView infoProfile;
    private TextView addTour;
    private Button logout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);

        imageView = view.findViewById(R.id.img_image_profile);
        name =view.findViewById(R.id.tv_name_profile);
        email = view.findViewById(R.id.tv_email_profile);
        phoneNumber = view.findViewById(R.id.tv_phone_number_profile);
        introduction = view.findViewById(R.id.tv_introduction_profile);
        infoProfile = view.findViewById(R.id.tv_information_profile);
        addTour = view.findViewById(R.id.tv_add_tour_profile);
        logout = view.findViewById(R.id.btn_logout);




        return view;
    }
}
