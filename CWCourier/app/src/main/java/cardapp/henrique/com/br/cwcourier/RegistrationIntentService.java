package cardapp.henrique.com.br.cwcourier;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import cardapp.henrique.com.br.cwcourier.pojo.Entregador;
import io.realm.Realm;

/**
 * Created by Henrique on 21/11/2015.
 */
public class RegistrationIntentService extends IntentService {
    public static final String LOG = "LOG";

    public RegistrationIntentService(){
        super(LOG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean status = false;
        //preferences.getBoolean("status", false);

        synchronized (LOG){
            InstanceID instanceID = InstanceID.getInstance(this);
            try {
                if (status == false){

                    String token = instanceID.getToken("434311917397", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    //preferences.edit().putBoolean("status", token != null && token.trim().length() > 0).apply();
                    //Atualiza o objeto no Realm
                    Realm realm = Realm.getInstance(getApplicationContext());
                    realm.beginTransaction();
                    Entregador entregador = realm.where(Entregador.class).findFirst();
                    entregador.setRegistrationID(token);
                    long id = entregador.getId();
                    realm.commitTransaction();
                    realm.close();



                    URL url = new URL("http://cardappweb.com/motofrete/motoqueiro/webservice/registraIDEntregador.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(20000);
                    conn.setConnectTimeout(25000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("registrationID", token)
                            .appendQueryParameter("id", ""+id);

                    String query = builder.build().getEncodedQuery();

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();
                    InputStream is = conn.getInputStream();
                    //String resp = Util.streamToString(is);


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
