package com.hsc.mystep.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hsc.mystep.R;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AppCompatActivity {

    public LocationClient mLocationClient;
    private TextView tvAddress;
    private  String weather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        //initBaidu();
        //requestLocation();
        //initPermissionResult();
        final Intent intent = new Intent(this, MainActivity.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                    //intent.putExtra("weather_id", weather);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
       
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void initPermissionResult(){
        List<String> list = new ArrayList<>();
       if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!list.isEmpty()) {
            String[] permission = list.toArray(new String[list.size()]);
            ActivityCompat.requestPermissions(FirstActivity.this, permission, 1);
        } else {
            requestLocation();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private void initBaidu(){
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener2());
    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }
    
    public class MyLocationListener2 implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String currentPosition = bdLocation.getDistrict();// 获取区
            tvAddress.setText(currentPosition);
            if (!"".equals(currentPosition)) {
                weather = currentPosition;
                System.out.println(weather);
                Log.d("FirstActivity", weather);
            }
        }
    }

    
}
