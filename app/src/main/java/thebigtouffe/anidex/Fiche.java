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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


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

        public String getFavori() {
            // lit le fichier des favori puis transfère les données vers la page HTML
            File favori = new File(getExternalFilesDir(null), "favori.txt");
            String favori_js = "";
            try {
                FileReader fileReader = new FileReader(favori);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                List<String> lines = new ArrayList<String>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
                bufferedReader.close();

                String[] favori_liste;
                favori_liste = lines.toArray(new String[lines.size()]);


                int i = 0;
                while (i<favori_liste.length-1) {
                    favori_js = favori_js + favori_liste[i];
                    i=i+1;
                }
                favori_js= favori_js + favori_liste[i];


                //WebViewMain.loadUrl(favori_js);
                Log.w("Favori", favori_js);

            }
            catch (IOException e) {
                // Impossible de lire le fichier
                Log.w("ExternalStorage", "Error reading " + favori, e);
            }
            return favori_js;
        }

        public String getSeen() {
            // lit le fichier des aperçus puis transfère les données vers la page HTML
            File seen = new File(getExternalFilesDir(null), "seen.txt");
            String seen_js = "";
            try {
                FileReader fileReader = new FileReader(seen);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                List<String> lines = new ArrayList<String>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
                bufferedReader.close();

                String[] seen_liste;
                seen_liste = lines.toArray(new String[lines.size()]);


                int i = 0;
                while (i<seen_liste.length-1) {
                    seen_js = seen_js + seen_liste[i];
                    i=i+1;
                }
                seen_js = seen_js + seen_liste[i];


                //WebViewMain.loadUrl(seen_js);
                Log.w("Seen", seen_js);

            }
            catch (IOException e) {
                // Impossible de lire le fichier
                Log.w("ExternalStorage", "Error reading " + seen, e);
            }
            return seen_js;
        }

        public String getParametres() {
            // lit le fichier des paramètres puis transfère les données vers la page HTML
            File settings = new File(getExternalFilesDir(null), "settings.txt");
            String settings_js = "";

            try {
                FileReader fileReader = new FileReader(settings);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                List<String> lines = new ArrayList<String>();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
                bufferedReader.close();

                String[] settings_liste;
                settings_liste = lines.toArray(new String[lines.size()]);

                settings_js = settings_liste[0] + settings_liste[1];
                Log.w("Settings", settings_js);

            }
            catch (IOException e) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                Log.w("ExternalStorage", "Error reading " + settings, e);
            }
            return settings_js;
        }


    }

}
