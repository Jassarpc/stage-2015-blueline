package mg.blueline.gulfsat.argusfinder.GPSTrackers;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;





import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;



import mg.blueline.gulfsat.argusfinder.session.SessionManagement;

/**
 * Created by hamidullah on 10/14/15.
 * Start tracking now!!!!! <3 MadaGeeksCar <3
 */
public class GPSTracker extends Service implements LocationListener {
    private Context context;
    private static final String logTag = "GPSTracker";

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

    Location location;

    double longitude;
    double latitude;

    private final static long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;
    private final static long MIN_TIME_BW_UPDATE = 1000 * 60 * 1;

    /**
     * 1s=1000 milliseconds donc on a une minute avec 1000*60*1 ,,,actualisation chaque minute<M></M>
     */

    SharedPreferences pref;
    protected LocationManager locationManager;


    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
        pref= PreferenceManager.getDefaultSharedPreferences(context);
        Double d= getLatitude();
        String lat=d.toString();
        Double dx=getLongitude();
        String lon=dx.toString();
        updateSession(lat,lon);

    }

    private void getLocation() {

        try {

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {


            } else {
                this.canGetLocation = true;


                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATE,
                            MIN_DISTANCE_CHANGE_FOR_UPDATE,
                            this);
                if (locationManager!=null){
                location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (location!=null){
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();
                    Log.i(logTag, "Location from network Provider was taken");
                    }
                }
                }

                if(isGPSEnabled){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATE,
                            MIN_DISTANCE_CHANGE_FOR_UPDATE,
                            this);

                    if (locationManager!=null){
                        location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location!=null){
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
                    Log.i(logTag, "Location from GPS Provider was taken");
                        }
                    }
                }

            }


            }catch (Exception e){
            e.printStackTrace();
            Log.e(logTag,e.getMessage());

        }
    }

    public void stopGettingFromGPS(){
        if (locationManager!=null){
            locationManager.removeUpdates(GPSTracker.this);
        }

    }
    public boolean canGetLocation(){
        return this.canGetLocation;
    }
    public double getLatitude(){

        if (location!=null){
            latitude=location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){

        if (location!=null){
            longitude=location.getLongitude();
        }
        return longitude;
    }
    public void showSettingsAlert(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS is setting");
        alertDialog.setMessage("Do you want to go to the menu to enable it");
        alertDialog.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        alertDialog.setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(i);
            }
        });
    alertDialog.show();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updateSession(String lat, String lon){

        SharedPreferences.Editor editor=pref.edit();
        editor.putString(SessionManagement.SESSION_LAT, lat);
        editor.putString(SessionManagement.SESSION_LONG,lon);
        editor.commit();

        Log.d("Session", "User login session modified!");


    }

}
