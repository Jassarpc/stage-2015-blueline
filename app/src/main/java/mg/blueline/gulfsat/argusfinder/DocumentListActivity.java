package mg.blueline.gulfsat.argusfinder;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.blueline.gulfsat.argusfinder.adapters.DocumentAdapter;
import mg.blueline.gulfsat.argusfinder.myObjectModel.Document;

public class DocumentListActivity extends AppCompatActivity {

    String URL_SERVER="http://192.168.43.150/droid/listdoc.php";



    ProgressDialog pd;
    ListView v;
    JSONArray ar;

    List<Document> docs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        v=(ListView) findViewById(R.id.recu);
        pd=new ProgressDialog(this);
        pd.setCancelable(true);
        pd.setMessage("Sending request to the server");
        pd.show();
        getMyData();




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Actualisation reussi", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
                            preferences = PreferenceManager.getDefaultSharedPreferences(DocumentListActivity.this);
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


        RequestQueue queue= Volley.newRequestQueue(DocumentListActivity.this);
        queue.add(request);
    }
    private void refreshDataList() {
        ArrayAdapter adapter= new DocumentAdapter(DocumentListActivity.this,docs);
        v.setAdapter(adapter);
    }
}
