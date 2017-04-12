package mg.blueline.gulfsat.argusfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
import mg.blueline.gulfsat.argusfinder.adapters.DocumentAdapter;
import mg.blueline.gulfsat.argusfinder.login.LoginActivity;
import mg.blueline.gulfsat.argusfinder.myObjectModel.Document;
import mg.blueline.gulfsat.argusfinder.session.SessionManagement;




public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    String URL_SERVER="http://192.168.43.150/droid/listdoc.php";
    ActionBar actionBar;


    ProgressDialog pd;
    ListView v;

    //  String[] str;
    JSONArray ar;
    ViewFlipper flip;

    List<Document> docs;
    ImageView imageView1;

    DrawerLayout lay;

    TextView tv1,tv2;
    Thread th;
    String textS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        v=(ListView) findViewById(R.id.recu);
        pd=new ProgressDialog(this);
        pd.setCancelable(true);

        Bundle b= this.getIntent().getExtras();

        tv1=(TextView) findViewById(R.id.tvName);
        tv1.setText(b.getString("name", null));
        tv2=(TextView) findViewById(R.id.tvMail);
        tv2.setText(b.getString("mail",null));



        flip =(ViewFlipper) findViewById(R.id.flips);


        flip.setFlipInterval(1000);

        flip.startFlipping();


        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        setupNavigationDrawerContent(navigationView);


       // getMyData();
        Map<String,String> para= new HashMap<>();
        SharedPreferences pr= PreferenceManager.getDefaultSharedPreferences(this);
        Double x=Double.valueOf(pr.getString(SessionManagement.SESSION_LONG, null));
        Double y = Double.valueOf(pr.getString(SessionManagement.SESSION_LAT, null));

        LatLng lng = new LatLng(y,x);
        String last=getGeocodedLocation(lng,this);
        para.put("username", pr.getString(SessionManagement.SESSION_USERNAME, null));
        para.put("long", pr.getString(SessionManagement.SESSION_LONG, null));
        para.put("lat",pr.getString(SessionManagement.SESSION_LAT,null));

        Log.i("yasser",lng.toString());

        para.put("last",last);
        Log.i("yasser","last"+last);
        if(last==null){
            para.put("tag", "no");
            Log.i("yasser", "emplacement introuvable");
            textS="You have no internet access";
        }
        else {
            para.put("tag", "update");
            Log.i("yasser", "emplacement :" + last);
            textS="You have internet access";


        }



        lay=(DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        Snackbar snackbar = Snackbar
                .make(lay,textS, Snackbar.LENGTH_INDEFINITE)
                .setAction("Hide", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });


        snackbar.setActionTextColor(Color.RED);


        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        //Button bt = sbView.findViewById();

            snackbar.show();
            submitUserLocation(para, this);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }











    private void refreshDataList() {
        ArrayAdapter adapter= new DocumentAdapter(MainActivity.this,docs);
        v.setAdapter(adapter);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_drawer_home:
                                menuItem.setChecked(true);

                                getSupportActionBar().setTitle(menuItem.getTitle());
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;

                            case R.id.item_navigation_drawer_my_docs:
                                menuItem.setChecked(true);
                                Intent x = new Intent(MainActivity.this,DocumentListActivity.class);
                                startActivity(x);

                                /*getMyData();
                                refreshDataList();*/
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_user_lists:
                                menuItem.setChecked(true);
                                getAllUsers();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_settings:
                                menuItem.setChecked(true);

                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.item_navigation_drawer_log_out:
                                menuItem.setChecked(true);
                                pd.setMessage("Logging out...");
                                pd.show();
                                SessionManagement ses=new SessionManagement(MainActivity.this);
                                ses.setLogout(MainActivity.this);
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                pd.dismiss();
                                startActivity(i);
                                finish();

                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                        return true;
                    }
                });

    }

    private void getAllUsers() {

        Intent i = new Intent(MainActivity.this,UserListActivity.class);
        startActivity(i);
    }



//geo code




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

















































    private void getMyData() {
        StringRequest request=new StringRequest(Request.Method.POST, URL_SERVER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                try {
                    pd.setMessage("Retrieving data from server");
                    ar=new JSONArray(s);
                    docs=new ArrayList<>();
                    //str=new String[ar.length()];
                    for (int i=0;i<ar.length();i++)
                    {

                        JSONObject object = ar.getJSONObject(i);
                        Document doc= new Document();

                        doc.setCreator(object.getString("creator"));
                        doc.setName(object.getString("name"));
                        String date=object.getString("date");
                        date=date.replace("T", " à ");
                        date=date.substring(0, 21);
                        doc.setDate(date);
                        doc.setExtension();


                        if (doc.getCreator().equals("System") ||
                                doc.getCreator().equals("abeecher") ||
                                doc.getCreator().equals("mjackson") ||
                                doc.getCreator().equals("admin") ||
                                doc.getName().contains("dashboard.xml")

                                ) {

                        }else {
                            SharedPreferences preferences;
                            preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            if(doc.getCreator().equals(preferences.getString("username",null))){
                                docs.add(doc);

                            }
                        }

                        refreshDataList();

                    }
                    Log.i("yasser", "" + docs.size());


                    refreshDataList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.setMessage("Done!!!!");
                pd.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("tag","listdoc");

                return params;
            }
        }

                ;


        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }
    private void getAllData() {
        StringRequest request=new StringRequest(Request.Method.POST, URL_SERVER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                try {
                    pd.setMessage("Retrieving data from server");
                    ar=new JSONArray(s);
                    //str=new String[ar.length()];
                    docs=new ArrayList<>();
                    for (int i=0;i<ar.length();i++)
                    {

                        JSONObject object = ar.getJSONObject(i);
                        Document doc= new Document();

                        doc.setCreator(object.getString("creator"));
                        doc.setName(object.getString("name"));
                        String date=object.getString("date");
                        date=date.replace("T", " à ");
                        date=date.substring(0, 21);
                        doc.setDate(date);
                        doc.setExtension();


                        if (doc.getCreator().equals("System") ||
                                doc.getCreator().equals("abeecher") ||
                                doc.getCreator().equals("mjackson") ||
                                doc.getCreator().equals("admin") ||
                                doc.getName().contains("dashboard.xml")
                                ) {
                            refreshDataList();
                        }else {
                            refreshDataList();
                            docs.add(doc);
                        }

                        refreshDataList();
                    }
                    Log.i("yasser", "" + docs.size());




                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.setMessage("Done!!!!");
                pd.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("tag","listdoc");

                return params;
            }
        }

                ;


        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        Log.i("yasser","il veut retourner");
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            Log.i("yasser", "emplacement introuvable");
            textS="You have no internet access";
        }
        else {
            para.put("tag", "update");
            Log.i("yasser", "emplacement :" + last);
            textS="You have internet access";


        }

        submitUserLocation(para,this);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
