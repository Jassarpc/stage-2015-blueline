package mg.blueline.gulfsat.argusfinder.register;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import mg.blueline.gulfsat.argusfinder.R;
import mg.blueline.gulfsat.argusfinder.login.LoginActivity;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputrPassword;
        StringRequest request;
    private EditText inputUsername;
    private ProgressBar pb;
    private String name, username, rpassword,password, mail;
    private boolean isOnAlfresco=false,isPasswordMatched;
    private RequestQueue quee;
    private ProgressDialog pDialog;
    private Dialog d;
    private TextView notif,noti;
    public String mailI="",fullI="";
    private String us;
    RequestQueue queue;
    JSONObject object = null;
    private  boolean state;
    TextView avail;
    JSONArray array;
    private static final String SERVER_URL = "http://192.168.43.150/droid/";
    private String p1,p2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

try{        setContentView(R.layout.register);}
catch (Exception e){
    Log.i("yasser",e.getMessage());
}
        notif=(TextView) findViewById(R.id.notif);
        noti=(TextView) findViewById(R.id.noti);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        inputUsername = (EditText) findViewById(R.id.username);
        pb=(ProgressBar) findViewById(R.id.progressBar);
        inputrPassword = (EditText) findViewById(R.id.rpassword);
        inputFullName.setEnabled(false);
        inputFullName.setTextColor(Color.WHITE);
        inputEmail.setEnabled(false);
        inputEmail.setTextColor(Color.WHITE);
        avail= (TextView) findViewById(R.id.availability);
        d = new Dialog(this);
        btnRegister.setOnClickListener(this);

        btnRegister.setEnabled(false);
        btnLinkToLogin.setOnClickListener(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        inputUsername.addTextChangedListener(new TextWatcher() {
                                                 @Override
                                                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                     queue = Volley.newRequestQueue(RegisterActivity.this);
                                                 }

                                                 @Override
                                                 public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                     pb.setVisibility(View.VISIBLE);
                                                     Log.i("yasser", "requet envoi");

                                                     request=new StringRequest(Request.Method.POST, "http://192.168.43.150/droid/listuser.php",
                                                             new Response.Listener<String>() {
                                                                 @Override
                                                                 public void onResponse(String c) {
                                                                     Log.i("yasser",c);
                                                                     try {
                                                                         array=new JSONArray(c);
                                                                         Log.i("yasser",""+array.length());
                                                                         for(int i=0; i<array.length();i++) {
                                                                             object = array.getJSONObject(i);

                                                                             Log.i("yasser",""+array.length());

                                                                             if (object.getString("username").toString().equals(inputUsername.getText().toString())) {
                                                                                 isOnAlfresco = true;
                                                                                 fullI = object.getString("first") + " " + object.getString("last");
                                                                                 mailI = object.getString("mail");
                                                                                 btnRegister.setClickable(true);
                                                                                 Log.i("yasser", "vrai" + " objet:" + object.getString("username") + " input:" + inputUsername.getText().toString());
                                                                                 inputEmail.setText(mailI);
                                                                                 inputFullName.setText(fullI);
                                                                                 avail.setTextColor(Color.YELLOW);

                                                                                 notif.setText(getResources().getString(R.string.already));
                                                                                 notif.setTextColor(Color.GREEN);
                                                                                 pb.setVisibility(View.INVISIBLE);
                                                                                 break;
                                                                             } else {
                                                                                 Log.i("yasser", "faux" + " objet:" + object.getString("username") + " input:" + inputUsername.getText().toString());
                                                                                 inputEmail.setText("");
                                                                                 inputFullName.setText("");

                                                                                     notif.setText(getResources().getString(R.string.alreadyonalfresco));
                                                                                     notif.setTextColor(Color.RED);
                                                                                     isOnAlfresco = false;
                                                                             }
                                                                         }

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
                                                             Map<String, String> params = new HashMap<String, String>();
                                                             params.put("tag","userlist");


                                                             return params;
                                                         }
                                                     };


                                                 }

                                                 @Override
                                                 public void afterTextChanged(Editable s) {



                                                     queue.add(request);


                                                     Log.i("yasser", "after");

                                                     if (isOnAlfresco) {

                                                         notif.setText(getResources().getString(R.string.already));
                                                     } else {
                                                         notif.setText(getResources().getString(R.string.alreadyonalfresco));

                                                     }

                                                 }


                                             }


        );



        inputrPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                p1=inputPassword.getText().toString().trim();
                p2=inputrPassword.getText().toString().trim();
                pb.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!(p1.length()<6)){
                    if (!p1.equals(p2)){


                        isPasswordMatched=false;
                        noti.setText(getResources().getString(R.string.notmatched));
                        noti.setTextColor(Color.RED);


                    }
                    else {
                        noti.setTextColor(Color.GREEN);
                        noti.setText(getResources().getString(R.string.matched));
                        isPasswordMatched=true;
                        pb.setVisibility(View.INVISIBLE);

                    }

                }else {
                    noti.setTextColor(Color.RED);
                    noti.setText(getResources().getString(R.string.passmin));
                }
                if (!isOnAlfresco || !isPasswordMatched) {
                    btnRegister.setEnabled(false);
                    pb.setVisibility(View.INVISIBLE);
                } else {
                    btnRegister.setEnabled(true);
                }

            }
        });
        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                p1=inputPassword.getText().toString().trim();
                p2=inputrPassword.getText().toString().trim();
                pb.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!(p1.length()<6)){

                    if (!p1.equals(p2)){

                        noti.setTextColor(Color.RED);
                        isPasswordMatched=false;
                        noti.setText(getResources().getString(R.string.notmatched));



                    }
                    else {
                        noti.setTextColor(Color.GREEN);
                        noti.setText(getResources().getString(R.string.matched));
                        isPasswordMatched=true;
                        pb.setVisibility(View.INVISIBLE);

                    }
                }else {
                    noti.setTextColor(Color.RED);
                    noti.setText(getResources().getString(R.string.passmin));
                    if (!isOnAlfresco || !isPasswordMatched ) {
                        btnRegister.setEnabled(false);
                        pb.setVisibility(View.INVISIBLE);
                    } else {
                        btnRegister.setEnabled(true);
                    }
                }

            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnRegister:

                name = inputFullName.getText().toString().trim();
                mail = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                rpassword = inputrPassword.getText().toString().trim();
                username = inputUsername.getText().toString().trim();

                pDialog.setMessage("Envoi de la requete au serveur");
                pDialog.show();
                StringRequest request = new StringRequest(Request.Method.POST, SERVER_URL,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String s) {
                                pDialog.setMessage("Parsing du reponse de serveur");
                                try {
                                    JSONObject object = new JSONObject(s);
                                    Log.i("yasser", "eto iz zio");
                                    boolean error = object.getBoolean("error");


                                    pDialog.dismiss();
                                    if (error) {
                                        if(object.getBoolean("etat")==false){
                                            avail.setTextColor(Color.YELLOW);
                                            avail.setText("Username not available");
                                        }
                                        Toast.makeText(RegisterActivity.this, object.getString("msg"), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, object.getString("msg"), Toast.LENGTH_LONG).show();


                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();


                                    Toast.makeText(RegisterActivity.this, "JSON ato:" + e.toString(), Toast.LENGTH_LONG).show();
                                } finally {

                                }

                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        pDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", name);
                        params.put("password", password);
                        params.put("username", username);
                        params.put("mail", mail);
                        params.put("tag", "register");
                        return params;
                    }
                };


                quee = Volley.newRequestQueue(this);
                quee.add(request);
                break;

            case R.id.btnLinkToLoginScreen:
                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                break;

        }
    }

    private boolean isAlreadyInAlfresco(String username){
        return true;
    }}



/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/




