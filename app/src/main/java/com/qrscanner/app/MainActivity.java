package com.qrscanner.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button buttonScan, buttonMap;
    private Button buttonPlans, buttonCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = findViewById(R.id.buttonScan);
        buttonMap = findViewById(R.id.buttonMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity2.class));
            }
        });
        buttonScan.setOnClickListener(view -> {
            scanCode();
        });
        buttonPlans = (Button) findViewById(R.id.btnPlans);
        buttonPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlansActivity();
            }
        });

        buttonCard = (Button) findViewById(R.id.btnCreditCard);
        buttonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreditCard();
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