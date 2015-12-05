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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Param extends Activity {

    WebView WebViewParam;
    Integer arret;
    String[] paramListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);
        getActionBar().hide();

        WebViewParam = (WebView) findViewById(R.id.webview_param);
        WebViewParam.getSettings().setJavaScriptEnabled(true);
        WebViewParam.addJavascriptInterface(new JSInterfaceParam(this), "Android");
        WebViewParam.loadUrl("file:///android_asset/param.html");

        // lit les paramètres
        File settings = new File(getExternalFilesDir(null), "settings.txt");
        try {
            FileReader fileReader = new FileReader(settings);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();

            paramListe = lines.toArray(new String[lines.size()]);

            String settings_js = "javascript:settings=["+paramListe[0]+","+paramListe[1]+"];";
            WebViewParam.loadUrl(settings_js);
            Log.w("Settings", settings_js);


        }
        catch (IOException e) {
            // Impossible de lire les paramètres
            Log.w("ExternalStorage", "Error reading " + settings, e);
        }

        // Transfère l'URL de la carte SD au script JS
        String path_sd = getExternalFilesDir(null).toString();
        String js_path = "javascript:path_sd=" + "'file://" + path_sd + "/'";
        WebViewParam.loadUrl(js_path);

        // Vérifie  la connexion Internet
        String internet;
        internet = "javascript:internet=1";
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // Aucun réseau actif.
            internet = "javascript:internet=0";
        }
        Log.w("Internet", internet);

        WebViewParam.loadUrl(internet);
        WebViewParam.setWebViewClient(new WebViewClient());
        WebViewParam.setInitialScale(1);

        // Par défaut le téléchargement ne s'arrête pas si lancé
        arret = 0;
    }


    public class JSInterfaceParam {

        Context mContext;
        JSInterfaceParam(Context c){
            mContext = c;
        }

        public void Reprendre() {
            arret=0;
            WebViewParam.loadUrl("javascript:arret=0");
        }

        public void Download(String id) {

            if (arret == 0) {

                try {

                    //set the download URL, a url that points to a file on the internet
                    //this is the file to be downloaded
                    URL url = new URL("http://ms91310.fr/stick/thumbs/img-" + id + ".jpg");

                    //create the new connection
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    File file = new File(getExternalFilesDir(null) + "/photos", "image" + id + ".jpg");

                    //this will be used to write the downloaded data into the file we created
                    FileOutputStream fileOutput = new FileOutputStream(file);

                    //this will be used in reading the data from the internet
                    InputStream inputStream = urlConnection.getInputStream();

                    //this is the total size of the file
                    int totalSize = urlConnection.getContentLength();
                    //variable to store total downloaded bytes
                    int downloadedSize = 0;

                    //create a buffer...
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0; //used to store a temporary size of the buffer

                    //now, read through the input buffer and write the contents to the file
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        //add the data in the buffer to the file in the file output stream (the file on the sd card
                        fileOutput.write(buffer, 0, bufferLength);
                        //add up the size so we know how much is downloaded
                        downloadedSize += bufferLength;


                    }
                    // close the output stream when done
                    Log.w("Download", id);
                    fileOutput.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void stop() {
            arret = 1;
            WebViewParam.loadUrl("javascript:arret=1");
        }

        public void modifierParam1(String param) {

            paramListe[0]=param;
            String settings_js = "javascript:settings=["+paramListe[0]+","+paramListe[1]+"];";
            WebViewParam.loadUrl(settings_js);
            Log.w("Paramètres ", settings_js);

            try {
                File my_file_name = new File(getExternalFilesDir(null), "settings.txt");
                PrintWriter writer = new PrintWriter(my_file_name, "UTF-8");
                writer.println(paramListe[0]);
                writer.println(paramListe[1]);
                writer.close();
                Toast.makeText(mContext, "Paramètre modifié", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void modifierParam2(String param) {

            paramListe[1]=param;
            String settings_js = "javascript:settings=["+paramListe[0]+","+paramListe[1]+"];";
            WebViewParam.loadUrl(settings_js);
            Log.w("Paramètres ", settings_js);

            try {
                File my_file_name = new File(getExternalFilesDir(null), "settings.txt");
                PrintWriter writer = new PrintWriter(my_file_name, "UTF-8");
                writer.println(paramListe[0]);
                writer.println(paramListe[1]);
                writer.close();
                Toast.makeText(mContext, "Paramètre modifié", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
