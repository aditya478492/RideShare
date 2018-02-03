package com.example.aditya.uber_adi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    Switch swtch=null;
    Button btn=null;

    public void getStarted(View view){
        if(swtch.isChecked()){
            ParseUser.getCurrentUser().put("riderOrDriver","rider");
            Intent rideractvity=new Intent(getApplicationContext(),RiderActivity.class);
            startActivity(rideractvity);
        }else if(swtch.isChecked()==false){
            ParseUser.getCurrentUser().put("riderOrDriver","driver");
            Intent driveractivity=new Intent(this,DriverList.class);
            startActivity(driveractivity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        swtch=(Switch)findViewById(R.id.switch1);
        btn=(Button)findViewById(R.id.button);

        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e==null){
                    Toast.makeText(MainActivity.this, "Logged In !!", Toast.LENGTH_SHORT).show();
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
