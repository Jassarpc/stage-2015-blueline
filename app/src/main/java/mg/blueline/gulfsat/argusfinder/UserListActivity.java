package mg.blueline.gulfsat.argusfinder;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.blueline.gulfsat.argusfinder.GPSTrackers.GPSTracker;
import mg.blueline.gulfsat.argusfinder.adapters.UserAdapter;
import mg.blueline.gulfsat.argusfinder.myObjectModel.User;
import mg.blueline.gulfsat.argusfinder.session.SessionManagement;

/**
 * Created by hamidullah on 10/3/15.
 */
public class UserListActivity extends ListActivity {

    String textS;
    ProgressDialog pd;
    JSONArray ar;
    List<User> users;
    JSONObject object ;
    User user;
    ArrayAdapter adapter;
    RequestQueue q;
    SharedPreferences pref;
    String sesUs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getActionBar().show();
      q = Volley.newRequestQueue(this);
        pd= new ProgressDialog(this);
        users=new ArrayList<>();
        pd.setMessage("Sending request");
        pd.setCancelable(false);
        pd.show();
        getBudyList();
        Log.i("yasser", " Total :" + users.size());
        refreshUserList();
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        sesUs=pref.getString(SessionManagement.SESSION_USERNAME,null);
        Map<String,String> para= new HashMap<>();
        SharedPreferences pr= PreferenceManager.getDefaultSharedPreferences(this);
        Double x=Double.valueOf(pr.getString(SessionManagement.SESSION_LONG, null));
        Double y = Double.valueOf(pr.getString(SessionManagement.SESSION_LAT, null));

        LatLng lng = new LatLng(y,x);
        String last=getGeocodedLocation(lng,this);
        para.put("username", pr.getString(SessionManagement.SESSION_USERNAME, null));
        para.put("long", pr.getString(SessionManagement.SESSION_LONG, null));
        para.put("lat",pr.getString(SessionManagement.SESSION_LAT,null));

        para.put("last",last);
        if(last.equals("")){
            para.put("tag", "no");
            Log.i("yasser","emplacement introuvable");
            textS="You have no internet access";
        }
        else {
            para.put("tag", "update");
            Log.i("yasser", "emplacement :"+last);
            textS="You have internet access";


        }


        submitUserLocation(para,this);
    }

    private void refreshUserList() {
        adapter= new UserAdapter(UserListActivity.this,users);
        setListAdapter(adapter);
    }

    private void getBudyList() {



        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.43.150/droid/user.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            pd.setMessage("Retrieving data from server");
                            ar=new JSONArray(s);


                            int i=0;

                           while (i<(ar.length()-1))
                            {
                                if(i==ar.length()){ Log.e("yasser", "lenamboarazagn");}
                                object = ar.getJSONObject(i);
                                user = new User();
                                user.setName(object.getString("name"));
                                user.setHd(object.getString("hd"));
                                user.setLastLocation(object.getString("last_location"));
                                user.setLongitude(Double.parseDouble(object.getString("last_long")));
                                user.setLatitude(Double.parseDouble(object.getString("last_lat")));
                                user.setUsername(object.getString("username"));
                                user.setImage("x");
                                user.setMail(object.getString("mail"));

                                if(!object.getString("username").equals(sesUs)){
                                users.add(user);}

                                Log.e("yasser", "Taille" + users.size());
                                Log.e("yasser", "" + user.getHd());
                                Log.e("yasser",""+user.getUsername());
                                Log.e("yasser",""+user.getLatitude());
                                Log.e("yasser",""+user.getLongitude());
                                Log.e("yasser",""+user.getLastLocation());
                                Log.e("yasser",""+user.getMail());
                                Log.e("yasser",""+user.getName());
                                Log.e("yasser",""+user.getImage());
                                Log.e("i =",""+i);
                                Log.e("ar.length()= ",""+ar.length());
                                i++;
                                refreshUserList();
                            }
                            pd.setMessage("done!!!");
                            pd.dismiss();
                            Log.e("yasser","tay");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        )
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("tag","user");

                return params;
            }
        }

                ;
        q.add(request);



    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        User us= users.get(position);
        Intent i = new Intent(UserListActivity.this, MapsActivity.class);
        String n=us.getName();
        Double  lon=us.getLongitude();
        Double  lat=us.getLatitude();
       // google.maps.geometry.spherical.computeDistanceBetween (latLngA, latLngB);

        Bundle bund = new Bundle();
        bund.putDouble("long",lon);
        bund.putDouble("lat",lat);
        bund.putString("hisName",n);
        i.putExtras(bund);
        startActivity(i);

        Log.i("yasser", "Username" + users.get(0).getName());
        Log.i("yasser", "us" + users.get(position).getName());
    }

    @Override
    protected void onPause() {
        super.onPause();

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


}
