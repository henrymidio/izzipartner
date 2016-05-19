package cardapp.henrique.com.br.cwcourier;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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


    }

    @Override
    protected void onResume() {
        super.onResume();
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

        //Criar Realm Object da nova corrida para armazenamento
        corrida = gson.fromJson(frete, Corrida.class);
        //Log.i("FRETE", corrida.getTrajeto().get(0).getEndereco());

    }

    public void novaCorridaAction(View v){
        int id = v.getId();
        if(id == recusar.getId()){
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancelAll();
            finish();
        }
        else if(id == aceitar.getId()) {
            aceitaCorrida();
        }
    }

    public void armazenaCorrida(){

        realm.copyToRealmOrUpdate(corrida);
        realm.commitTransaction();
    }

    public void aceitaCorrida(){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

        // Instantiate the RequestQueue.
        NetworkConnection nc = NetworkConnection.getInstance(getApplicationContext());
        RequestQueue queue = nc.getRequestQueue();
        String url = "http://cardappweb.com/motofrete/motoqueiro/confirma-entrega.php?motofrete_id="+corrida.getId()+"&id_entregador="+id_entregador;

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.startsWith("Suc")) {
                    armazenaCorrida();

                    Intent it = new Intent(NovaCorrida.this, DetalhesCorrida.class);
                    it.putExtra("id", corrida.getId());
                    startActivity(it);
                    finish();
                } else if(response.startsWith("Esta")) {
                    Toast.makeText(getApplicationContext(), "Esta corrida foi remanejada", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Ocorreu um erro. Entre em contato com a central.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Servidor não encontrado", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(sr);
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
