package com.qrscanner.app;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.qrscanner.app.databinding.ActivityMaps2Binding;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Send the screen name to the analytics Controller
        AnalyticsController analytics = new AnalyticsController();
        analytics.SendScreenNameToAnalytics("Maps Activity");

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(MapsActivity2.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MapsActivity2.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MapsActivity2.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


        db.collection("testKiosks")
                .whereEqualTo("Status", "Online")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

/*                                String umbrellas = document.getString("Umbrellas");
                                int NoofUmb = Integer.parseInt(umbrellas);*/

                                List<String> list = (List<String>) document.get("UmbrellasArray");
                                int NoofUmb = list.size();
                                //String Umbrella1 = list.get(0);

                                int availableSpaces = 6 - NoofUmb;

                                String LocationLat = document.getString("LocationLat");
                                String LocationLng = document.getString("LocationLng");
                                double LocLat = Double.parseDouble(LocationLat);
                                double LocLng = Double.parseDouble(LocationLng);

/*                                String AvailableSpaces = document.getString("UmbSpacesAvailable");
                                int availableSpaces = Integer.parseInt(AvailableSpaces);*/

                                String LocationName = document.getString("LocationName");

                                String Status = document.getString("Status");

                                LatLng Location = new LatLng(LocLat, LocLng);

                                MarkerOptions marker = new MarkerOptions();
                                marker.position(Location);
                                marker.title(LocationName);
                                marker.snippet("Umbrellas: " + NoofUmb + "\n" + "Umbrella Spaces: " + availableSpaces + "\n" + "Status: " + Status);
                                if (NoofUmb == 0) {
                                    marker.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_beach_access_red));
                                }
                                else
                                {
                                    marker.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_beach_access_24));
                                }
                                mMap.addMarker(marker);

                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(@NonNull Marker marker) {
                                        String markerTitle = marker.getTitle();
                                        String markerInfo = marker.getSnippet();

                                        //  Toast.makeText(MapsActivity.this, "Snippet is: " + markerUmbrellas, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(MapsActivity2.this,  InformationActivity.class);
                                        intent.putExtra("Location", markerTitle);
                                        intent.putExtra("Information", markerInfo);
                                        //  intent.putExtra("Umbrellas", document.getString("Umbrellas"));
                                        //  intent.putExtra("Spaces", document.getString("UmbSpacesAvailable"));
                                        startActivity(intent);

                                    }
                                });

                            }
                        }
                    }
                });

        db.collection("testKiosks")
                .whereEqualTo("Status", "Offline")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

 /*                                String umbrellas = document.getString("Umbrellas");
                                int NoofUmb = Integer.parseInt(umbrellas);*/

                                List<String> list = (List<String>) document.get("UmbrellasArray");
                                int NoofUmb = list.size();
                                //String Umbrella1 = list.get(0);

                                int availableSpaces = 6 - NoofUmb;

                                String LocationLat = document.getString("LocationLat");
                                String LocationLng = document.getString("LocationLng");
                                double LocLat = Double.parseDouble(LocationLat);
                                double LocLng = Double.parseDouble(LocationLng);

/*                                String AvailableSpaces = document.getString("UmbSpacesAvailable");
                                int availableSpaces = Integer.parseInt(AvailableSpaces);*/

                                String LocationName = document.getString("LocationName");

                                String Status = document.getString("Status");

                                LatLng Location = new LatLng(LocLat, LocLng);

                                MarkerOptions marker = new MarkerOptions();
                                marker.position(Location);
                                marker.title(LocationName);
                                marker.snippet("Umbrellas: " + NoofUmb + "\n" + "Umbrella Spaces: " + availableSpaces + "\n" + "Status: " + Status);
                                if (NoofUmb == 0) {
                                    marker.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_beach_access_red));
                                }
                                else
                                {
                                    marker.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_beach_access_24));
                                }
                                mMap.addMarker(marker);

                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(@NonNull Marker marker) {
                                        String markerTitle = marker.getTitle();
                                        String markerInfo = marker.getSnippet();

                                        //  Toast.makeText(MapsActivity.this, "Snippet is: " + markerUmbrellas, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(MapsActivity2.this,  InformationActivity.class);
                                        intent.putExtra("Location", markerTitle);
                                        intent.putExtra("Information", markerInfo);
                                        //  intent.putExtra("Umbrellas", document.getString("Umbrellas"));
                                        //  intent.putExtra("Spaces", document.getString("UmbSpacesAvailable"));
                                        startActivity(intent);

                                    }
                                });

                            }
                        }
                    }
                });


    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicHeight(),
                vectorDrawable.getIntrinsicWidth());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void closestKiosk(View view)
    {
       /* private String getUrl(double latitude, double longitude) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 1500);
        googlePlacesUrl.append("&keyword=restaurant");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyC4xjcqqSIqt-UrvFLYLIN7CGYqJnxr2Rk");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }*/
    }
}