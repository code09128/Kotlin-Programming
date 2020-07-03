package com.base.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.base.R;
import com.global.BaseGlobalFunction;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by JyunWu on 2019/2/15.
 */
@SuppressLint("MissingPermission,StaticFieldLeak")
public class LocationUtil implements LocationListener {
    private static LocationUtil locationUtil;
    private LocationManager locationManager;//定位服務
    private Criteria criteria;//設定定位相關資訊
    private boolean isLocationChanged;//是否更新位置資訊
    private LocationChangeListener listener;//監聽器
    private Context context;

    public static LocationUtil getLocationUtil() {
        if (locationUtil == null) {
            locationUtil = new LocationUtil();
        }

        return locationUtil;
    }

    public interface LocationChangeListener {
        void onLocationChanged(double longitude, double latitude);
    }

    /*設置當前監聽器*/
    public void setLocationChangeListener(LocationChangeListener listener) {
        this.listener = listener;
    }

    public void getLocationInfo(Context context) {
        this.context = context;

        init();

        /*獲取定位資訊*/
        requestLocationUpdates();
        Location location = getLastKnownLocation();

        if (listener != null) {
            /*定位為GPS時*/
            if (location != null & locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                listener.onLocationChanged(location.getLongitude(), location.getLatitude());
            } else {
                BaseGlobalFunction.setToast(context, context.getString(R.string.location_not_found));
            }
        }
    }

    private void init() {
        if (locationManager == null) {
            /*是否更新位置資訊*/
            setLocationChanged(false);

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            criteria = new Criteria();

            /*設定定位精確度 Criteria.ACCURACY_COARSE 比較粗略， Criteria.ACCURACY_FINE則比較精細*/
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            /*設定是否需要海拔資訊 Altitude*/
            criteria.setAltitudeRequired(true);
            /*設定是否需要方位資訊 Bearing*/
            criteria.setBearingRequired(true);
            /*設定對電源的需求*/
            criteria.setPowerRequirement(Criteria.POWER_LOW);
        }
    }

    /**取得最精準定位資訊*/
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }

            /*數字越低，越準確*/
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }

        return bestLocation;
    }

    /**500毫秒更新一次位置資訊*/
    private void requestLocationUpdates() {
        /*獲取GPS資訊提供者*/
//        String bestProvider = locationManager != null ? locationManager.getBestProvider(criteria, true) : null;
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
    }

    /*移除定位監聽器*/
    public void removeLocationListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /*是否更新位置資訊*/
    private boolean isLocationChanged() {
        return isLocationChanged;
    }

    /*設置更新位置資訊*/
    private void setLocationChanged(boolean locationChanged) {
        isLocationChanged = locationChanged;
    }

    @Override
    public void onLocationChanged(Location location) {
        /*是否更新位置資訊*/
        if (isLocationChanged()) {
            listener.onLocationChanged(location.getLongitude(), location.getLatitude());
        }
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
}