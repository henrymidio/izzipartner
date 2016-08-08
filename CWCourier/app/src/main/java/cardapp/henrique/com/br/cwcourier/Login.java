package cardapp.henrique.com.br.cwcourier;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cardapp.henrique.com.br.cwcourier.network.NetworkConnection;
import cardapp.henrique.com.br.cwcourier.pojo.Entregador;
import io.realm.Realm;
import io.realm.RealmObject;


public class Login extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String URL = "http://cardappweb.com/motofrete/motoqueiro/webservice/autentica-courier.php";
    private EditText login;
    private EditText senha;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPlayServices();

        login = (EditText) findViewById(R.id.etLogin);
        senha = (EditText) findViewById(R.id.etSenha);

    }

    public void logar(View view) {

        // Instantiate the RequestQueue.
        NetworkConnection nc = NetworkConnection.getInstance(getApplicationContext());
        RequestQueue queue = nc.getRequestQueue();

        // Request a string response from the provided URL.
        StringRequest sr = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //Recupera o JSON e converte em objeto
                            Gson gson = new GsonBuilder()
                                    .setExclusionStrategies(new ExclusionStrategy() {
                                        @Override
                                        public boolean shouldSkipField(FieldAttributes f) {
                                            return f.getDeclaringClass().equals(RealmObject.class);
                                        }

                                        @Override
                                        public boolean shouldSkipClass(Class<?> clazz) {
                                            return false;
                                        }
                                    })
                                    .create();

                            Entregador entregador = gson.fromJson(response, Entregador.class);

                            //Grava o objeto no Realm
                            Realm realm = Realm.getInstance(Login.this);
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(entregador);
                            realm.commitTransaction();
                            realm.close();

                            //Salva nas preferências para continuar logado
                            SharedPreferences settings = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("logged", true);
                            editor.commit();

                            Intent serv = new Intent(Login.this, RegistrationIntentService.class);
                            startService(serv);

                            Intent it = new Intent(Login.this, MapsActivity.class);
                            startActivity(it);

                            pd.dismiss();

                            finish();

                        } catch (com.google.gson.JsonSyntaxException ex) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Erro de autenticação", Toast.LENGTH_LONG).show();
                            Log.i("Error", ex.getMessage());

                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Servidor não encontrado", Toast.LENGTH_LONG).show();

            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String username = login.getText().toString().trim();
                String password = senha.getText().toString().trim();

                params.put("login", username);
                params.put("senha", password);

                return params;
            }
        };

        queue.add(sr);
        pd = ProgressDialog.show(this, "Aguarde...", "", true);

    }


    private boolean checkPlayServices() {
        final GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

                finish();
            }
            return false;
        }
        return true;
    }



}
