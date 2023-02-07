package com.qrscanner.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {
    Button signOut;
    TextView signInMain, createAccountMain;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInMain = findViewById(R.id.MainloginBtn);
        createAccountMain = findViewById(R.id.MainregisterBtn);
        signOut = findViewById(R.id.signOutBtn);
        firebaseAuth = FirebaseAuth.getInstance();


        //Check if user is logged in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            signInMain.setVisibility(View.VISIBLE);
            createAccountMain.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
        }
        else {
            signInMain.setVisibility(View.GONE);
            createAccountMain.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        }
        signInMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
        createAccountMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Sign Out complete", Toast.LENGTH_SHORT).show();
                signOut.setVisibility(View.GONE);
                signInMain.setVisibility(View.VISIBLE);
                createAccountMain.setVisibility(View.VISIBLE);
            }
        });
    }
}