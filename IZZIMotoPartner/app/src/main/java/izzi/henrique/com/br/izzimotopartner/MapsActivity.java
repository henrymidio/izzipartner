package izzi.henrique.com.br.izzimotopartner;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import izzi.henrique.com.br.izzimotopartner.network.NetworkConnection;
import izzi.henrique.com.br.izzimotopartner.pojo.Entregador;

public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GoogleApiClient mGoogleApiClient;

    private boolean loggedIn;
    private ImageView ivFoto;
    private TextView tvNome;
    private TextView tvStatus;
    private Button btnEntrar;
    private Button btnSair;
    private MarkerOptions mo;
    private Entregador entregador;
    Realm realm;
    String nome;
    String foto;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        //Verifica se o usuários está logado no APLICATIVO
        SharedPreferences sp = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        loggedIn = sp.getBoolean("logged", false);

        if(!loggedIn){
            Intent it = new Intent(this, Login.class);
            startActivity(it);
            finish();
        }
        else {
            //ESTANDO LOGADO
            //Seta o Toolbar
            Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            //Inicializa as variáveis de exibição
            ivFoto = (ImageView) findViewById(R.id.ivFoto);
            tvNome = (TextView) findViewById(R.id.tvNome);
            tvStatus = (TextView) findViewById(R.id.tvStatus);
            btnEntrar = (Button) findViewById(R.id.btnEntrar);
            btnEntrar.setTextColor(Color.WHITE);
            btnSair = (Button) findViewById(R.id.btnSair);
            btnSair.setTextColor(Color.WHITE);
            mo = null;

            setaInformacoesUser();

            // Create an instance of GoogleAPIClient.
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        realm.close();
        super.onStop();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.corridas:
                Intent it = new Intent(this, Corridas.class);
                startActivity(it);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpMap() {
        try {
            mMap.setMyLocationEnabled(true);

        } catch (SecurityException se){
            se.printStackTrace();
        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public void setaInformacoesUser(){

        realm = Realm.getInstance(this);
        entregador = realm.where(Entregador.class).findFirst();

        if(entregador.isValid()) {
            nome = entregador.getNome();
            foto = entregador.getFoto();
            id   = entregador.getId();

            //Verifica status
            alteraBotaoLogin(entregador.isStatus());

            // Retrieves an image specified by the URL, displays it in the UI.
            NetworkConnection.setImageRequest(getApplicationContext(), foto, ivFoto);

            tvNome.setText(nome);

        }

    }

    public void alteraBotaoLogin(boolean status){
        if(status){
            btnEntrar.setVisibility(Button.GONE);
            btnSair.setVisibility(Button.VISIBLE);
            tvStatus.setText("ONLINE");

            //Altera status do user
            realm.beginTransaction();
            entregador.setStatus(true);
            realm.commitTransaction();

        } else {
            btnEntrar.setVisibility(Button.VISIBLE);
            btnSair.setVisibility(Button.GONE);
            tvStatus.setText("OFFLINE");

            //Altera status do user
            realm.beginTransaction();
            entregador.setStatus(false);
            realm.commitTransaction();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            LatLng coordenadas = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            //Atualiza posição
            Entregador.updateLocation(getApplicationContext(), id, mLastLocation.getLatitude(), mLastLocation.getLongitude());

            if (mLastLocation != null && mo == null) {
                mo = new MarkerOptions().position(coordenadas).title("Localização atual");
                mMap.addMarker(mo);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null);

            }
        } catch (SecurityException se){
            se.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Tratamento do clique nos botões de logar/sair
    public void login(View view){
        int v = view.getId();
        int newState = 0;
        if(v == R.id.btnEntrar){
            newState = 1;
        }
        else if(v == R.id.btnSair){
            newState = 0;
        }

        // Instantiate the RequestQueue.
        NetworkConnection nc = NetworkConnection.getInstance(getApplicationContext());
        RequestQueue queue = nc.getRequestQueue();
        String path = "http://cardappweb.com/motofrete/motoqueiro/webservice/update-status.php";

        //Apenas para deixar acessível dentro da passagem de parâmetros
        final long idCourier = id;
        final int novoStatus = newState;

        // Request a string response from the provided URL.
        StringRequest sr = new StringRequest(Request.Method.POST, path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       if(response.trim().equals("1")){
                           alteraBotaoLogin(true);
                       }
                        else {
                           alteraBotaoLogin(false);
                       }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Err", error.getMessage());
            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(idCourier));
                params.put("status", String.valueOf(novoStatus));
                return params;
            }
        };

        queue.add(sr);
    }


}
