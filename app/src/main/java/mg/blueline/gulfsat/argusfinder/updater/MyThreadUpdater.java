package mg.blueline.gulfsat.argusfinder.updater;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mg.blueline.gulfsat.argusfinder.GPSTrackers.GPSTracker;
import mg.blueline.gulfsat.argusfinder.MapsActivity;

/**
 * Created by hamidullah on 10/16/15.
 */
public class MyThreadUpdater extends Thread  {


    private SharedPreferences pref;
    private String lat;
    private String lon;
    private String last;
    private String username;
    private Map<String , String> parm;
    private Context context;

    public MyThreadUpdater (Context c){
        context=c;
    }

    @Override
    public void run() {


            try {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i("argus","tay");
                        pref = PreferenceManager.getDefaultSharedPreferences(context);
                        lat = pref.getString("lat", null);
                        lon = pref.getString("long", null);
                        last = getGeocodedLocation(new LatLng(Double.valueOf(lat),Double.valueOf(lon)),context);
                        username = pref.getString("username", null);

                        parm = new HashMap<>();
                        parm.put("username", username);
                        parm.put("long", lon);
                        parm.put("lat", lat);
                        parm.put("last", last);
                        parm.put("tag","update");
                        submitUserLocation(parm, context);
                    }
                }, 0, 1000);
            }catch (Exception e){
               Log.i("argusErr:", e.getMessage());

        }



    }
    public void submitUserLocation(final Map<String,String> params,Context c){

        RequestQueue queue = Volley.newRequestQueue(c);
        StringRequest req = new StringRequest(Request.Method.POST, MapsActivity.SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject object= new JSONObject(s);
                            Log.i("argus", object.getString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        queue.add(req);

    }

    public String getGeocodedLocation(LatLng latLng,Context c){
        GPSTracker gpsTracker = new GPSTracker(c);
        String location="";
        Geocoder geo=new Geocoder(c);
        try {
            List<Address> adr= geo.getFromLocation(latLng.latitude,latLng.longitude,100);
            if(adr.size()>0){

                Address ad=adr.get(0);
                location=ad.getFeatureName();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }




        return location;
    }
}
