package com.qrscanner.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Plans extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        //Send the screen name to the analytics Controller
        AnalyticsController analytics = new AnalyticsController();
        analytics.SendScreenNameToAnalytics("Plans Activity");
    }
}