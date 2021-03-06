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
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    String[] REQUIRED_PERMISSIONS  = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location mCurrentLocatiion;
    LatLng currentPosition;
    Double latitude; //위도
    Double longitude;    //경도
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)



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
                        intent.putExtra("latitude",latitude); //위도
                        intent.putExtra("longitude",longitude); //경도
                        intent.putExtra("location",locations);
                        intent.putExtra("radius_value",radius_value);
                        startActivity(intent); //주기만 할 때
                        //   startActivityForResult(intent,0);//뭔가 돌려 받을때
                    }else{
                        throw new NumberFormatException();
                    }

                }catch(NumberFormatException e){
                    Toast.makeText(getApplicationContext(), "위치를 선택해주세요", Toast.LENGTH_SHORT).show();
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
        //초기 위치 동아대세팅!

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        //위치 퍼미션을 가지고 있는지 체크
        //이미 가지고 있다면?
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            startLocationUpdates(); //  위치 업데이트 시작
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])){
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( Mapview.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            }else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        plus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //   =Integer.parseInt(radius.getText().toString());
                if(radius_value<300) {
                    radius_value += 10;
                    radius.setText("반경:" + String.valueOf(radius_value) + "m");
                }else{
                    Toast.makeText(Mapview.this, "최대 반경은 300m 입니다 ",Toast.LENGTH_SHORT).show();
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
                    radius.setText("반경:" + String.valueOf(radius_value) + "m");
                }else{
                    Toast.makeText(Mapview.this, "최소 반경은 30m 입니다 ",Toast.LENGTH_SHORT).show();
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

        // 맵 터치 이벤트 구현 //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {

                Cpoint =point;
                MarkerOptions mOptions = new MarkerOptions();
                // 마커 타이틀
                mOptions.title("마커 좌표");
                latitude = point.latitude; // 위도
                longitude = point.longitude; // 경도
                // 마커의 스니펫(간단한 텍스트) 설정

                List<Address> list = null; // 얻어올 값의 개수
                try {
                    list = geocoder.getFromLocation(
                            latitude, // 위도
                            longitude, // 경도
                            10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(list != null){
                    if(list.size()==0){
                        mOptions.snippet("해당되는 주소 정보는 없습니다.");
                    }else{

                        String []splitStr = list.get(0).toString().split(",");
                        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주
                        editText.setText(address);
                        mOptions.snippet(address);

                    }
                }
                //Toast.makeText(Map_view.this, list.get(0).toString(),Toast.LENGTH_LONG);
                // mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(latitude, longitude));
                // 마커(핀) 추가
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

        // 버튼 이벤트
        button.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                String str=editText.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                    Toast.makeText(Mapview.this, "주소를 찾았습니다 ",Toast.LENGTH_LONG).show();
                }
                catch (IOException e) {
                    Toast.makeText(Mapview.this, "e.getMessage() ", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.d(TAG,"주소변환 실패");
                }

                System.out.println(addressList.get(0).toString());
                // 콤마를 기준으로 split
                String []splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                System.out.println(address);

                String latitudeEx = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String longitudeEx = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                System.out.println(latitude);
                System.out.println(longitude);
                latitude=Double.parseDouble(latitudeEx);
                longitude=Double.parseDouble(longitudeEx);
                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(latitude,longitude );
                // 마커 생성
                MarkerOptions mOptions2 = new MarkerOptions();
                mOptions2.title("search result");
                mOptions2.snippet(address);
                mOptions2.position(point);
                // 마커 추가
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


                // 해당 좌표로 화면 줌
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
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);

                LatLng lo = new LatLng(location.getLatitude(), location.getLongitude());
                if(trigger==0) {
                    trigger++;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lo, 17));
                }
                //현재 위치에 마커 생성하고 이동
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
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
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
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

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
        //현재 위치에 마커 생성 이동 ...

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
        //디폴트 위치, 동아대
        LatLng DEFAULT_LOCATION = new LatLng(35.117588, 128.968036);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


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


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
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
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new OnClickListener() {

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
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }


}