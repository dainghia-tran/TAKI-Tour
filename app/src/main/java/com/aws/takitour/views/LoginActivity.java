package com.aws.takitour.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aws.takitour.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp;
    private TextView forgotPassword;

    private EditText userEmail;
    private EditText userPassword;

    private Button signIn;
    private SignInButton signInGoogle;

    private FirebaseAuth firebaseAuth;

    public static FirebaseDatabase myDatabase = FirebaseDatabase.getInstance();
    public static DatabaseReference myDBReference = myDatabase.getReference();

    private GoogleSignInClient googleSignInClient;
    private final int RC_SIGN_IN = 1;
    private List<String> config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        int LOGIN_CODE = getIntent().getIntExtra("LOGIN_CODE", 1);
        String currentUserEmail = getIntent().getStringExtra("USER_EMAIL");
        if (firebaseAuth.getCurrentUser() != null && LOGIN_CODE != 0) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        linkElements();

        signIn.setClickable(true);
        if (currentUserEmail != null) {
            userEmail.setText(currentUserEmail);
        }
        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signUp.setPaintFlags(signUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("981414444913-gto09va4p3jfu5gt7s9teakrag62uulm.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signUp.setOnClickListener(v -> {
           startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        forgotPassword.setOnClickListener(v->{
            //TODO change activity to Forgot Password
        });

        signIn.setOnClickListener(v->{
            String email = userEmail.getText().toString();
            String password = userPassword.getText().toString();
            if (TextUtils.isEmpty(email)) {
                Snackbar.make(findViewById(R.id.login_activity), "Vui lòng nhập email.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Snackbar.make(findViewById(R.id.login_activity), "Vui lòng nhập mật khẩu.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            signIn.setClickable(false);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                userPassword.setError(getString(R.string.minimum_password));
                            } else {
                                signIn.setClickable(true);
                                Snackbar.make(findViewById(R.id.login_activity), getString(R.string.auth_failed), Snackbar.LENGTH_LONG).show();
                            }
                            signIn.setClickable(true);
                        } else {
                            if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                                Snackbar.make(findViewById(R.id.login_activity), "Vui lòng xác nhận email trước khi đăng nhập.", Snackbar.LENGTH_SHORT).show();
                                signIn.setClickable(true);
                                return;
                            }
                            Toast.makeText(getBaseContext(), "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    });
        });

        signInGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void linkElements(){
        signUp = findViewById(R.id.tv_sign_up);
        forgotPassword = findViewById(R.id.tv_forgot_password);

        userEmail = findViewById(R.id.edt_email);
        userPassword = findViewById(R.id.edt_password);

        signIn = findViewById(R.id.btn_signin);
        signInGoogle = findViewById(R.id.btn_signin_with_google);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Login with Google successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Snackbar.make(findViewById(R.id.login_activity), "Login with Google failed", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }
}