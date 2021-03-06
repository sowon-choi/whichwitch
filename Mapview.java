package com.project.whichwitch;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Mapview extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    int trigger=0;
    ArrayList<Marker> markers=new ArrayList<Marker>();
    ArrayList<Circle> circles=new ArrayList<Circle>();

    private GoogleMap mMap;
    private Geocoder geocoder;
    private Button button;
    private EditText editText;
    TextView radius;
    Button plus,minus;
    Button mapview_check_btn, mapview_cancel_btn;
    int radius_value;
    LatLng Cpoint;

    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1???
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5???

    // onRequestPermissionsResult?????? ????????? ???????????? ActivityCompat.requestPermissions??? ????????? ????????? ????????? ???????????? ?????? ???????????????.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    String[] REQUIRED_PERMISSIONS  = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // ?????? ?????????

    Location mCurrentLocatiion;
    LatLng currentPosition;
    Double latitude; //??????
    Double longitude;    //??????
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;  // Snackbar ???????????? ???????????? View??? ???????????????.
    // (????????? Toast????????? Context??? ??????????????????.)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_mapview);

        mLayout = findViewById(R.id.layout_map);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        radius=(TextView)findViewById(R.id.mapview_radius_textview);
        plus=(Button)findViewById(R.id.mapview_plus_btn);
        minus=(Button)findViewById(R.id.mapview_minus_btn);
        editText = (EditText) findViewById(R.id.mapview_address_edittext);
        button=(Button)findViewById(R.id.mapview_search_btn);
        mapview_check_btn=(Button)findViewById(R.id.mapview_check_btn);

        mapview_check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    Toast.makeText(getApplicationContext(), editText.getText().toString() + radius_value +" "+ latitude  +" "+ longitude, Toast.LENGTH_SHORT).show();
                try{
                    if(latitude!=null &&longitude !=null){
                        String locations =editText.getText().toString();
                        Intent intent = new Intent(getApplicationContext(),Addlist.class);
                        intent.putExtra("latitude",latitude); //??????
                        intent.putExtra("longitude",longitude); //??????
                        intent.putExtra("location",locations);
                        intent.putExtra("radius_value",radius_value);
                        startActivity(intent); //????????? ??? ???
                        //   startActivityForResult(intent,0);//?????? ?????? ?????????
                    }else{
                        throw new NumberFormatException();
                    }

                }catch(NumberFormatException e){
                    Toast.makeText(getApplicationContext(), "????????? ??????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mapview_cancel_btn=(Button)findViewById(R.id.mapview_cancel_btn);
        mapview_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview_map);
        mapFragment.getMapAsync(this);


        if(radius.getText().toString().length()==7)
            radius_value=Integer.parseInt(radius.getText().toString().substring(3,6));
        else
            radius_value=Integer.parseInt(radius.getText().toString().substring(3,5));




    }


  /*  minus.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){
            int radius_value=Integer.parseInt(radius.getText().toString());
            radius_value-=10;
            radius.setText(String.valueOf(radius_value));
        }
    });*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        setDefaultLocation();
        //?????? ?????? ???????????????!

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        //?????? ???????????? ????????? ????????? ??????
        //?????? ????????? ??????????
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            startLocationUpdates(); //  ?????? ???????????? ??????
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])){
                Snackbar.make(mLayout, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.",
                        Snackbar.LENGTH_INDEFINITE).setAction("??????", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                        ActivityCompat.requestPermissions( Mapview.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // ?????? ???????????? ?????? ????????????
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        plus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //   =Integer.parseInt(radius.getText().toString());
                if(radius_value<300) {
                    radius_value += 10;
                    radius.setText("??????:" + String.valueOf(radius_value) + "m");
                }else{
                    Toast.makeText(Mapview.this, "?????? ????????? 300m ????????? ",Toast.LENGTH_SHORT).show();
                }
                if(!circles.isEmpty()){
                    for(int i=0;i< circles.size();i++){
                        Circle temp=circles.get(i);
                        temp.remove();
                    }
                    CircleOptions circle100M = new CircleOptions().center(Cpoint).radius(radius_value).strokeWidth(0f).fillColor(Color.parseColor("#880000ff"));
                    Circle ctmp=googleMap.addCircle(circle100M);
                    circles.add(ctmp);
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //   int radius_value=Integer.parseInt(radius.getText().toString());
                if(radius_value>30) {
                    radius_value -= 10;
                    radius.setText("??????:" + String.valueOf(radius_value) + "m");
                }else{
                    Toast.makeText(Mapview.this, "?????? ????????? 30m ????????? ",Toast.LENGTH_SHORT).show();
                }
                if(!circles.isEmpty()){
                    for(int i=0;i< circles.size();i++){
                        Circle temp=circles.get(i);
                        temp.remove();
                    }
                    CircleOptions circle100M = new CircleOptions().center(Cpoint).radius(radius_value).strokeWidth(0f).fillColor(Color.parseColor("#880000ff"));
                    Circle ctmp=googleMap.addCircle(circle100M);
                    circles.add(ctmp);
                }
            }
        });

        // ??? ?????? ????????? ?????? //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {

                Cpoint =point;
                MarkerOptions mOptions = new MarkerOptions();
                // ?????? ?????????
                mOptions.title("?????? ??????");
                latitude = point.latitude; // ??????
                longitude = point.longitude; // ??????
                // ????????? ?????????(????????? ?????????) ??????

                List<Address> list = null; // ????????? ?????? ??????
                try {
                    list = geocoder.getFromLocation(
                            latitude, // ??????
                            longitude, // ??????
                            10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(list != null){
                    if(list.size()==0){
                        mOptions.snippet("???????????? ?????? ????????? ????????????.");
                    }else{

                        String []splitStr = list.get(0).toString().split(",");
                        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // ???
                        editText.setText(address);
                        mOptions.snippet(address);

                    }
                }
                //Toast.makeText(Map_view.this, list.get(0).toString(),Toast.LENGTH_LONG);
                // mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: ?????? ?????? ?????? ?????????
                mOptions.position(new LatLng(latitude, longitude));
                // ??????(???) ??????
                //googleMap.addMarker(mOptions.draggable(true));
                if(!markers.isEmpty()){
                    for(int i=0;i<markers.size();i++) {
                        Marker temp = markers.get(i);
                        temp.remove();
                    }
                }
                if(!circles.isEmpty()){
                    for(int i=0;i< circles.size();i++){
                        Circle temp=circles.get(i);
                        temp.remove();
                    }
                }
                Marker temp=googleMap.addMarker(mOptions.draggable(true));
                markers.add(temp);

                CircleOptions circle100M = new CircleOptions().center(point).radius(radius_value).strokeWidth(0f).fillColor(Color.parseColor("#880000ff"));
                Circle ctmp=googleMap.addCircle(circle100M);
                circles.add(ctmp);
            }
        });
        ////////////////////

        // ?????? ?????????
        button.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                String str=editText.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText??? ????????? ?????????(??????, ??????, ?????? ???)??? ?????? ????????? ????????? ??????
                    addressList = geocoder.getFromLocationName(
                            str, // ??????
                            10); // ?????? ?????? ?????? ??????
                    Toast.makeText(Mapview.this, "????????? ??????????????? ",Toast.LENGTH_LONG).show();
                }
                catch (IOException e) {
                    Toast.makeText(Mapview.this, "e.getMessage() ", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.d(TAG,"???????????? ??????");
                }

                System.out.println(addressList.get(0).toString());
                // ????????? ???????????? split
                String []splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // ??????
                System.out.println(address);

                String latitudeEx = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // ??????
                String longitudeEx = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // ??????
                System.out.println(latitude);
                System.out.println(longitude);
                latitude=Double.parseDouble(latitudeEx);
                longitude=Double.parseDouble(longitudeEx);
                // ??????(??????, ??????) ??????
                LatLng point = new LatLng(latitude,longitude );
                // ?????? ??????
                MarkerOptions mOptions2 = new MarkerOptions();
                mOptions2.title("search result");
                mOptions2.snippet(address);
                mOptions2.position(point);
                // ?????? ??????
                //    mMap.addMarker(mOptions2);
                if(!markers.isEmpty()){
                    for(int i=0;i<markers.size();i++) {
                        Marker temp = markers.get(i);
                        temp.remove();
                    }
                }
                if(!circles.isEmpty()){
                    for(int i=0;i< circles.size();i++){
                        Circle temp=circles.get(i);
                        temp.remove();
                    }
                }
                Marker temp=googleMap.addMarker(mOptions2.draggable(true));
                markers.add(temp);
                Cpoint=point;
                CircleOptions circle100M = new CircleOptions().center(point).radius(radius_value).strokeWidth(0f).fillColor(Color.parseColor("#880000ff"));
                Circle ctmp=googleMap.addCircle(circle100M);
                circles.add(ctmp);


                // ?????? ????????? ?????? ???
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,17));
            }
        });
        ////////////////////

        // Add a marker in Sydney and move the camera
        //   LatLng donga = new LatLng(35.117588, 128.968036);
        //    mMap.addMarker(new MarkerOptions().position(donga).title("Marker in DONG-A UNIV."));
        //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(donga,17));
        //   mMap.moveCamera(CameraUpdateFactory.newLatLng(donga));
    }





    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);
                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                        + " ??????:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);

                LatLng lo = new LatLng(location.getLatitude(), location.getLongitude());
                if(trigger==0) {
                    trigger++;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lo, 17));
                }
                //?????? ????????? ?????? ???????????? ??????
                //      setCurrentLocation(location, markerTitle, markerSnippet);

            }
        }

    };

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {
                Log.d(TAG, "startLocationUpdates : ????????? ???????????? ??????");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            //   mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            if (checkPermission())
                mMap.setMyLocationEnabled(true);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();
        if (mFusedLocationClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {
        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

/*
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        //?????? ????????? ?????? ?????? ?????? ...

        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

    }
*/

    public void setDefaultLocation() {
        //????????? ??????, ?????????
        LatLng DEFAULT_LOCATION = new LatLng(35.117588, 128.968036);
        String markerTitle = "???????????? ????????? ??? ??????";
        String markerSnippet = "?????? ???????????? GPS ?????? ?????? ???????????????";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //     currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //??????????????? ????????? ????????? ????????? ?????? ????????????
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????
            boolean check_result = true;
            // ?????? ???????????? ??????????????? ???????????????.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                // ???????????? ??????????????? ?????? ??????????????? ???????????????.
                startLocationUpdates();
            } else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // ???????????? ????????? ????????? ???????????? ?????? ?????? ???????????? ????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();

                }else {


                    // "?????? ?????? ??????"??? ???????????? ???????????? ????????? ????????? ???????????? ??????(??? ??????)?????? ???????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }

        }
    }
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS ????????? ?????????");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }


}