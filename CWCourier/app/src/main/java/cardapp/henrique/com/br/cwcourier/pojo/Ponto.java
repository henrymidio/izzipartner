package cardapp.henrique.com.br.cwcourier.pojo;

import io.realm.RealmObject;

/**
 * Created by Henrique on 18/05/2016.
 */
public class Ponto extends RealmObject {
    private String endereco;
    private String obs;
    private int ponto;

    public int getPonto() {
        return ponto;
    }

    public void setPonto(int ponto) {
        this.ponto = ponto;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
