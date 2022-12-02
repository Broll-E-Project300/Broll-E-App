package com.qrscanner.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class InformationActivity extends AppCompatActivity {
    TextView tvLocation, tvInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        tvLocation = findViewById(R.id.tvLocation);
        tvInformation = findViewById(R.id.tvInformation);
        //   tvUmbrellas = findViewById(R.id.tvUmbrellas);
        //   tvUmbAvailable = findViewById(R.id.tvUmbAvailable);

        String LocationName = getIntent().getStringExtra("Location");
        tvLocation.setText(LocationName);

        String LocationInfo = getIntent().getStringExtra("Information");
        tvInformation.setText(LocationInfo);

        //   String Umbrellas = getIntent().getStringExtra("Umbrellas");
        //  tvUmbrellas.setText(Umbrellas);

        //  String Spaces = getIntent().getStringExtra("Spaces");
        //  tvUmbAvailable.setText(Spaces);
    }

    public void onClickMap(View view) {
        Intent intent = new Intent(InformationActivity.this, MapsActivity2.class);
        startActivity(intent);
    }

    public void onClickScan(View view)
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
            AlertDialog.Builder builder = new AlertDialog.Builder(InformationActivity.this);
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
}