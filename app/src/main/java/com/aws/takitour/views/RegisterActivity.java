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

    private EditText email;
    private EditText password;
    private EditText rePassword;
    private EditText fullName;
    private Button register;
    private Toolbar toolbarReturn;
    private TextView signIn;

    private final Handler handler = new Handler();

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        linkElements();


        toolbarReturn.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbarReturn.setNavigationOnClickListener(v -> {
            finish();
        });

        register.setClickable(true);

        firebaseAuth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(v->{
            finish();
        });

        register.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            String userRePassword = rePassword.getText().toString().trim();
            String userFullName = fullName.getText().toString().trim();

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
            register.setClickable(false);
            String defaultProfileImage = "https://firebasestorage.googleapis.com/v0/b/taki-tour.appspot.com/o/avatar.png?alt=media&token=591a850e-d704-4820-8b2d-a933fbb31b14";
            User newUser = new User(userFullName, 0, userEmail, "", defaultProfileImage, "");

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
                                                register.setClickable(true);
                                            }
                                        });
                            }
                        });
            }).start();
        });
    }

    private void linkElements() {
        fullName = findViewById(R.id.edt_name);
        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_password);
        rePassword = findViewById((R.id.edt_re_password));
        register = findViewById(R.id.btn_signup);
        toolbarReturn = findViewById(R.id.tb_return);
        signIn = findViewById(R.id.tv_sign_in);
    }
}