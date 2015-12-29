package thebigtouffe.anidex;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


public class Fiche extends Activity {

    WebView WebViewFiche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Démarre l'activité Fiche en plein écran
        getActionBar().hide();
        setContentView(R.layout.activity_fiche);

        // Récupère l'ID envoyé depuis l'activité principale
        Bundle extras = getIntent().getExtras();
        String value = extras.getString("variable_name");

        WebViewFiche = (WebView) findViewById(R.id.webview_fiche);
        WebViewFiche.getSettings().setJavaScriptEnabled(true);
        WebViewFiche.addJavascriptInterface(new JSInterfaceFiche(this), "Android");

        // Envoie l'ID demandé dans l'URL, l'ID sera récupéré par une fonction JS
        WebViewFiche.loadUrl("file:///android_asset/fiche.html?id="+value+"?");

        // Indique l'adresse du support de stockage au script JS
        String path_sd = getExternalFilesDir(null).toString();
        String js_path = "javascript:path_sd="+"'file://"+path_sd+"/'";
        WebViewFiche.loadUrl(js_path);

        // Détermine si une connexion à Internet est possible
        String internet;
        internet = "javascript:internet=1";
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // Si aucun réseau n'est utilisable on le dit au script JS.
            internet = "javascript:internet=0";
        }
        Log.w("Internet actif", internet);

        WebViewFiche.loadUrl(internet);
        WebViewFiche.setWebViewClient(new WebViewClient());
        WebViewFiche.setInitialScale(1);

    }

    public class JSInterfaceFiche {

        Context mContext;
        JSInterfaceFiche(Context c){
            mContext = c;
        }

        public void AjouterFavori(String id) {

            try {
                File my_file_name = new File(getExternalFilesDir(null), "favori.txt");
                Writer output;
                output = new BufferedWriter(new FileWriter(my_file_name, true));

                output.append(", " + id);
                output.close();
                Toast.makeText(mContext, "Ajouté à la liste des favoris", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void AjouterSeen(String id) {

            try {
                File my_file_name = new File(getExternalFilesDir(null), "seen.txt");
                Writer output;
                output = new BufferedWriter(new FileWriter(my_file_name, true));

                output.append(", " + id);
                output.close();
                Toast.makeText(mContext, "Ajouté à la liste des aperçus", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
