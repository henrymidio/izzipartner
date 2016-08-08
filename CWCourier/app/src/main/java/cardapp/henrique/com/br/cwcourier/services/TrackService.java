package cardapp.henrique.com.br.cwcourier.services;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import cardapp.henrique.com.br.cwcourier.pojo.Entregador;
import io.realm.Realm;

public class TrackService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    final int PERMISSION_ACCESS_FINE_LOCATION_CODE = 6;

    LocationRequest mLocationRequest;
    GoogleApiClient gac;
    private long idCourier;

    public TrackService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Realm realm = Realm.getInstance(this);
        Entregador entregador = realm.where(Entregador.class).findFirst();
        idCourier = entregador.getId();
        realm.close();

        callConnection();
        initLocationRequest();
        return START_STICKY;
    }

    private synchronized void callConnection(){
        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        gac.connect();

    }

    public void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(9000);
        mLocationRequest.setFastestInterval(8000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(gac, mLocationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Entregador.updateLocation(getApplicationContext(), idCourier, location.getLatitude(), location.getLongitude());
                    Log.i("Service", "Rodando");

                }
            });
        } catch (SecurityException se){
            Toast.makeText(this, "GPS DESATIVADO", Toast.LENGTH_LONG).show();
        }





    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        gac.disconnect();
        Log.i("Service", "Parado");
    }

    /*
    class Worker extends Thread {
        public Worker(){

        }

        @Override
        public void run() {
            super.run();
        }
    }
    */

    //MÉTODOS DO GOOGLE API CLIENT
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Falha na geolocalização", Toast.LENGTH_LONG).show();
    }

}

