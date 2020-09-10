package br.clima.aps.clima.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mateus Santos on 11/10/2017.
 */

public class ClimaSQL extends SQLiteOpenHelper {

    private static final String DB_NAME = "clima.db";
    private static final int VERSION = 1;

    public static final String TABLE_MUNICIPIOS = "municipios";
    public static final String COLUMN_ID = "id_municipio";
    public static final String COLUMN_CIDADE = "cidade";
    public static final String COLUMN_MUNICIPIO = "municipio";

    public ClimaSQL(Context context){
        super(context,DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+ TABLE_MUNICIPIOS + " (" +
                COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_CIDADE + " TEXT NOT NULL, "+
                COLUMN_MUNICIPIO + " TEXT NOT NULL)";
        db.execSQL(sql);

        db.execSQL("CREATE TABLE pathws (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT NOT NULL)");
        db.execSQL("INSERT INTO pathws (url) values ('http://mtsde.sytes.net:9191/')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
