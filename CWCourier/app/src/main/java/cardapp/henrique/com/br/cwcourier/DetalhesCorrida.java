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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import cardapp.henrique.com.br.cwcourier.network.NetworkConnection;
import cardapp.henrique.com.br.cwcourier.pojo.Corrida;
import io.realm.Realm;

public class DetalhesCorrida extends AppCompatActivity {

    private EditText etClienteNome;
    private EditText etOrigem;
    private EditText etDestino;
    private EditText etContato;
    private EditText etReferencia;
    private TextView tvToolbar;
    private Button btnFinalzar;
    private long id;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_corrida);

        etClienteNome = (EditText) findViewById(R.id.etClienteNome);
        etOrigem      = (EditText) findViewById(R.id.etOrigem);
        etDestino     = (EditText) findViewById(R.id.etDestino);
        etContato     = (EditText) findViewById(R.id.etContato);
        etReferencia  = (EditText) findViewById(R.id.etReferencia);
        tvToolbar = (TextView) findViewById(R.id.tvToolbar);
        btnFinalzar = (Button) findViewById(R.id.btnFinalizar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id");
        }

        realm = Realm.getInstance(this);
        final Corrida corre = realm.where(Corrida.class).equalTo("id",id).findFirst();

        tvToolbar.setText("CORRIDA #"+id);
        etClienteNome.setText(corre.getCliente_nome().toString());
        etOrigem.setText(corre.getRetirada().toString());
        etDestino.setText(corre.getEntrega().toString());
        etContato.setText(corre.getTelefone().toString());
        etReferencia.setText(corre.getObs().toString());

        realm.close();

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
                                                    Realm realm = Realm.getInstance(DetalhesCorrida.this);
                                                    Corrida corre = realm.where(Corrida.class).equalTo("id",id).findFirst();
                                                    realm.beginTransaction();
                                                    corre.removeFromRealm();
                                                    realm.commitTransaction();
                                                    realm.close();
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
