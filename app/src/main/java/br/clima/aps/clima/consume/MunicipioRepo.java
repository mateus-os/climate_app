package br.clima.aps.clima.consume;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.clima.aps.clima.dao.ClimaSQL;
import br.clima.aps.clima.model.MunicipiosModel;

/**
 * Created by Mateus Santos on 11/10/2017.
 */

public class MunicipioRepo {
    private ClimaSQL sql;

    public MunicipioRepo(Context ctx) {
        sql = new ClimaSQL(ctx);
    }

    public void inserirMunicipios(List<MunicipiosModel> municipios){
        SQLiteDatabase db = sql.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            for (MunicipiosModel municipio : municipios) {
//                MunicipiosModel municipio = municipios.get(i);

                cv.put(ClimaSQL.COLUMN_CIDADE, municipio.getMunicipio());
                cv.put(ClimaSQL.COLUMN_MUNICIPIO, municipio.getEstado());
                db.insert(ClimaSQL.TABLE_MUNICIPIOS, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<String> listEstados(){
            SQLiteDatabase db = sql.getWritableDatabase();
            List<String> estadosList = new ArrayList<String>();
            String[] args = null;
            String sql = "SELECT DISTINCT " + ClimaSQL.COLUMN_MUNICIPIO + " FROM " + ClimaSQL.TABLE_MUNICIPIOS +
                    " ORDER BY " + ClimaSQL.COLUMN_MUNICIPIO;
            Cursor cursor = db.rawQuery(sql, args);

            while (cursor.moveToNext()) {
                estadosList.add(cursor.getString(cursor.getColumnIndex(ClimaSQL.COLUMN_MUNICIPIO)));
            }
            cursor.close();

            return estadosList;
    }

    public List<String> listMunicipios(String estado){
        SQLiteDatabase db = sql.getWritableDatabase();
        List<String> municipiosList = new ArrayList<String>();

        String sql = "SELECT "+ClimaSQL.COLUMN_CIDADE+" FROM "+ClimaSQL.TABLE_MUNICIPIOS+
                " WHERE "+ClimaSQL.COLUMN_MUNICIPIO + " = ?"+
                " ORDER BY "+ ClimaSQL.COLUMN_CIDADE;
        Cursor cursor = db.rawQuery(sql, new String[]{estado});

        while (cursor.moveToNext()){
            municipiosList.add(cursor.getString(cursor.getColumnIndex(ClimaSQL.COLUMN_CIDADE)));
        }
        cursor.close();
        db.close();

        return municipiosList;
    }

    public int verifyData(){
        SQLiteDatabase db = sql.getWritableDatabase();

        String sql = "SELECT COUNT(DISTINCT "+ ClimaSQL.COLUMN_MUNICIPIO +") FROM " + ClimaSQL.TABLE_MUNICIPIOS;

        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToNext();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count;
    }

    public String getURL(){
        SQLiteDatabase db = sql.getWritableDatabase();

        String sql = "SELECT url FROM pathws";

        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToNext();
        String url = cursor.getString(0);

        cursor.close();
        db.close();

        return url;
    }

    public void setURL(String url){
        SQLiteDatabase db = sql.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("url", url);
        db.update("pathws", cv, "id = 1", null);
        db.close();
    }

    public void dropDB(Context ctx){
        ctx.deleteDatabase("clima.db");
    }
}
