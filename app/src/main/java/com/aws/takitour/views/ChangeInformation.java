package com.aws.takitour.views;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;
import com.aws.takitour.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class ChangeInformation extends AppCompatActivity {
    private Toolbar tbReturn;
    private ImageView imageView;
    private EditText name;
    private EditText introduction;
    private EditText phoneNumber;
    private Button changePassword;
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        tbReturn = findViewById(R.id.tb_return_change_information);
        imageView = findViewById(R.id.img_image_change_information);
        name = findViewById(R.id.edt_name_change_information);
        introduction = findViewById(R.id.edt_introduction_change_information);
        phoneNumber = findViewById(R.id.edt_phone_change_information);
        changePassword = findViewById(R.id.btn_change_password_change_information);
        save = findViewById(R.id.btn_save_change_information);

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

                        Glide.with(Objects.requireNonNull(getBaseContext())).load(currentUser.getProfileImage()).into(imageView);
                        name.setText(currentUser.getName());
                        introduction.setText(currentUser.getDescription());
                        phoneNumber.setText(currentUser.getTelephone());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });

        changePassword.setOnClickListener(v -> {

        });

        save.setOnClickListener(v -> {

        });

    }
}