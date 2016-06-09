package cardapp.henrique.com.br.cwcourier;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import cardapp.henrique.com.br.cwcourier.adapters.DetalhesCorridaAdapter;
import cardapp.henrique.com.br.cwcourier.network.NetworkConnection;
import cardapp.henrique.com.br.cwcourier.pojo.Corrida;
import cardapp.henrique.com.br.cwcourier.pojo.Ponto;
import cardapp.henrique.com.br.cwcourier.services.TrackService;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class DetalhesCorrida extends AppCompatActivity {

    private TextView tvToolbar;
    private Button btnFinalzar;
    private TextView tvSolicitabte;
    private TextView tvContato;
    private ListView listView;
    private long id;
    private Realm realm;
    private RealmList<Ponto> pontos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_corrida);

        tvSolicitabte = (TextView) findViewById(R.id.tvSolicitante);
        tvContato = (TextView) findViewById(R.id.tvContato);
        tvToolbar = (TextView) findViewById(R.id.tvToolbar);
        listView = (ListView) findViewById(R.id.lvPontos);

        btnFinalzar = (Button) findViewById(R.id.btnFinalizar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id");
        }

        realm = Realm.getInstance(this);
        final Corrida corre = realm.where(Corrida.class).equalTo("id",id).findFirst();
        pontos = corre.getTrajeto();
        RealmResults<Ponto> pt = pontos.where().findAll();
        //Log.i("RESULT", pt.get(1).getEndereco());

        listView.setAdapter(new DetalhesCorridaAdapter(this, pt, true));

        tvToolbar.setText("CORRIDA #"+id);
        tvSolicitabte.setText(corre.getCliente_nome());
        tvContato.setText(corre.getTelefone());

        //realm.close();

        //LISTENER DE ENCERRAMENTO DO SERVIÇO
        btnFinalzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetalhesCorrida.this)
                        .setTitle("FINALIZAÇÃO")
                        .setMessage("tem certeza que deseja encerrar esta corrida?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {


                                // Instantiate the RequestQueue.
                                NetworkConnection nc = NetworkConnection.getInstance(getApplicationContext());
                                RequestQueue queue = nc.getRequestQueue();
                                String path = "http://cardappweb.com/motofrete/motoqueiro/webservice/finaliza-entrega.php";

                                // Request a string response from the provided URL.
                                StringRequest sr = new StringRequest(Request.Method.POST, path,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                if(response.trim().equals("1")){

                                                    //Realm realm = Realm.getInstance(DetalhesCorrida.this);
                                                    //Corrida corre = realm.where(Corrida.class).equalTo("id",id).findFirst();
                                                    //realm.beginTransaction();
                                                    //corre.removeFromRealm();
                                                    //realm.commitTransaction();
                                                    //realm.close();

                                                    Intent in = new Intent(DetalhesCorrida.this, Corridas.class);
                                                    in.putExtra("id", id);
                                                    startActivity(in);
                                                    finish();

                                                } else {
                                                    Toast.makeText(DetalhesCorrida.this, ""+response, Toast.LENGTH_SHORT).show();
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
                                        params.put("idCorrida", String.valueOf(id));
                                        params.put("status", String.valueOf(0));
                                        return params;
                                    }
                                };

                                queue.add(sr);

                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

    }


}
