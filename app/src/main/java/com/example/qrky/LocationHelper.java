package com.example.qrky;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * this class is responsible for retrieving the last known GPS location of the device.
 */
public class LocationHelper implements DefaultLifecycleObserver {
    private final Handler handler;
    private LocationCallback callback;
    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (callback != null) {
                handler.removeCallbacksAndMessages(null);
                callback.onSuccess(location.getLatitude(), location.getLongitude());
            }
        }
    };

    private final Context context;

    /**
     *this method is a constructor for a class called LocationHelper. The constructor accepts two parameters: context and owner.
     * @param context The context of the application.
     * @param owner The LifecycleOwner object that is responsible for managing the lifecycle of the LocationHelper object.
     */
    public LocationHelper(Context context, LifecycleOwner owner) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        owner.getLifecycle().addObserver(this);
    }

    /**
     * this method is responsible for retrieving the last known GPS location of the device.
     * @return The last known GPS location of the device.
     */
    public Location getGPS() {
        Location location = null;
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return location;
    }

    /**
     *this method is responsible for retrieving the last known network-based location of the device.
     * @return The last known network-based location of the device.
     */
    public Location getNetwork() {
        Location location = null;
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    /**
     * this method is is responsible for retrieving the last known location of the device based on the best available provider that matches the given criteria.
     * @param criteria The criteria that is used to select the best available provider.
     * @return The last known location of the device based on the best available provider that matches the given criteria.
     */
    public Location getBest(Criteria criteria) {
        Location location;
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (criteria == null) {
            criteria = new Criteria();
        }
        String provider = manager.getBestProvider(criteria, true);
        if (TextUtils.isEmpty(provider)) {
            location = getNetwork();
        } else {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            location = manager.getLastKnownLocation(provider);
        }
        return location;
    }

    /**
     *this method is responsible for requesting location updates from the specified provider and handling the location update events via the provided callback.
     * @param provider The name of the provider with which to register for location updates.
     * @param callback The callback that is invoked when the location is updated.
     */
    public void startLocation(String provider, LocationCallback callback) {
        if (callback != null) {
            this.callback = callback;
            handler.postDelayed(() -> {
                handler.removeCallbacksAndMessages(null);
                callback.onFailure(new RuntimeException("Timeout"));
            }, 15000);
        }
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(provider, 5000L, 0.0F, listener);
    }

    /**
     *this method is responsible for stopping the location updates that were previously requested and removing any pending callbacks related to location updates.
     */
    public void stopLocation() {
        handler.removeCallbacksAndMessages(null);
        if (callback != null) {
            LocationManager manager =
                    (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.removeUpdates(listener);
        }
    }
    /**
     * this method is responsible for handling the onDestroy lifecycle event of the associated LifecycleOwner (such as an Activity or Fragment). The method is called when the LifecycleOwner is being destroyed.
     */
    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        stopLocation();
    }

    /**
     * this method is responsible for handling the onPause lifecycle event of the associated LifecycleOwner (such as an Activity or Fragment). The method is called when the LifecycleOwner is being paused.
     */
    public interface LocationCallback {
        void onSuccess(double latitude, double longitude);

        void onFailure(Throwable throwable);
    }
}
