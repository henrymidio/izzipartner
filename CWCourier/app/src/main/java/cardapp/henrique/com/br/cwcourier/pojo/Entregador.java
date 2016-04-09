package cardapp.henrique.com.br.cwcourier.pojo;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import cardapp.henrique.com.br.cwcourier.network.NetworkConnection;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Henrique on 29/03/2016.
 */
public class Entregador extends RealmObject {

    @PrimaryKey private long id;

    private String nome;
    private String foto;
    private String registrationID;
    private boolean status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static void updateLocation(Context context, final long id, final double lat, final double lng){

        final String URL = "http://cardappweb.com/motofrete/motoqueiro/atualiza-posicao-motoqueiro.php?entregador="+id+"&lat="+
                lat+"&lng="+lng;

        // Instantiate the RequestQueue.
        NetworkConnection nc = NetworkConnection.getInstance(context);
        RequestQueue queue = nc.getRequestQueue();

        StringRequest sr = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }
        );

        queue.add(sr);

    }

}
