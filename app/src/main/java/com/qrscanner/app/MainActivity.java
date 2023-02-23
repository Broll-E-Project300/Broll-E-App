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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
//Add for Google Firebase Analytics


public class MainActivity extends AppCompatActivity {

    Button buttonScan, buttonMap, signOut;
    TextView signInMain, createAccountMain;
    FirebaseAuth firebaseAuth;
    private Button buttonPlans, buttonCard;

    //Create a new instance of the accounts controller
    AnalyticsController analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize analytics in oncreate.
        analytics = new AnalyticsController();

        //Send the screen name to the analytics Controller
        analytics.SendScreenNameToAnalytics("Main Activity");

        signInMain = findViewById(R.id.MainloginBtn);
        createAccountMain = findViewById(R.id.MainregisterBtn);
        signOut = findViewById(R.id.signOutBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        buttonScan = findViewById(R.id.buttonScan);
        buttonMap = findViewById(R.id.buttonMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send the button name to the analytics
                analytics.SendButtonClick("Maps button");
                startActivity(new Intent(getApplicationContext(), MapsActivity2.class));
            }
        });
        buttonScan.setOnClickListener(view -> {
            scanCode();
        });
        buttonPlans = (Button) findViewById(R.id.btnPlans);
        buttonPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Send the button name to the analytics
                analytics.SendButtonClick("Plans button");
                openPlansActivity();
            }
        });

        buttonCard = (Button) findViewById(R.id.btnCreditCard);
        buttonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Send the button name to the analytics
                analytics.SendButtonClick("CreditCard button");
                openCreditCard();
            }
        });

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
                //Send the button name to the analytics
                analytics.SendButtonClick("Sign in button");
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
        createAccountMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send the button name to the analytics
                analytics.SendButtonClick("Register button");
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Send the button name to the analytics
                analytics.SendButtonClick("Sign out button");
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Sign Out complete", Toast.LENGTH_SHORT).show();
                signOut.setVisibility(View.GONE);
                signInMain.setVisibility(View.VISIBLE);
                createAccountMain.setVisibility(View.VISIBLE);
            }
        });
    }

    private void scanCode()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->{
        if(result.getContents() != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            }).show();
        }
    });

    public void openPlansActivity(){
        Intent intent = new Intent(this,Plans.class);
        startActivity(intent);
    }

    public void openCreditCard(){
        Intent intent = new Intent(this, CreditCard.class);
        startActivity(intent);
    }
}