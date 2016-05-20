package cardapp.henrique.com.br.cwcourier;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cardapp.henrique.com.br.cwcourier.adapters.CorridasAdapter;
import cardapp.henrique.com.br.cwcourier.pojo.Corrida;
import io.realm.Realm;
import io.realm.RealmResults;

public class Corridas extends AppCompatActivity {

    private RealmResults<Corrida> corridas;
    private ListView lv;
    private CorridasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corridas);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long idR = extras.getLong("id");
            Realm realm = Realm.getInstance(this);
            Corrida corre = realm.where(Corrida.class).equalTo("id",idR).findFirst();
            realm.beginTransaction();
            corre.setAberta(false);
            realm.commitTransaction();
            realm.close();
        }

        Realm realm = Realm.getInstance(this);
        corridas = realm.where(Corrida.class).equalTo("aberta", true).findAll();
        corridas.sort("id", RealmResults.SORT_ORDER_DESCENDING);

        lv = (ListView) findViewById(R.id.lvCorridas);
        adapter = new CorridasAdapter(this, corridas, true);
        lv.setAdapter(adapter);
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
