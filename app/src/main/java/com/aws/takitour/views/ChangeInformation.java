package com.aws.takitour.views;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;
import com.aws.takitour.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class ChangeInformation extends AppCompatActivity {
    private Toolbar tbReturn;

    private ImageView ivProfileImage;

    private EditText edtName;
    private EditText edtDescription;
    private EditText edtTelephone;

    private Button btnChangePassword;
    private Button btnSaveChanges;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        tbReturn = findViewById(R.id.tb_return_change_information);
        ivProfileImage = findViewById(R.id.iv_profile_image_change_information);
        edtName = findViewById(R.id.edt_name_change_information);
        edtDescription = findViewById(R.id.edt_description_change_information);
        edtTelephone = findViewById(R.id.edt_phone_change_information);
        btnChangePassword = findViewById(R.id.btn_change_password_change_information);
        btnSaveChanges = findViewById(R.id.btn_save_change_information);

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

                        if(currentUser.getType() == 2){
                            btnChangePassword.setVisibility(View.INVISIBLE);
                        }

                        Glide.with(ChangeInformation.this).load(currentUser.getProfileImage()).into(ivProfileImage);
                        edtName.setText(currentUser.getName());
                        edtDescription.setText(currentUser.getDescription());
                        edtTelephone.setText(currentUser.getTelephone());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        tbReturn.setNavigationOnClickListener(v -> {
            finish();
        });

        btnChangePassword.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_change_password);

            EditText currentPassword = dialog.findViewById(R.id.edt_current_password);
            EditText newPassword = dialog.findViewById(R.id.edt_new_password);
            EditText retypePassword = dialog.findViewById(R.id.edt_retype_password);
            Button confirmChange = dialog.findViewById(R.id.btn_confirm_change);

            dialog.show();

            confirmChange.setOnClickListener(view -> {
                String currentPass = currentPassword.getText().toString();
                String newPass = newPassword.getText().toString();
                String retypePass = retypePassword.getText().toString();

                if(currentPass.length() == 0){
                    Toast.makeText(this, "Bạn chưa nhập mật khẩu hiện tại.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPass.length() < 6) {
                    Toast.makeText(this, "Mật khẩu phải tối thiểu 6 ký tự.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!retypePass.equals(newPass)) {
                    Toast.makeText(this, "Vui lòng đảm bảo bạn nhập lại mật khẩu chính xác.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);

                new Thread(() -> {
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d("Change information", "Password updated");
                                            handler.post(() -> {
                                                Toast.makeText(this, "Đã đổi mật khẩu thành công.", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            });
                                        } else {
                                            Log.d("Change Information", "Error password not updated");
                                            handler.post(() -> {
                                                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            });
                                        }
                                    });
                                } else {
                                    Log.d("Change information", "Error auth failed");
                                    handler.post(() -> {
                                        Toast.makeText(this, "Mật khẩu hiện tại của bạn không chính xác, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                }).start();
            });
        });

        btnSaveChanges.setOnClickListener(v -> {

        });

    }
}