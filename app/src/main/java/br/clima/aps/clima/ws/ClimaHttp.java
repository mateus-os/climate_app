package br.clima.aps.clima.ws;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.clima.aps.clima.model.ClimaModel;

/**
 * Created by Mateus Santos on 08/10/2017.
 */

public class ClimaHttp {
//    public static final String CLIMA_URL = "http://mtsde.sytes.net:9191/ClimaWS/clima/listAll";
            //"http://192.168.25.20:8080/ClimaWS/clima/consultarClima/OSASCO/SP";

    public static HttpURLConnection connect(String urlWS, String ReqMethod, ClimaModel clima) throws IOException {
        final int SEGUNDOS = 1000;
        URL url = new URL(urlWS);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setReadTimeout(10*SEGUNDOS);
        conn.setConnectTimeout(15*SEGUNDOS);
        conn.setRequestMethod(ReqMethod);
        conn.setDoInput(true);
        if(clima != null){
            conn.setDoOutput(true);
            conn.addRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(gsonToBytes(clima));
//            os.flush();
            os.close();
        } else {
            conn.setDoOutput(false);
        }
        conn.connect();
        return conn;
    }

    public static boolean haveConnection(Context ctx){
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static String bytesToGson(InputStream is) throws IOException {
        InputStreamReader isReader = new InputStreamReader(is);

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(isReader);

        String read = br.readLine();

        while(read !=null){
            sb.append(read);
            read = br.readLine();
        }
        String s = sb.toString();
        return sb.toString();
    }

    public static byte[] gsonToBytes(ClimaModel clima) throws IOException {
        Gson gson = new Gson();

        String climaGson = gson.toJson(clima);

        return climaGson.getBytes();
    }
}
