package cardapp.henrique.com.br.cwcourier;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import cardapp.henrique.com.br.cwcourier.network.NetworkConnection;
import cardapp.henrique.com.br.cwcourier.pojo.Corrida;
import cardapp.henrique.com.br.cwcourier.pojo.Entregador;
import io.realm.Realm;
import io.realm.RealmObject;

public class NovaCorrida extends AppCompatActivity {

    private String frete;
    private Button recusar;
    private Button aceitar;
    private Realm realm;
    private Entregador entregador;
    private long id_entregador;
    private Gson gson;
    private Corrida corrida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_corrida);

        realm = Realm.getInstance(this);
        realm.beginTransaction();
        entregador = realm.where(Entregador.class).findFirst();
        id_entregador = entregador.getId();

        recusar = (Button) findViewById(R.id.btnRecusar);
        aceitar = (Button) findViewById(R.id.btnAceitar);

        //Recupera json para criar o realm Object da Nova Corrida
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            frete = extras.getString("frete");
        }

        //Configura Gson para trabalhar com Realm
        gson = new GsonBuilder()
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

    }

    public void novaCorridaAction(View v){
        int id = v.getId();
        if(id == recusar.getId()){
            finish();
        }
        else if(id == aceitar.getId()) {
            armazenaCorrida();



            Intent it = new Intent(this, DetalhesCorrida.class);
            it.putExtra("id", corrida.getId());
            startActivity(it);
            finish();
        }
    }

    public void armazenaCorrida(){

        corrida = gson.fromJson(frete, Corrida.class);
        realm.copyToRealmOrUpdate(corrida);
        realm.commitTransaction();
    }

    public boolean aceitaCorrida(){
        /*
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
        */
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}