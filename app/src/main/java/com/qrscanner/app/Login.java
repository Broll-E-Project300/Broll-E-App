package com.qrscanner.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mRegisterBtn;
    FirebaseAuth firebaseAuth;
    ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginBtn);
        mRegisterBtn = findViewById(R.id.registerAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        mProgress = findViewById(R.id.progressBar);

        //check if user is already logged in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = mEmail.getText().toString().trim();
                String user_password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(user_email)){
                    mEmail.setError("Email address field is empty");
                    return;
                }
                if(TextUtils.isEmpty(user_password)){
                    mPassword.setError("Password field is empty");
                    return;
                }


            }
        });
    }
    private  void validate(String userEmail, String userPassword){
        mProgress.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), MapsActivity2.class));

                }
                else{
                    mProgress.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Error! "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}