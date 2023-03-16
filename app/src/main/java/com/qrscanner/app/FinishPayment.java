package com.qrscanner.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FinishPayment extends AppCompatActivity {
    RadioGroup payradio_btn;
    Button pay_btn;
    Boolean selected = false;
    String price, amount;
    ProgressBar payloading;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());


    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration configuration;

    String Secret_Key="sk_test_51MWmAWAN6SGb0UM2Q779ZFV8IuLThGCZ7tMao8PBYWg5rVhTY1I7M0eKBYbxi3F17IJ2FAOPldLP7zl4eMR02ahy00U3G94ekP";
    String Publishable_Key="pk_test_51MWmAWAN6SGb0UM210EmE3aLhc7I39xyR9pUVQcENjv1815rhrvKxPg3nk8B0NndbtHM7rXE8APYlShccWgRHgji00jB0n7kk1";

    String customerId;
    String EphericalKey;
    String ClientSecret;
    String umbID_scaned, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_payment);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        payradio_btn = findViewById(R.id.payradio);
        pay_btn = findViewById(R.id.payprice_btn);
        ImageButton pay_backbutton = findViewById(R.id.pay_backbutton);
        payloading = findViewById(R.id.pay_progressbar);

        Intent intent = getIntent();
        umbID_scaned = intent.getStringExtra("message_key");

        PaymentConfiguration.init(this,Publishable_Key);
        paymentSheet=new PaymentSheet(this, paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);


        });

        pay_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                finish();
            }
        });
        payradio_btn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedid) {
                switch (checkedid){
                    case R.id.plan_4hr_radio:
                        price ="070";
                        selected = true;
                        sentData(price);
                        break;
                    case R.id.plan_12hr_radio:
                        price = "200";
                        selected = true;
                        sentData(price);

                        break;
                    case R.id.plan_24hr_radio:
                        price = "430";
                        selected = true;
                        sentData(price);
                        break;
                }
                amount = price;

                //Toast.makeText(FinishPayment.this, amount, Toast.LENGTH_SHORT).show();
            }
        });

        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected!=false){
                    //Toast.makeText(FinishPayment.this, "OSHEEEEEE "+price, Toast.LENGTH_SHORT).show();
                    payloading.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PaymentFlow();
                            payloading.setVisibility(View.GONE);
                        }

                    },2000);


                }else{

               }
            }
        });

    }
    private void sentData(String amount){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);
                            customerId = object.getString("id");
                            //Toast.makeText(getApplicationContext(), customerId, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(FinishPayment.this, amount, Toast.LENGTH_SHORT).show();
                            getEphericalkey(customerId,amount);

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
                header.put("authorization", "Bearer " +Secret_Key);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(
                getApplicationContext() );
        requestQueue.add(stringRequest);

    }
    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if(paymentSheetResult instanceof PaymentSheetResult.Completed){

            Map<String, Object> umbrellaSession = new HashMap<>();
            umbrellaSession.put("dateCreated","" + date);
            umbrellaSession.put("paymentStatus","Paid");
            umbrellaSession.put("UmbrellaID","" + umbID_scaned);
            umbrellaSession.put("userID",userID);
            umbrellaSession.put("StripeCustomerID", customerId);

            db.collection("umbrellaSession")
                    .add(umbrellaSession)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            startActivity(new Intent(getApplicationContext(), SuccessfulPay.class));
                            finish();
                        }
                    });

            //Toast.makeText(this, "Payment Approved", Toast.LENGTH_SHORT).show();
        }
    }
    private void getEphericalkey(String customerId, String amount) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            //Toast.makeText(getApplicationContext(), EphericalKey, Toast.LENGTH_SHORT).show();

                            getClientSecret(customerId, EphericalKey, amount);
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
                header.put("authorization", "Bearer " +Secret_Key);
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
                getApplicationContext() );
        requestQueue.add(stringRequest);

    }
    private void getClientSecret(String customerId, String ephericalKey, String amount) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            //Toast.makeText(getApplicationContext(), ClientSecret, Toast.LENGTH_SHORT).show();



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
                header.put("authorization", "Bearer " +Secret_Key);

                return header;
            }


            String money = "080";
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("customer", customerId);
                params.put("amount", amount);
                params.put("currency", "eur");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(
                getApplicationContext() );
        requestQueue.add(stringRequest);
    }
    private void PaymentFlow() {


        paymentSheet.presentWithPaymentIntent(
                ClientSecret, new PaymentSheet.Configuration("Broll-E",
                        new PaymentSheet.CustomerConfiguration(
                                customerId,
                                EphericalKey
                        ))
        );

    }

}
