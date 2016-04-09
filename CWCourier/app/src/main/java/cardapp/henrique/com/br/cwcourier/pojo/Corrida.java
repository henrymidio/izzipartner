package cardapp.henrique.com.br.cwcourier.pojo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Henrique on 02/04/2016.
 */
public class Corrida extends RealmObject {

    @PrimaryKey
    private long id;

    private String cliente_nome;
    private String retirada;
    private String entrega;
    private String obs;
    private String telefone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCliente_nome() {
        return cliente_nome;
    }

    public void setCliente_nome(String cliente_nome) {
        this.cliente_nome = cliente_nome;
    }

    public String getRetirada() {
        return retirada;
    }

    public void setRetirada(String retirada) {
        this.retirada = retirada;
    }

    public String getEntrega() {
        return entrega;
    }

    public void setEntrega(String entrega) {
        this.entrega = entrega;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
