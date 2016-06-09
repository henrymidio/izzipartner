package cardapp.henrique.com.br.cwcourier;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.util.HashMap;
import java.util.Map;

import cardapp.henrique.com.br.cwcourier.network.NetworkConnection;
import cardapp.henrique.com.br.cwcourier.pojo.Entregador;
import cardapp.henrique.com.br.cwcourier.services.TrackService;
import io.realm.Realm;

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
    String nome;
    String foto;
    long id;
    private Intent tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();


        SharedPreferences sp = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        loggedIn = sp.getBoolean("logged", false);

        //Verifica se o usuários está logado no APLICATIVO e caso não esteja envia para tela de login
        if (!loggedIn) {
            Intent it = new Intent(this, Login.class);
            startActivity(it);
            finish();
        } else {
            //ESTANDO LOGADO
            //Seta o Toolbar
            Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            //Seta Material Drawer
            setMaterialDrawer(tb);

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

        } catch (SecurityException se) {
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

    public void setaInformacoesUser() {

        Realm realm = Realm.getInstance(this);
        entregador = realm.where(Entregador.class).findFirst();

        if (entregador.isValid()) {
            nome = entregador.getNome();
            foto = entregador.getFoto();
            id = entregador.getId();

            //Verifica status
            alteraBotaoLogin(entregador.isStatus());

            // Retrieves an image specified by the URL, displays it in the UI.
            NetworkConnection.setImageRequest(getApplicationContext(), foto, ivFoto);

            tvNome.setText(nome);

        }

        realm.close();

    }

    public void alteraBotaoLogin(boolean status) {
        if (status) {
            btnEntrar.setVisibility(Button.GONE);
            btnSair.setVisibility(Button.VISIBLE);
            tvStatus.setText("ONLINE");

            //Altera status do user
            Realm realm = Realm.getInstance(this);
            entregador = realm.where(Entregador.class).findFirst();
            realm.beginTransaction();
            entregador.setStatus(true);
            realm.commitTransaction();
            realm.close();


        } else {
            btnEntrar.setVisibility(Button.VISIBLE);
            btnSair.setVisibility(Button.GONE);
            tvStatus.setText("OFFLINE");

            //Altera status do user
            Realm realm = Realm.getInstance(this);
            entregador = realm.where(Entregador.class).findFirst();
            realm.beginTransaction();
            entregador.setStatus(false);
            realm.commitTransaction();
            realm.close();
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
        } catch (SecurityException se) {
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
    public void login(View view) {
        int v = view.getId();
        int newState = 0;
        if (v == R.id.btnEntrar) {
            newState = 1;
            //Inicia o service de rastreamento
            /*
            tracker = new Intent(this, TrackService.class);
            tracker.setAction("TRACK");
            tracker.putExtra("id", id);
            startService(tracker);
            */
        } else if (v == R.id.btnSair) {
            newState = 0;
            //Interrompe o service de rastreamento

            tracker = new Intent(this, TrackService.class);
            tracker.setAction("TRACK");
            stopService(tracker);

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

                        if (response.trim().equals("1")) {
                            alteraBotaoLogin(true);
                        } else {
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

    public void setMaterialDrawer(Toolbar tb){
        Realm realm = Realm.getInstance(this);
        entregador = realm.where(Entregador.class).findFirst();

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                // Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                NetworkConnection.setImageRequest(getApplicationContext(), uri.toString(), imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                // Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

        });

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.fundo_lollip)
                .addProfiles(
                        new ProfileDrawerItem().withName(entregador.getNome()).withEmail("sac@corrertec.com.br")
                                .withIcon(entregador.getFoto())
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(tb)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home_black_24dp),
                        new PrimaryDrawerItem().withName("Extrato").withIcon(R.drawable.ic_date),
                        new PrimaryDrawerItem().withName("Novidades").withIcon(R.drawable.ic_add_alert_black),
                        new PrimaryDrawerItem().withName("Indique").withIcon(R.drawable.ic_favorite_black),
                        new PrimaryDrawerItem().withName("Ajuda").withIcon(R.drawable.ic_help)
                        //new SecondaryDrawerItem().withName("SAIR").withIcon(R.drawable.ic_input_black_48dp)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return true;
                    }
                })
                .build();
        result.addStickyFooterItem(new SecondaryDrawerItem().withName("SAIR").withIcon(R.drawable.ic_input_black_48dp));

        realm.close();
    }


}
