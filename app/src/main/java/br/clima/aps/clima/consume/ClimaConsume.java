package br.clima.aps.clima.consume;

import android.net.Uri;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import br.clima.aps.clima.model.ClimaModel;
import br.clima.aps.clima.model.MunicipiosModel;
import br.clima.aps.clima.parameters.Paths;

import static br.clima.aps.clima.ws.ClimaHttp.bytesToGson;
import static br.clima.aps.clima.ws.ClimaHttp.connect;

/**
 * Created by Mateus Santos on 11/10/2017.
 */

public class ClimaConsume {

    private static int i = 0;

    public static List<ClimaModel> getAllClimas(){
        try {
            HttpURLConnection conn = connect(Paths.LISTALL, "GET", null);

            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){

                Gson gson = new Gson();
                ClimaModel[] climaModel = gson.fromJson(bytesToGson(conn.getInputStream()), ClimaModel[].class);

                return Arrays.asList(climaModel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<MunicipiosModel> getAllMunicipios(String path){
        try {
            HttpURLConnection conn = connect(path+Paths.LISTMUNICIPIOS, "GET", null);
            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){

                Gson gson = new Gson();
                String json = bytesToGson(conn.getInputStream());
                MunicipiosModel[] municipiosModel = gson.fromJson(json, MunicipiosModel[].class);

                return Arrays.asList(municipiosModel);
            }
        } catch (IOException e) {
            i++;
            switch(i){
                case 1:
                    return getAllMunicipios("http://192.168.0.100:8080/");
                case 2:
                    return getAllMunicipios("http://192.168.25.100:8080/");
            }
        }
        return null;
    }

    public static ClimaModel getClima(ClimaModel clima, String path){
        try {
            HttpURLConnection conn = connect(String.format(path+Paths.CONSULTAR, Uri.encode(clima.getMunicipio()), clima.getEstado()), "GET", null);

            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){

                Gson gson = new Gson();
                ClimaModel climaModel = gson.fromJson(bytesToGson(conn.getInputStream()), ClimaModel.class);

                return climaModel;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ClimaModel(e.getMessage());
        }
    }

    public static String inserirClima(ClimaModel climaModel, String path){
        try {
            HttpURLConnection conn = connect(path+Paths.INSERIR, "POST", climaModel);

            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){
                return "OK";
            } else if(response == HttpURLConnection.HTTP_BAD_REQUEST){
                return "BAD REQUEST";
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String atualizarClima(ClimaModel climaModel, String path){
        try {
            HttpURLConnection conn = connect(path+Paths.ATUALIZAR, "PUT", climaModel);

            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){
                return "OK";
            } else if(response == HttpURLConnection.HTTP_NOT_FOUND){
                return "NOT FOUND";
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String excluirClima(ClimaModel climaModel, String path){
        try {
            HttpURLConnection conn = connect(String.format(path+Paths.EXCLUIR, climaModel.getId_clima()), "DELETE", null);

            int response = conn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){
                return "OK";
            } else if(response == HttpURLConnection.HTTP_NOT_FOUND){
                return "NOT FOUND";
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
