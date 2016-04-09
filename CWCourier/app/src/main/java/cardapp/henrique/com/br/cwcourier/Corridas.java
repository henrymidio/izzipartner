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
import cardapp.henrique.com.br.cwcourier.pojo.Entregador;
import io.realm.Realm;
import io.realm.RealmResults;

public class Corridas extends AppCompatActivity {

    private Realm realm;
    private RealmResults<Corrida> corridas;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corridas);

        realm = Realm.getInstance(this);
        corridas = realm.where(Corrida.class).findAll();

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
