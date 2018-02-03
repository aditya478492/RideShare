package com.example.aditya.uber_adi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager = null;
    LocationListener locationListener = null;
    Button btn=null;
    TextView et=null;
    String checker=null;
    int chk;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getUber(View view){
        Log.i("chk in click",""+chk);
        if(chk%2!=0) {
            checker = "getUber";
            ParseObject obj = new ParseObject("RiderActivity");
            obj.put("UserName", ParseUser.getCurrentUser().getUsername());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                UpdateMap(lastknownlocation);
                ParseGeoPoint locationParse = new ParseGeoPoint(lastknownlocation.getLatitude(), lastknownlocation.getLongitude());
                obj.put("Location", locationParse);
                obj.put("Status","Requested");
                obj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(RiderActivity.this, "Requested !!", Toast.LENGTH_SHORT).show();
                            btn.setText("Cancel Request");
                            btn.setBackgroundColor(0xFFFF0000);
                            chk=2;
                            Log.i("chk in YES",""+chk);
                            if (checker.equals("getUser")) {
                                et.setText("Searching for nearby Drivers_ _ _ _ _ ");
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }else if(chk%2==0){
            Log.i("chk","Entered NO");
            new AlertDialog.Builder(RiderActivity.this)
                    .setTitle("Are you sure to cancel Uber ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ParseQuery<ParseObject> query=ParseQuery.getQuery("RiderActivity");
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e==null){
                                        Log.i("chk","current user="+ParseUser.getCurrentUser().toString());
                                        for(ParseObject obj:objects){
                                            Log.i("chk","table user name="+obj.get("UserName"));
                                            if(ParseUser.getCurrentUser().getUsername().equals(obj.get("UserName").toString())){
                                                obj.put("Status","Cancelled");
                                                obj.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if(e==null){
                                                            btn.setText("Request Uber");
                                                            btn.setBackgroundColor(0xFF00FF00 );
                                                            chk=3;
                                                            Log.i("chk in NO",""+chk);
                                                            Toast.makeText(RiderActivity.this, "Cancelled !!", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Log.i("chk","error save in background");
                                                        }
                                                    }
                                                });
                                            }else{
                                                Log.i("chk","error in equals");
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    }

    public void UpdateMap(Location location) {
        LatLng new_location = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new_location, 17));
        mMap.addMarker(new MarkerOptions().position(new_location).title("Your Location"));
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
            UpdateMap(lastknownlocation);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn=(Button)findViewById(R.id.btn);
        et=(TextView)findViewById(R.id.et);
        btn.setBackgroundColor(0xFF00FF00 );
        chk=1;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                UpdateMap(location);
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
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style));

            if (!success) {
                Log.i("chk", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.i("chk", "Can't find style. Error: ", e);
        }
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
            UpdateMap(lastknownlocation);
        }
    }
}
