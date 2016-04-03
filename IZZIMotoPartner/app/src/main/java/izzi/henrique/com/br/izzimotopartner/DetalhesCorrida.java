package izzi.henrique.com.br.izzimotopartner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import io.realm.Realm;
import izzi.henrique.com.br.izzimotopartner.pojo.Corrida;

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
        etClienteNome.setText(corre.getCliente().toString());
        etOrigem.setText(corre.getOrigem().toString());
        etDestino.setText(corre.getDestino().toString());
        etContato.setText(corre.getContato().toString());
        etReferencia.setText(corre.getReferencia().toString());

        realm.close();

    }


}
