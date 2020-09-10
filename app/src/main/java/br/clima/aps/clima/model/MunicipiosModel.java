package br.clima.aps.clima.model;

import java.io.Serializable;

/**
 * Created by Mateus Santos on 10/10/2017.
 */

public class MunicipiosModel implements Serializable {
    private int id_municipio;
    private String municipio;
    private String estado;


    public MunicipiosModel(int id_municipio, String municipio, String estado) {
        this.id_municipio = id_municipio;
        this.municipio = municipio;
        this.estado = estado;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}