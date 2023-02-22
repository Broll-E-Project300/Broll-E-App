package com.qrscanner.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * A main controller class for all events ahandling and tracking
 * functionality.
 */
public class AnalyticsController extends AppCompatActivity
{

    //Add for google Firebase Analytics
    private FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Added for Google Firebase Analytics
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    /**
     * Sends a transaction's id to the analytics.
     * @param transactionID
     */
    public void SendTransactionID(String transactionID)
    {
        //Create a new bundle instances
        Bundle params = new Bundle();
        //Insert the transaction id into the bundle instance
        params.putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionID);
        //Dispatch the event to the analytics.
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
    }

    /**
     *Sends a button click message to the analytics
     *
     * @param button
     */
    public void SendButtonClick(String button)
    {
        //Create a new bundle instances
        Bundle bundle = new Bundle();
        //Assign the custom event with button name to bundle
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button);
        //Dispatch the event to the analytics.
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * Sends the screen name to the analytics
     * Should be called on each activity's OnCreate with the name
     * passed in for tracking.
     * @param screenName
     */
    public void SendScreenNameToAnalytics(String screenName)
    {
        //Create a new bundle instances
        Bundle bundle = new Bundle();
        //Insert the screen name into the bundle
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, screenName);
        //Dispatch the event to the analytics.
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
