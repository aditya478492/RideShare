package com.example.aditya.uber_adi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DriverList extends AppCompatActivity {

    ListView lst=null;
    ArrayList<String> lst1=new ArrayList<>();
    ArrayAdapter adp;
    LocationManager locationManager=null;
    LocationListener locationListener=null;
    ArrayList<Double> lat=new ArrayList<>();
    ArrayList<Double> lng=new ArrayList<>();
    ParseGeoPoint driverlocation=null;


    public void UpdateList(Location location){
        driverlocation =new ParseGeoPoint(location.getLatitude(),location.getLongitude());
        ParseQuery<ParseObject> query=new ParseQuery<ParseObject>("RiderActivity");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject obj : objects) {
                    Double distance=obj.getParseGeoPoint("Location").distanceInKilometersTo(driverlocation);
                    long round_dist=Math.round(distance*10)/10;
                    lst1.add("" + round_dist + " Kms");
                    lat.add(obj.getParseGeoPoint("Location").getLatitude());
                    lng.add(obj.getParseGeoPoint("Location").getLongitude());
                }
                adp.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastknownlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            UpdateList(lastknownlocation);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);
        adp=new ArrayAdapter(this,android.R.layout.simple_list_item_1,lst1);
        lst=(ListView)findViewById(R.id.lstvw);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                ParseQuery<ParseObject> query1=ParseQuery.getQuery("RiderActivity");
                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for(ParseObject obj:objects){
                            ParseGeoPoint loc=obj.getParseGeoPoint("Location");
                            if(loc.getLatitude()==lat.get(i) && loc.getLongitude()==lng.get(i)){
                                if(obj.get("Status").equals("Requested")){
                                    obj.put("Status","Driver Accepted "+ ParseUser.getCurrentUser().getUsername());
                                    obj.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null){
                                                Toast.makeText(DriverList.this, "Confirmed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Intent driverMap=new Intent(getApplicationContext(),DriverActivity.class);
                                    driverMap.putExtra("Driverlat",driverlocation.getLatitude());
                                    driverMap.putExtra("Driverlng",driverlocation.getLongitude());
                                    driverMap.putExtra("lat",lat.get(i));
                                    driverMap.putExtra("lng",lng.get(i));
                                    startActivity(driverMap);
                                }else{
                                    Toast.makeText(DriverList.this, "Rider Cancelled Ride", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });


            }
        });
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lst.setAdapter(adp);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            return;
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            UpdateList(lastknownlocation);
        }
    }


    }
