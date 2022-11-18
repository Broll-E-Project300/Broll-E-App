package com.qrscanner.app;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qrscanner.app.databinding.ActivityMaps2Binding;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        LatLng Sligo = new LatLng(54.2766, -8.4761);
        LatLng Cork = new LatLng(51.8985, -8.4756);
        LatLng Belfast = new LatLng(54.5973, -5.9301);

        int umbrellas = 5;

        mMap.addMarker(new MarkerOptions()
                .position(Sligo)
                .title("Broll-E Umbrella")
                .snippet("Umbrellas:" + umbrellas)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_beach_access_24)));

        mMap.addMarker(new MarkerOptions()
                .position(Cork)
                .title("Broll-E Umbrella")
                .snippet("Umbrellas:" + umbrellas)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_beach_access_24)));

        if (umbrellas >= 3) {
            mMap.addMarker(new MarkerOptions()
                    .position(Belfast)
                    .title("Broll-E Umbrella")
                    .snippet("Umbrellas:" + umbrellas)
                    .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_beach_access_green)));
        }

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