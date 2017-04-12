package mg.blueline.gulfsat.argusfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.blueline.gulfsat.argusfinder.GPSTrackers.GPSTracker;
import mg.blueline.gulfsat.argusfinder.session.SessionManagement;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    ProgressDialog pb;
    Double lat, lon;
    GPSTracker gps;
    Map<String,String> para;

    public static final String SERVER_URL="http://192.168.43.150/droid/updatel.php";

        LatLng lt;
        String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps=new GPSTracker(MapsActivity.this);
        pb = new ProgressDialog(this);
        pb.setMessage("Chargement de la carte");
        pb.show();

        Bundle b = getIntent().getExtras();
        Double a= b.getDouble("lat");
        Double c=b.getDouble("long");
        name=b.getString("hisName");
        lt=new LatLng(a,c);

         para= new HashMap<>();
        SharedPreferences pr= PreferenceManager.getDefaultSharedPreferences(this);
        Double x=Double.valueOf(pr.getString(SessionManagement.SESSION_LONG, null));
        Double y = Double.valueOf(pr.getString(SessionManagement.SESSION_LAT, null));

        LatLng lng = new LatLng(y,x);
        String last=getGeocodedLocation(lng,this);
        para.put("username", pr.getString(SessionManagement.SESSION_USERNAME, null));
        para.put("long", pr.getString(SessionManagement.SESSION_LONG, null));
        para.put("lat",pr.getString(SessionManagement.SESSION_LAT,null));
        para.put("last",last);
        para.put("tag", "update");





    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

      if (gps.canGetLocation()){
            lat=gps.getLatitude();
            lon=gps.getLongitude();
        }
        Log.i("GPS", "longitude=" + lon + "// latitude=" + lat);

        LatLng me = new LatLng(lat, lon);
        Marker mark=mMap.addMarker(new MarkerOptions()
                .position(lt)
                .title("Vous")
                .snippet(getGeocodedLocation(me,this)));
        mark.showInfoWindow();

        Marker marker=mMap.addMarker(new MarkerOptions()
                .position(me)
                .title(name)
                .snippet(getGeocodedLocation(lt,this)));
        marker.showInfoWindow();
       // mark.setSnippet(getGeocodedLocation(lt,this));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
        UiSettings settings = mMap.getUiSettings();

        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setMapToolbarEnabled(true);

        settings.setZoomControlsEnabled(true);
        //settings.setAllGesturesEnabled(false);
        settings.setRotateGesturesEnabled(true);
        settings.setTiltGesturesEnabled(true);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);

        mMap.setMyLocationEnabled(true);
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 19), 2000, null);
        pb.setMessage("Terminée");
        pb.dismiss();


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                marker.getPosition(), 16));
/*
        mMap.addMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f)
                .position(marker.getPosition()));*/

        submitUserLocation(para,this);
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

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    public double getOurDistance(){

        return 0;
    }

}
