package com.aws.takitour.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.aws.takitour.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText edtUserEmail;
    private Button btnNext;
    private Toolbar toolbarReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtUserEmail = findViewById(R.id.edt_email_forgot_password);
        btnNext = findViewById(R.id.btn_next);
        toolbarReturn = findViewById(R.id.tb_return_forgot_password);

        toolbarReturn.setNavigationOnClickListener(v->{
            finish();
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        btnNext.setOnClickListener(v->{
            String userEmail = edtUserEmail.getText().toString().trim();
            if (TextUtils.isEmpty(userEmail)) {
                Snackbar.make(findViewById(R.id.forgot_password_activity), "Vui lòng nhập địa chỉ email hợp lệ.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            auth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.forgot_password_activity), "Chúng tôi đã gửi hướng dẫn đặt lại mật khẩu vào email của bạn.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.forgot_password_activity), "Có lỗi xảy ra, vui lòng thử lại.", Snackbar.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}