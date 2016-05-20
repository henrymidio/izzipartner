package cardapp.henrique.com.br.cwcourier.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import cardapp.henrique.com.br.cwcourier.R;
import cardapp.henrique.com.br.cwcourier.pojo.Corrida;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by Henrique on 02/04/2016.
 */
public class CorridasAdapter extends RealmBaseAdapter<Corrida> implements ListAdapter {


    public CorridasAdapter(Context context, RealmResults<Corrida> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public long getItemId(int i) {

        return realmResults.get(i).getId();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_corridas_lv, parent, false);
            holder = new CustomViewHolder();
            convertView.setTag(holder);

            holder.tvOrigem = (TextView) convertView.findViewById(R.id.tvOrigem);
            holder.tvCliente = (TextView) convertView.findViewById(R.id.tvCliente);
        } else {
            holder = (CustomViewHolder) convertView.getTag();
        }

        Corrida corre = realmResults.get(position);
        holder.tvCliente.setText(corre.getCliente_nome());
        holder.tvOrigem.setText(corre.getTrajeto().get(0).getEndereco());

        return convertView;
    }

    public static class CustomViewHolder{
        TextView tvOrigem;
        TextView tvCliente;
    }
}
