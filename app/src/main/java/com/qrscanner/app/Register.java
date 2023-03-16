package com.qrscanner.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Register extends AppCompatActivity {
    EditText mConfirmpassword, mEmail, mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    ProgressBar mProgress;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Send the screen name to the analytics Controller
        AnalyticsController analytics = new AnalyticsController();
        analytics.SendScreenNameToAnalytics("Register Activity");

        mEmail = findViewById(R.id.Email);
        mProgress = findViewById(R.id.progressBar);
        mPassword = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.MainregisterBtn);
        mLoginBtn = findViewById(R.id.accountlogin);
        mConfirmpassword = findViewById(R.id.confirmpassword);
        firebaseAuth  = FirebaseAuth.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_email = mEmail.getText().toString().trim();
                String user_password = mPassword.getText().toString().trim();
                String user_confirm = mConfirmpassword.getText().toString().trim();
               //Input field logic
                if (TextUtils.isEmpty(user_email)){
                    mEmail.setError("Email address is required.");
                    return;
                }else if(user_password.isEmpty()){
                    mPassword.setError("Password required");
                    return;
                }else if(user_password.length()<6){
                    mPassword.setError("Password must be at least 6 characters");
                    return;
                }else if(!user_password.equals(user_confirm)){
                    mConfirmpassword.setText("");
                    mPassword.setError("Passwords must match");
                    return;
                }else {

                }

                firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgress.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()){
                            startActivity(new Intent(Register.this, MainActivity2.class));
                            finish();
                        }else {
                            mProgress.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Registration Failed "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
}