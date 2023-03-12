package com.qrscanner.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Plans extends AppCompatActivity {

    ImageView plans_backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        plans_backbtn = findViewById(R.id.plan_backbutton);

        //Send the screen name to the analytics Controller
        AnalyticsController analytics = new AnalyticsController();
        analytics.SendScreenNameToAnalytics("Plans Activity");

        plans_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}