package thebigtouffe.anidex;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    WebView WebViewMain;

    private String vue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on modifie la police de l'action bar
        Typeface police_titre = Typeface.createFromAsset(getAssets(),"titre.ttf");
        int titleId = getResources().getIdentifier("action_bar_title", "id","android");
        TextView TextViewTitre = (TextView) findViewById(titleId);
        TextViewTitre.setTextColor(getResources().getColor(R.color.apptheme_accent));
        TextViewTitre.setTypeface(police_titre);

        // initialise la WebViewMain avec le pont Java <> JS
        WebViewMain = (WebView) findViewById(R.id.webview_main);
        WebViewMain.setWebChromeClient(new WebChromeClient());
        WebViewMain.getSettings().setJavaScriptEnabled(true);
        WebViewMain.addJavascriptInterface(new JSInterfaceMain(this), "Android");
        WebViewMain.setInitialScale(1);
        WebViewMain.setWebViewClient(new WebViewClient());
        WebViewMain.loadUrl("file:///android_asset/index.html");
        vue = "AZ";

        // crée un dossier pour télécharger les photos (si besoin)
        File dossier = new File(getExternalFilesDir(null),"/photos");
        dossier.mkdirs();

        // crée le fichier des favori si inexistant
        File favori = new File(getExternalFilesDir(null), "favori.txt");
        if(!favori.exists()) {
            try {
                InputStream is = getAssets().open("favori.txt");
                OutputStream os = new FileOutputStream(favori);
                byte[] data = new byte[is.available()];
                is.read(data);
                os.write(data);
                is.close();
                os.close();
            } catch (IOException e) {
                Log.w("ExternalStorage", "Error writing " + favori, e);
            }
        }

        // crée le fichier des paramètres si inexistant
        File settings = new File(getExternalFilesDir(null), "settings.txt");
        if(!settings.exists()) {
            try {
                InputStream is = getAssets().open("settings.txt");
                OutputStream os = new FileOutputStream(settings);
                byte[] data = new byte[is.available()];
                is.read(data);
                os.write(data);
                is.close();
                os.close();
            } catch (IOException e) {
                Log.w("ExternalStorage", "Error writing " + settings, e);
            }
        }


        // crée l'action bar et le panneau de navigation
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.navigation_drawer_shadow, GravityCompat.START); // dessine une jolie ombre sous le panneau

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // On modifie la police des boutons du panneau
        Button bouton1 = (Button) findViewById(R.id.bouton1);
        bouton1.setTypeface(police_titre);
        Button bouton2 = (Button) findViewById(R.id.bouton2);
        bouton2.setTypeface(police_titre);
        Button bouton3 = (Button) findViewById(R.id.bouton3);
        bouton3.setTypeface(police_titre);
        Button bouton4 = (Button) findViewById(R.id.bouton4);
        bouton4.setTypeface(police_titre);
        Button bouton5 = (Button) findViewById(R.id.bouton5);
        bouton5.setTypeface(police_titre);
        Button bouton6 = (Button) findViewById(R.id.bouton6);
        bouton6.setTypeface(police_titre);
        Button bouton7 = (Button) findViewById(R.id.bouton7);
        bouton7.setTypeface(police_titre);
        Button bouton8 = (Button) findViewById(R.id.bouton8);
        bouton8.setTypeface(police_titre);
        Button bouton9 = (Button) findViewById(R.id.bouton9);
        bouton9.setTypeface(police_titre);
        Button bouton10 = (Button) findViewById(R.id.bouton10);
        bouton10.setTypeface(police_titre);
        Button bouton11 = (Button) findViewById(R.id.bouton11);
        bouton11.setTypeface(police_titre);
        Button bouton12 = (Button) findViewById(R.id.bouton12);
        bouton12.setTypeface(police_titre);

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                /* empty */
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                /* empty */
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }


    // Rafraîchit les favori et les paramètres quand l'utilisateur revient sur l'activité principale
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // on rafraîchit la WebView
        WebViewMain.loadUrl("javascript:updateParametres()");
        WebViewMain.loadUrl("javascript:updateFavori()");

    }


    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(findViewById(R.id.navigation_drawer));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        if (mDrawerLayout != null && isDrawerOpen())
            showGlobalContextActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    // on configure le comportement du bouton retour
    // si on est pas dans la vue AZ on retourne dans la vue AZ, sinon on quitte l'appli
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if (vue == "AZ") {
                        // on quitte l'application quand le bouton retour est pressé en vue AZ
                        finish();
                    }
                    else {
                        WebViewMain.loadUrl("javascript:showAZ()");
                        DrawerLayout mDrawerLayout;
                        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                        mDrawerLayout.closeDrawers();
                        vue = "AZ";
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.identification:
                break;
            case R.id.param:
                // Démarre l'activité des paramètres
                Intent intent = new Intent(MainActivity.this, Param.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Modifie le titre affiché par l'action bar
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    // Crée le pont JS <> Java
    public class JSInterfaceMain {

        Context mContext;
        JSInterfaceMain(Context c){
            mContext = c;
        }

        // lance une nouvelle activité depuis le script JS en transférant l'ID de la fiche
        public void AfficherFiche(String id) {
            Intent i = new Intent(MainActivity.this, Fiche.class);
            i.putExtra("variable_name",id);
            mContext.startActivity(i);
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

    public void showFavori(View view) {
        if (vue != "favori") {
            WebViewMain.loadUrl("javascript:showFavori()");
            DrawerLayout mDrawerLayout;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawers();
        }
        vue = "favori";
    }

    public void showAZ(View view) {
        if (vue != "AZ") {
            WebViewMain.loadUrl("javascript:showAZ()");
            DrawerLayout mDrawerLayout;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawers();
        }
        vue = "AZ";
    }
}