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
    public LocationHelper(Context context, LifecycleOwner owner) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        owner.getLifecycle().addObserver(this);
    }

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

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        stopLocation();
    }

    public interface LocationCallback {
        void onSuccess(double latitude, double longitude);

        void onFailure(Throwable throwable);
    }
}
