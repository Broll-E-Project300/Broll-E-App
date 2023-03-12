package com.qrscanner.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.DescriptorProtos;
import com.qrscanner.app.databinding.ActivityMaps2Binding;

import java.util.List;

public class MapFragment extends Fragment {
    ImageButton mylocation_btn;
    Integer check = 0;
    ItemViewModel viewModel;
    int umbrellaNumber, available = 6;
    Location currentLocation;
    String ulat, ulong;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 101;

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private FirebaseFirestore db;
    private LocationRequest locationRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location!=null){
                            currentLocation = location;
                            ulat = String.valueOf(location.getLatitude());
                            ulong = String.valueOf(location.getLongitude());

                        }
                        //Initiate googleMap
                        SupportMapFragment supportMapFragment = (SupportMapFragment)
                                getChildFragmentManager().findFragmentById(R.id.map);
                        db = FirebaseFirestore.getInstance();
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                mMap = googleMap;
                                mMap.setMyLocationEnabled(true);
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                //Toast.makeText(getActivity(), ulat, Toast.LENGTH_SHORT).show();
                                LatLng userlocale = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                                //mMap.addMarker(new MarkerOptions().position(userlocale));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocale,15));



                                db.collection("testKiosks")
                                        .whereEqualTo("Status","Online")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        List<String> list = (List<String>) document.get("UmbrellasArray");
                                                        int NoofUmb = list.size();
                                                        //String Umbrella1 = list.get(0);

                                                        int availableSpaces = available - NoofUmb;

                                                        String LocationLat = document.getString("LocationLat");
                                                        String LocationLng = document.getString("LocationLng");
                                                        double LocLat = Double.parseDouble(LocationLat);
                                                        double LocLng = Double.parseDouble(LocationLng);

                                                        String LocationName = document.getString("LocationName");

                                                        String Status = document.getString("Status");

                                                        LatLng Location = new LatLng(LocLat, LocLng);

                                                        MarkerOptions marker = new MarkerOptions();
                                                        marker.position(Location);

                                                        marker.title(LocationName);
                                                        marker.snippet(String.valueOf(NoofUmb));
                                                        if (NoofUmb == 0) {
                                                            marker.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_baseline_beach_access_red));
                                                        }
                                                        else
                                                        {
                                                            marker.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_baseline_beach_access_24));
                                                        }
                                                        mMap.addMarker(marker);
                                                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                            @Override
                                                            public boolean onMarkerClick(@NonNull Marker marker) {
                                                                showDockDialog(marker);
                                                                return true;
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

                                                        List<String> list = (List<String>) document.get("UmbrellasArray");
                                                        int NoofUmb = list.size();

                                                        int availableSpaces = 6 - NoofUmb;

                                                        String LocationLat = document.getString("LocationLat");
                                                        String LocationLng = document.getString("LocationLng");
                                                        double LocLat = Double.parseDouble(LocationLat);
                                                        double LocLng = Double.parseDouble(LocationLng);

                                                        String LocationName = document.getString("LocationName");

                                                        String Status = document.getString("Status");

                                                        LatLng Location = new LatLng(LocLat, LocLng);

                                                        MarkerOptions marker = new MarkerOptions();
                                                        marker.position(Location);
                                                        marker.title(LocationName);
                                                        marker.snippet(String.valueOf(NoofUmb));
                                                        if (NoofUmb == 0) {
                                                            marker.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_baseline_beach_access_red));
                                                        }
                                                        else
                                                        {
                                                            marker.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_baseline_beach_access_24));
                                                        }
                                                        mMap.addMarker(marker);
                                                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                            @Override
                                                            public boolean onMarkerClick(@NonNull Marker marker) {
                                                                showDockDialog(marker);
                                                                return true;
                                                            }
                                                        });

                                                    }
                                                }
                                            }
                                        });

                                ImageButton location = view.findViewById(R.id.location_btn);
                                location.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocale,15));
                                    }
                                });

                            }
                        });

                    }
                });

        //Return view
        return view;
    }

    private void showDockDialog(Marker marker) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dockbottom_sheet);

        int umb = Integer.parseInt(marker.getSnippet());
        int slots = available-umb;

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DockbottomAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView Location = dialog.findViewById(R.id.umbLocation);
        Location.setText(marker.getTitle());

        TextView umbrellas = dialog.findViewById(R.id.umbNumber);
        TextView umbslots = dialog.findViewById(R.id.umbSlots);
        umbrellas.setText(marker.getSnippet());
        umbslots.setText(String.valueOf(slots));
        //Close docksheet
        ImageButton closeds = dialog.findViewById(R.id.docksheetbackbtn);
        closeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

       /* Button test = view.findViewById(R.id.testing);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changes viewmodel data that MainActivity will detect
                viewModel.setData(check);
            }
        });*/
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


}