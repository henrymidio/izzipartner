package cardapp.henrique.com.br.cwcourier;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends ActionBarActivity {


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    WebView web;
    ImageView imgLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isNetworkAvailable(this)) {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                finish();
            }
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_LONG).show();
            finish();
        }

        if(checkPlayServices()){

            Intent it = new Intent(this, RegistrationIntentService.class);
            startService(it);
        }


        web = (WebView) findViewById(R.id.webview);
        WebSettings ws = web.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(true);
        imgLoading = (ImageView) findViewById(R.id.imgloader);
        web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                //hide loading image
                imgLoading.setVisibility(View.GONE);
                //show webview
                web.setVisibility(View.VISIBLE);
                //CookieSyncManager.getInstance().sync();
            }



        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String link = extras.getString("link");
            web.setWebChromeClient(new GeoWebChromeClient());
            web.loadUrl(link);
        }
        else {

            web.setWebChromeClient(new GeoWebChromeClient());
            web.loadUrl("http://cardappweb.com/motofrete/logado.php");
        }

    }

    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }
    }

    @Override
    public void onBackPressed() {
        String url = web.getUrl();

        if (web.canGoBack()){
            finish();
        }else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private boolean checkPlayServices() {
        final GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

                finish();
            }
            return false;
        }
        return true;
    }


}
