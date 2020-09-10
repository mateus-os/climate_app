package br.clima.aps.clima.ws;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus Santos on 07/10/2017.
 */

public class ClimaTask extends AsyncTask<String, Void, List<String>> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
    }

    private List<String> donwloadString(String urlWS){
        final int SEGUNDOS = 1000;
        try {
            URL url = new URL(urlWS);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10*SEGUNDOS);
            conn.setConnectTimeout(15*SEGUNDOS);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.connect();
            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                List<String> strings = new ArrayList<String>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String s = null;
                while((s = reader.readLine()) != null) {
                    strings.add(s);
                }
                return strings;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}