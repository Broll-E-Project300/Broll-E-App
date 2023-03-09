package com.qrscanner.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.provider.Settings.Secure;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button buttonScan, buttonMap;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = findViewById(R.id.buttonScan);
        buttonMap = findViewById(R.id.buttonMap);
        buttonScan.setOnClickListener(view -> {
            scanCode();
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

                    Map<String, Object> umbrellaSession = new HashMap<>();
                    umbrellaSession.put("dateCreated", "" + date);
                    umbrellaSession.put("paymentStatus", "Pending");
                    umbrellaSession.put("UmbrellaID", "" + result.getContents());
                    umbrellaSession.put("userID", "Alex");

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

                    db.collection("umbrellaCollection")
                            .whereEqualTo("available", true)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if(task.isSuccessful()) {

                                        //String umbrellaID = document.getString("umbrellaID");

                                        DocumentReference docRef = db.collection("umbrellaCollection").document("" + result.getContents());
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                DocumentSnapshot document = task.getResult();

                                                String amountUseda = document.getString("amountUsed");
                                                int amountUsedINT = Integer.parseInt(amountUseda);
                                                int amountUsedPlus = amountUsedINT + 1;
                                                String amountUsedString = String.valueOf(amountUsedPlus);

                                                // FirebaseFirestore.getInstance()
                                                //         .collection("umbrellaCollection").document("" + result.getContents())
                                                //       .get()
                                                //      .getResult();

                                                Map<String, Object> umbrellaCollection = new HashMap<>();
                                                umbrellaCollection.put("available", false );
                                                umbrellaCollection.put("amountUsed", amountUsedString);

                                                db.collection("umbrellaCollection").document("" + result.getContents())
                                                        .set(umbrellaCollection, SetOptions.merge());
                                            }
                                        });



                                    }
                                }
                            });

                }
            }).show();
        }
    });

    public void openMap(View view)
    {
        Intent intent = new Intent(this, MapsActivity2.class);
        startActivity(intent);
    }
}