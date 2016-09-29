package org.bspb.smartbirds.pro.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.GpsStatusChangedEvent;

@EService
public class TrackingService extends Service implements LocationListener {

    private static final String TAG = SmartBirdsApplication.TAG + ".Tracking";

    @Bean
    EEventBus eventBus;

    boolean isGpsEnabled = false;
    boolean tracking = false;
    Location lastLocation;
    @SystemService
    LocationManager locationManager;
    @SystemService
    PowerManager powerManager;

    private final IBinder binder = new Binder() {
        public TrackingService getService() {
            return TrackingService.this;
        }
    };
    private PowerManager.WakeLock wakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!isTracking()) {
            Log.v(TAG, "Tracking self-stopping");
            stopSelf();
        }

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand...");
        startTracking();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Tracking destroying");
        if (isTracking()) {
            stopTracking();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        } else {
            throw new IllegalStateException("No permission to retrieve location!");
        }
        super.onDestroy();
    }

    private void startTracking() {
        Log.v(TAG, "Requesting location updates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            throw new IllegalStateException("No permission to retrieve location!");
        }

        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.acquire();

        tracking = true;
    }

    private void stopTracking() {
        Log.v(TAG, "Stopping location updates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        } else {
            throw new IllegalStateException("No permission to retrieve location!");
        }
        tracking = false;
        wakeLock.release();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location changed: " + location);
        setGpsEnabled(true);
        setLastLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // nothing to do here
    }

    @Override
    public void onProviderEnabled(String provider) {
        setGpsEnabled(true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        setGpsEnabled(false);
    }

    public boolean isTracking() {
        return tracking;
    }

    public void setGpsEnabled(boolean isGpsEnabled) {
        if (this.isGpsEnabled != isGpsEnabled) {
            this.isGpsEnabled = isGpsEnabled;
            eventBus.postSticky(new GpsStatusChangedEvent(isGpsEnabled));
        }
    }

    public void setLastLocation(Location location) {
        this.lastLocation = location;
        if (isTracking()) {
            eventBus.postSticky(location);
        }
    }
}
