package com.qrscanner.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mRegisterBtn;
    FirebaseAuth firebaseAuth;
    ProgressBar mProgress;
    ImageView google_img;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Send the screen name to the analytics Controller
        AnalyticsController analytics = new AnalyticsController();
        analytics.SendScreenNameToAnalytics("Login Activity");

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.MainloginBtn);
        mRegisterBtn = findViewById(R.id.registerAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        mProgress = findViewById(R.id.progressBar);
        //google_img=findViewById(R.id.google);

        //check if user is already logged in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        /*gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        gsc=GoogleSignIn.getClient(this,gso);
*/
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
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

                validate(mEmail.getText().toString().trim(), mPassword.getText().toString().trim());


            }
        });
       /* google_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });*/
    }

    private  void validate(String userEmail, String userPassword){
        mProgress.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                    finish();

                }
                else{
                    mProgress.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Error! "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void SignIn(){
        Intent intent=gsc.getSignInIntent();
        startActivityForResult(intent,100);
    }

     /* @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);

            }catch(Exception e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

            }
        }
    }*/

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                        finish();
                    }
                });
    }
}