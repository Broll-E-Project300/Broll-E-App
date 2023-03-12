package com.qrscanner.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button buttonScan, buttonMap, signOut;
    TextView signInMain, createAccountMain;
    FirebaseAuth firebaseAuth;
    private Button buttonPlans, buttonCard;
    //Create a new instance of the accounts controller
    AnalyticsController analytics;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize analytics in oncreate.
        analytics = new AnalyticsController();

        //Send the screen name to the analytics Controller
        analytics.SendScreenNameToAnalytics("Main Activity");

        Button buttonPay = findViewById(R.id.btnPay);


        PaymentConfiguration.init(this,Publishable_Key);
        paymentSheet=new PaymentSheet(this, paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);


        });

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlow();
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerId = object.getString("id");
                            Toast.makeText(MainActivity.this, customerId, Toast.LENGTH_SHORT).show();
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
                header.put("authorization", "Bearer " +Secret_Key);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(
                MainActivity.this );
        requestQueue.add(stringRequest);

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

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Approved", Toast.LENGTH_SHORT).show();
        }

    }

    private void getEphericalkey(String customerId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            Toast.makeText(MainActivity.this, EphericalKey, Toast.LENGTH_SHORT).show();

                            getClientSecret(customerId, EphericalKey);
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
                MainActivity.this );
        requestQueue.add(stringRequest);

    }

    private void getClientSecret(String customerId, String ephericalKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            Toast.makeText(MainActivity.this, ClientSecret, Toast.LENGTH_SHORT).show();



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


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("customer", customerId);
                params.put("amount", "399"+"00");
                params.put("currency", "eur");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(
                MainActivity.this );
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

                    Map<String, Object> umbrellaSession = new HashMap<>();
                    umbrellaSession.put("dateCreated","" + date);
                    umbrellaSession.put("paymentStatus","Pending");
                    umbrellaSession.put("UmbrellaID","" + result.getContents());
                    umbrellaSession.put("userID","Alex");

/*                    db.collection("umbrellaSession").document("J8mc1h1HuxtfG6275JQG")
                            .set(umbrellaSession)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });*/

                    db.collection("umbrellaSession")
                            .add(umbrellaSession)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                }
                            });

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