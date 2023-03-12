package com.qrscanner.app;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Plans extends AppCompatActivity implements View.OnClickListener{



    private Button buttonPlans, buttonCard;


    PaymentSheet paymentSheet;
    //String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration configuration;

    String Secret_Key="sk_test_51MWmAWAN6SGb0UM2Q779ZFV8IuLThGCZ7tMao8PBYWg5rVhTY1I7M0eKBYbxi3F17IJ2FAOPldLP7zl4eMR02ahy00U3G94ekP";
    String Publishable_Key="pk_test_51MWmAWAN6SGb0UM210EmE3aLhc7I39xyR9pUVQcENjv1815rhrvKxPg3nk8B0NndbtHM7rXE8APYlShccWgRHgji00jB0n7kk1";

    String customerId;
    String EphericalKey;
    String ClientSecret;

    //String amount;

    //day play for 5 euros
    String amountDay = "500";
    // hour plan 0.70/hour
    String amountHour = "70";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        Button hourPlan = findViewById(R.id.hrPlan);

        Button dayPlan = findViewById(R.id.dayPlan);

        PaymentConfiguration.init(this,Publishable_Key);
        paymentSheet=new PaymentSheet(this, paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);


        });

        hourPlan.setOnClickListener(this);
        dayPlan.setOnClickListener(this);


        //method to connect to stripe api
        StringRequest stringRequest = new StringRequest(Request.Method.POST,   "https://api.stripe.com/v1/customers" ,      new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);

                            customerId = object.getString("id");
                            Toast.makeText(Plans.this, customerId, Toast.LENGTH_SHORT).show();

                            getEphericalkey(customerId);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header=new HashMap<>();
                header.put("authorization", "Bearer " + Secret_Key);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(
                Plans.this );
        requestQueue.add(stringRequest);



    } // end Of oncreate

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Approved", Toast.LENGTH_SHORT).show();
        }

    } //end on payment method


  //method that calls payment
    private void PaymentHourFlow() {
        try {
            paymentSheet.presentWithPaymentIntent(
                    ClientSecret,
                    new PaymentSheet.Configuration(
                            "Broll-E",
                            new PaymentSheet.CustomerConfiguration(
                                    customerId,
                                    EphericalKey

                            )
                    )
            );
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void PaymentDayFlow() {
        try {
            paymentSheet.presentWithPaymentIntent(
                    ClientSecret,
                    new PaymentSheet.Configuration(
                            "Broll-E",
                            new PaymentSheet.CustomerConfiguration(
                                    customerId,
                                    EphericalKey
                            )
                    )
            );
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    //method to get connect to the stripe api get key from json
    private void getEphericalkey(String customerId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            Toast.makeText(Plans.this, EphericalKey, Toast.LENGTH_SHORT).show();

                            getClientSecret(customerId, EphericalKey, amountDay);
                              //getEphericalkey(customerId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header=new HashMap<>();
                header.put("Authorization", "Bearer " + Secret_Key);
               header.put("Stripe-Version", "2022-11-15" );
                return header;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("customer", customerId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(
                Plans.this );
        requestQueue.add(stringRequest);

    }

    private void getClientSecret(String customerId, String EphericalKey, String amount) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            Toast.makeText(Plans.this, ClientSecret, Toast.LENGTH_SHORT).show();

                            if (amount.equals(amountHour)) {
                                PaymentHourFlow();
                            } else {
                                PaymentDayFlow();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header=new HashMap<>();

                header.put("Authorization", "Bearer " + Secret_Key);

                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();

                params.put("customer", customerId);
                params.put("amount" , amount);
                params.put("currency", "eur");
                params.put("automatic_payment_methods[enabled]","true");

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(
                Plans.this );
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hrPlan:
                getClientSecret(customerId, EphericalKey, amountHour);
                break;
            case R.id.dayPlan:
                getClientSecret(customerId, EphericalKey, amountDay);
                break;


        }

    }
}