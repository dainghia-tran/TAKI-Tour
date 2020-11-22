package com.aws.takitour.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aws.takitour.R;
import com.aws.takitour.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText rePassword;
    private EditText fullName;
    private Button register;

    private final Handler handler = new Handler();

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        linkElements();

        register.setClickable(true);

        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            String userRePassword = rePassword.getText().toString().trim();
            String userFullName = fullName.getText().toString().trim();

            final String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Snackbar.make(findViewById(R.id.register_activity), "Please provide valid email address.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!userPassword.matches(passwordPattern)) {
                Snackbar.make(findViewById(R.id.register_activity), "Password minimum eight characters, at least one uppercase, lowercase letter and one number.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(userPassword, userRePassword)) {
                Snackbar.make(findViewById(R.id.register_activity), "Please make sure that re-enter correct password", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(userFullName)) {
                Snackbar.make(findViewById(R.id.register_activity), "Please provide your full name", Snackbar.LENGTH_SHORT).show();
                return;
            }
            register.setClickable(false);
            User newUser = new User("id", 0, userEmail, "");

            new Thread(() -> {
                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (!task.isSuccessful()) {
                                handler.post(() -> {
                                    Toast.makeText(getBaseContext(), "Xác thực thất bại.", Toast.LENGTH_SHORT).show();
                                    register.setClickable(true);
                                });
                            } else {
                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                myDBReference.child("users")
                                                        .child(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()))
                                                        .setValue(newUser);
                                                handler.post(()->{
                                                    Toast.makeText(getBaseContext(), "Email xác thực đã gửi tới email của bạn, vui lòng kiểm tra email và xác thực.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    intent.putExtra("LOGIN_CODE", 0);
                                                    intent.putExtra("USER_EMAIL", userEmail);
                                                    startActivity(intent);
                                                    finish();
                                                });
                                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                                    FirebaseAuth.getInstance().signOut();
                                                }
                                            } else {
                                                Toast.makeText(getBaseContext(), "Không thể gửi email xác thực, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                                register.setClickable(true);
                                            }
                                        });
                            }
                        });
            }).start();
        });
    }

    private void linkElements() {
        //TODO link elements
    }
}