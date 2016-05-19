package cardapp.henrique.com.br.cwcourier.pojo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Henrique on 02/04/2016.
 */
public class Corrida extends RealmObject {

    @PrimaryKey
    private long id;

    private String cliente_nome;
    private String data;
    private String telefone;
    private String distancia;
    private String duracao;
    private String valor;
    private RealmList<Ponto> trajeto;

    public RealmList<Ponto> getTrajeto() {
        return trajeto;
    }

    public void setTrajeto(RealmList<Ponto> trajeto) {
        this.trajeto = trajeto;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
