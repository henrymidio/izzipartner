package cardapp.henrique.com.br.cwcourier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import cardapp.henrique.com.br.cwcourier.pojo.Corrida;
import io.realm.Realm;

public class DetalhesCorrida extends AppCompatActivity {

    private EditText etClienteNome;
    private EditText etOrigem;
    private EditText etDestino;
    private EditText etContato;
    private EditText etReferencia;
    private TextView tvToolbar;
    private long id;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("id");
        }

        Realm realm = Realm.getInstance(this);
        Corrida corre = realm.where(Corrida.class).equalTo("id",id).findFirst();

        tvToolbar.setText("CORRIDA #"+id);
        etClienteNome.setText(corre.getCliente_nome().toString());
        etOrigem.setText(corre.getRetirada().toString());
        etDestino.setText(corre.getEntrega().toString());
        etContato.setText(corre.getTelefone().toString());
        etReferencia.setText(corre.getObs().toString());

        realm.close();

    }


}
