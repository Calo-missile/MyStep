package com.hsc.mystep.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hsc.mystep.R;
import com.hsc.mystep.controller.service.StepService;
import com.hsc.mystep.controller.service.UpdateUiCallBack;
import com.hsc.mystep.utils.SharedPreferencesUtils;
import com.hsc.mystep.view.StepArcView;

import java.util.ArrayList;
import java.util.List;


/**
  主业
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_data;
    private StepArcView step_view;
    private TextView tv_set;
    private TextView tv_isSupport;
    private TextView tv_baidu;
    private SharedPreferencesUtils sp;
    
    public LocationClient mLocationClient;

    private boolean isBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        addListener();
        initBaidu();
        initPermissionResult();
        
    }

    private void initView() {
        tv_data = (TextView) findViewById(R.id.tv_data);
        step_view = (StepArcView) findViewById(R.id.step_view);
        tv_set = (TextView) findViewById(R.id.tv_set);
        tv_isSupport = (TextView) findViewById(R.id.tv_isSupport);
        tv_baidu = (TextView) findViewById(R.id.tv_baidu);
    }
    
    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
        //设置当前步数为0
        step_view.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        tv_isSupport.setText("正在计步...");
        setupService();
        /*Intent intent = getIntent();
        tv_baidu.setText(intent.getStringExtra("weather_id"));*/
    }
    
    private void initPermissionResult(){
        List<String> list = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!list.isEmpty()) {
            String[] permission = list.toArray(new String[list.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permission, 1);
        } else {
            requestLocation();
        }
    }
    
    private void initBaidu(){
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
    }
    
    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }
    
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 授权
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    private void addListener() {
        tv_set.setOnClickListener(this);
        tv_data.setOnClickListener(this);
    }
    
    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String currentPosition = bdLocation.getDistrict();// 获取区
            tv_baidu.setText(currentPosition);
        }
    }
    
    /**
     *  开启计步服务
     * */
    private void setupService(){
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * 用于查询应用服务 (application Service)的状态的一种interface
     * 更详细的信息可以参考Service 和 context.bindService()中的描述
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接
         * @param componentName  实际所连接到的组件名称
         * @param iBinder  服务的通信信道的IBind, 可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            StepService service = ((StepService.StepBinder) iBinder).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
            step_view.setCurrentCount(Integer.parseInt(planWalk_QTY),service.getStepCount());
            // 设置步数监听回调
            service.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
                    step_view.setCurrentCount(Integer.parseInt(planWalk_QTY), stepCount);
                }
            });
        }

        /**
         * 当与service之间的连接丢失的时候会调用该方法
         * 这种情况经常发生在Service所在的进程崩溃或者被kill的时候调用,
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()
         * @param componentName   丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_set:
                startActivity(new Intent(this, StepPlanActivity.class));
                break;
            case R.id.tv_data:
                startActivity((new Intent(this, HistoryActivity.class)));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        if (isBind) {
            this.unbindService(conn);
        }
    }
}
