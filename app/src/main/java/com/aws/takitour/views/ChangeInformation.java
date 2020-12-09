package com.aws.takitour.views;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aws.takitour.R;
import com.aws.takitour.models.Tour;
import com.aws.takitour.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class ChangeInformation extends AppCompatActivity {
    private Toolbar tbReturn;

    private ImageView ivProfileImage;

    private EditText edtName;
    private EditText edtDescription;
    private EditText edtTelephone;

    private Button btnChangePassword;
    private Button btnSaveChanges;

    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private final Handler handler = new Handler();

    private final int PICK_IMAGE_REQUEST = 22;

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

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



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

                        if (currentUser.getProfileImage() != null && getApplicationContext() != null) {
                            Glide.with(ChangeInformation.this).load(currentUser.getProfileImage()).into(ivProfileImage);
                        }
                        edtName.setText(currentUser.getName());
                        edtDescription.setText(currentUser.getDescription());
                        edtTelephone.setText(currentUser.getTelephone());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        ivProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, "Chọn hình ảnh"),
                    PICK_IMAGE_REQUEST);
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
            String newName = edtName.getText().toString();
            String newDescription = edtDescription.getText().toString();
            String newTelephone = edtTelephone.getText().toString();

            List<String> coverImage = new ArrayList<>();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Đang cập nhật...");
            progressDialog.show();

            DatabaseReference databaseReference = myDBReference.child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","));

            databaseReference
                    .child("description")
                    .setValue(newDescription);
            databaseReference
                    .child("name")
                    .setValue(newName);
            databaseReference
                    .child("telephone")
                    .setValue(newTelephone);
            if (filePath != null) {
                StorageReference ref = storageReference.child("images/users/" + FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",") + "/" + UUID.randomUUID().toString());
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uri.isComplete()) {
                                    Log.d("URL", "Waiting to complete");
                                }
                                Uri url = uri.getResult();
                                assert url != null;
                                String imageLink = url.toString();

                                if (coverImage.isEmpty()) {
                                    coverImage.add(imageLink);
                                }

                                databaseReference
                                        .child("profileImage")
                                        .setValue(imageLink);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@io.reactivex.annotations.NonNull Exception e) {
                                 }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Đã tải " + (int) progress + "%");
                            }
                        });
            }
            progressDialog.dismiss();
            Toast.makeText(ChangeInformation.this, "Thay đổi thông tin thành công.", Toast.LENGTH_SHORT).show();
            finish();
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();

            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                ivProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}