package mg.blueline.gulfsat.argusfinder.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.blueline.gulfsat.argusfinder.MainActivity;
import mg.blueline.gulfsat.argusfinder.R;
import mg.blueline.gulfsat.argusfinder.register.RegisterActivity;
import mg.blueline.gulfsat.argusfinder.session.SessionManagement;


public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private RequestQueue myQuee;
    private List<String > info;

    private static final String SERVER_URL="http://192.168.43.150/droid/";

    private Dialog dialog;
    public SessionManagement session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);



        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        dialog= new Dialog(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        session=new SessionManagement(this);


        if (session.isLoggedIn()){

            Toast.makeText(LoginActivity.this , "One session is already running", Toast.LENGTH_LONG);
            Intent x = new Intent(LoginActivity.this,MainActivity.class);

            Bundle b = new Bundle();
            info=session.getUserSession();
            b.putString("username",info.get(0));
            b.putString("password", info.get(1));
            b.putDouble("lat", Double.parseDouble(info.get(2)));
            b.putDouble("long", Double.parseDouble(info.get(3)));
            b.putString("mail",info.get(4));
            b.putString("name", info.get(5));
            x.putExtras(b);
            startActivity(x);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();


                if (username.trim().length() > 0 && password.trim().length() > 0) {

                    checkLogin(username, password);
                } else {

                    Toast.makeText(getApplicationContext(),
                            R.string.credentials, Toast.LENGTH_LONG)
                            .show();
                }
            }

        });


        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void checkLogin(final String username, final String password) {

          myQuee= Volley.newRequestQueue(this);
        pDialog.setMessage(getResources().getString(R.string.loginin));
         showDialog();

        StringRequest request = new StringRequest(Request.Method.POST, SERVER_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String s) {

                        pDialog.setMessage(getResources().getString(R.string.parse));
                        try {
                            JSONObject object = new JSONObject(s);
                            boolean error=object.getBoolean("error");

                            if (!error){
                                String name=object.getString("name");
                                String mail=object.getString("mail");
                                String lat=object.getString("lat");
                                String lon=object.getString("long");


                                session.setLogin(true, username, password,name,mail,lat,lon);

                                Toast.makeText(LoginActivity.this,object.getString("msg"),Toast.LENGTH_LONG).show();


                                Intent x = new Intent(LoginActivity.this,MainActivity.class);

                                Bundle b = new Bundle();
                                info=session.getUserSession();
                                b.putString("username",info.get(0));
                                b.putString("password", info.get(1));
                                b.putDouble("lat", Double.parseDouble(info.get(2)));
                                b.putDouble("long",Double.parseDouble(info.get(3)));
                                b.putString("mail", info.get(4));
                                b.putString("name",info.get(5));
                                x.putExtras(b);

                                startActivity(x);

                            }else{
                                Toast.makeText(LoginActivity.this,object.getString("msg"),Toast.LENGTH_LONG).show();

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
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String , String> params = new HashMap<>();
                params.put("username",username);
                params.put("password",password);
                params.put("tag","login");
                return params;
            }
        };

        myQuee.add(request);

    }





    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }



}
