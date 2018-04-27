package com.hsc.mystep.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by 15827 on 2018/4/10.
 */

public class StepCountModeDispathcher {

    private Context mContext;
    private boolean hasSensor;

    public StepCountModeDispathcher(Context context) {
        this.mContext = context;
        hasSensor = isSupporStepCountSensor();
    }

    /**
     * 判断该设备是否支持计步
     *
     */
    @TargetApi(Build.VERSION_CODES.KITKAT) 
    public boolean isSupporStepCountSensor() {
        return mContext.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
    }
}

