package izzi.henrique.com.br.izzimotopartner;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import io.realm.Realm;
import io.realm.RealmResults;
import izzi.henrique.com.br.izzimotopartner.adapters.CorridasAdapter;
import izzi.henrique.com.br.izzimotopartner.pojo.Corrida;

public class Corridas extends AppCompatActivity {

    private Realm realm;
    private RealmResults<Corrida> corridas;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corridas);

        Corrida c1 = new Corrida();
        c1.setId(2);
        c1.setCliente("Henrique");
        c1.setOrigem("Rua Pirapora, 220");
        c1.setContato("9226-1262");
        c1.setDestino("Centro");

        Corrida c2 = new Corrida();
        c2.setId(3);
        c2.setCliente("Fulano");
        c2.setOrigem("Mundo");

        realm = Realm.getInstance(this);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(c1);
        realm.copyToRealmOrUpdate(c2);

        corridas = realm.where(Corrida.class).findAll();
        realm.commitTransaction();

        lv = (ListView) findViewById(R.id.lvCorridas);
        lv.setAdapter(new CorridasAdapter(this, corridas, true));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(Corridas.this, DetalhesCorrida.class);
                it.putExtra("id", id);
                startActivity(it);
            }
        });
    }


}
