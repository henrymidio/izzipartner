package cardapp.henrique.com.br.cwcourier.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import java.util.List;
import cardapp.henrique.com.br.cwcourier.R;
import cardapp.henrique.com.br.cwcourier.pojo.Ponto;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class DetalhesCorridaAdapter extends RealmBaseAdapter<Ponto> implements ListAdapter {

    public DetalhesCorridaAdapter(Context context, RealmResults<Ponto> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.listview_detalhes_corrida, parent, false);
            holder = new CustomViewHolder();
            convertView.setTag(holder);

            holder.tvEndereco = (TextView) convertView.findViewById(R.id.tvEndereco);
            holder.tvObs = (TextView) convertView.findViewById(R.id.tvObs);
            holder.ivPonto = (ImageView) convertView.findViewById(R.id.ivPonto);

        } else {
            holder = (CustomViewHolder) convertView.getTag();
        }

        int idImagePonto = position;
        if(position == 0) {idImagePonto = R.drawable.letra_0;}
        if(position == 1) {idImagePonto = R.drawable.letra_1;}
        if(position == 2) {idImagePonto = R.drawable.letra_2;}
        if(position == 3) {idImagePonto = R.drawable.letra_3;}
        if(position == 4) {idImagePonto = R.drawable.letra_4;}

        Ponto ponto = realmResults.get(position);
        holder.tvEndereco.setText(ponto.getEndereco());
        holder.tvObs.setText(ponto.getObs());
        holder.ivPonto.setBackgroundResource(idImagePonto);

        return convertView;
    }

    public static class CustomViewHolder{
        TextView tvEndereco;
        TextView tvObs;
        ImageView ivPonto;
    }
}
