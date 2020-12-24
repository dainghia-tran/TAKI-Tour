package com.aws.takitour.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;
import com.aws.takitour.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtRePassword;
    private EditText edtFullName;
    private Button btnRegister;
    private Toolbar tbReturn;
    private TextView tvSignIn;

    private final Handler handler = new Handler();

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        linkElements();


        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });

        btnRegister.setClickable(true);

        firebaseAuth = FirebaseAuth.getInstance();

        tvSignIn.setOnClickListener(v->{
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String userEmail = edtEmail.getText().toString().trim();
            String userPassword = edtPassword.getText().toString().trim();
            String userRePassword = edtRePassword.getText().toString().trim();
            String userFullName = edtFullName.getText().toString().trim();

            final String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Snackbar.make(findViewById(R.id.register_activity), "Vui lòng nhập email hợp lệ.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!userPassword.matches(passwordPattern)) {
                Snackbar.make(findViewById(R.id.register_activity), "Mật khẩu tối thiểu 8 ký tự, phải bao gồm chữ hoa chữ thường và số.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(userPassword, userRePassword)) {
                Snackbar.make(findViewById(R.id.register_activity), "Xác nhận mật khẩu chưa chính xác.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(userFullName)) {
                Snackbar.make(findViewById(R.id.register_activity), "Hãy điền tên của bạn.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            btnRegister.setClickable(false);
            String defaultProfileImage = "https://firebasestorage.googleapis.com/v0/b/taki-tour.appspot.com/o/avatar.png?alt=media&token=591a850e-d704-4820-8b2d-a933fbb31b14";
            User newUser = new User(userFullName, 0, userEmail, "", defaultProfileImage, "");

            new Thread(() -> {
                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (!task.isSuccessful()) {
                                handler.post(() -> {
                                    Toast.makeText(getBaseContext(), "Xác thực thất bại.", Toast.LENGTH_SHORT).show();
                                    btnRegister.setClickable(true);
                                });
                            } else {
                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                myDBReference.child("users")
                                                        .child(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()).replace(".", ","))
                                                        .setValue(newUser);
                                                handler.post(()->{
                                                    Toast.makeText(getBaseContext(), "Email xác thực đã gửi tới email của bạn, vui lòng kiểm tra email và xác thực.", Toast.LENGTH_SHORT).show();
                                                });
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                intent.putExtra("LOGIN_CODE", 0);
                                                intent.putExtra("USER_EMAIL", userEmail);
                                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                                    FirebaseAuth.getInstance().signOut();
                                                }
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(getBaseContext(), "Không thể gửi email xác thực, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                                btnRegister.setClickable(true);
                                            }
                                        });
                            }
                        });
            }).start();
        });
    }

    private void linkElements() {
        edtFullName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtRePassword = findViewById((R.id.edt_re_password));
        btnRegister = findViewById(R.id.btn_signup);
        tbReturn = findViewById(R.id.tb_return);
        tvSignIn = findViewById(R.id.tv_sign_in);
    }
}