package cardapp.henrique.com.br.cwcourier;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Henrique on 21/11/2015.
 */
public class MyGCMListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //super.onMessageReceived(from, data);
        Bitmap bitmap = Util.getBitmapFromURL(data.getString("icone"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker(data.getString("ticker"))
        .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bitmap)
                .setContentTitle(data.getString("titulo"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setAutoCancel(true);



        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(data.getString("texto"));
        builder.setStyle(bigText);

        Intent intent = new Intent(this, NovaCorrida.class);
        intent.putExtra("frete", data.getString("frete"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pi);

        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000});

        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.buzina);
        builder.setSound(uri);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());


        startActivity(intent);



    }
}
