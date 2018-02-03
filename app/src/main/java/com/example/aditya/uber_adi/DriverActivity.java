package com.example.aditya.uber_adi;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DriverActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Intent DriverMapAct;

    public void StartNavigation(View view){
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+DriverMapAct.getDoubleExtra("Driverlat",0)+","+DriverMapAct.getDoubleExtra("Driverlng",0)+"&daddr="+DriverMapAct.getDoubleExtra("lat",0)+","+DriverMapAct.getDoubleExtra("lng",0)+""));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DriverMapAct=getIntent();
        ArrayList<Marker> markers=new ArrayList<>();
        LatLng DriverLocMrk = new LatLng(DriverMapAct.getDoubleExtra("Driverlat",0),DriverMapAct.getDoubleExtra("Driverlng",0));
        LatLng UserLocMrk=new LatLng(DriverMapAct.getDoubleExtra("lat",0),DriverMapAct.getDoubleExtra("lng",0));
        markers.add(mMap.addMarker(new MarkerOptions().position(DriverLocMrk).title("YOur Location")));
        markers.add(mMap.addMarker(new MarkerOptions().position(UserLocMrk).title("YOur Location")));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(markers.get(0).getPosition());
        builder.include(markers.get(1).getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }
}
