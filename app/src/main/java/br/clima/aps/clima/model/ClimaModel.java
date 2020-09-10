package br.clima.aps.clima.model;

import java.io.Serializable;

/**
 * Created by deoliveira.mateus on 25/09/2017.
 */

public class ClimaModel implements Serializable {

    private int id_clima;
    private String municipio;
    private String estado;
    private String temp_max;
    private String temp_min;
    private String sens_term;
    private String temp_atual;
    private String umidade;
    private String date_ins;
    private String response;

    //Consultar
    public ClimaModel(int id_clima, String municipio, String estado, String temp_max, String temp_min, String sens_term, String temp_atual, String umidade, String date_ins) {
        this.id_clima = id_clima;
        this.municipio = municipio;
        this.estado = estado;
        this.temp_max = temp_max;
        this.temp_min = temp_min;
        this.sens_term = sens_term;
        this.temp_atual = temp_atual;
        this.umidade = umidade;
        this.date_ins = date_ins;
    }

    //Atualizar
    public ClimaModel(int id_clima, String municipio, String estado, String temp_max, String temp_min, String sens_term, String temp_atual, String umidade) {
        this.id_clima = id_clima;
        this.municipio = municipio;
        this.estado = estado;
        this.temp_max = temp_max;
        this.temp_min = temp_min;
        this.sens_term = sens_term;
        this.temp_atual = temp_atual;
        this.umidade = umidade;
    }

    //Inserir
    public ClimaModel(String municipio, String estado, String temp_max, String temp_min, String sens_term, String temp_atual, String umidade) {
        this.municipio = municipio;
        this.estado = estado;
        this.temp_max = temp_max;
        this.temp_min = temp_min;
        this.sens_term = sens_term;
        this.temp_atual = temp_atual;
        this.umidade = umidade;
        this.id_clima = 0;
    }

    //Excluir
    public ClimaModel(int id_clima) {
        this.id_clima = id_clima;
    }

    public ClimaModel(String municipio, String estado) {
        this.municipio = municipio;
        this.estado = estado;
    }

    public ClimaModel(String response) {
        if(response.contains("failed to connect")){
            this.response = "Tempo esgotado";
        } else this.response = response;
    }

    public ClimaModel(){};

    public int getId_clima() {
        return id_clima;
    }

    public void setId_clima(int id_clima) {
        this.id_clima = id_clima;
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

    public String getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public String getSens_term() {
        return sens_term;
    }

    public void setSens_term(String sens_term) {
        this.sens_term = sens_term;
    }

    public String getTemp_atual() {
        return temp_atual;
    }

    public void setTemp_atual(String temp_atual) {
        this.temp_atual = temp_atual;
    }

    public String getUmidade() {
        return umidade;
    }

    public void setUmidade(String umidade) {
        this.umidade = umidade;
    }

    public String getDate_ins() { return date_ins; }

    public void setDate_ins(String date_ins) {
        this.date_ins = date_ins;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return id_clima +", " + municipio +"-" + estado;
    }

    public boolean validateClima(){
        if(String.valueOf(this.id_clima).isEmpty() || this.municipio.isEmpty() || this.estado.isEmpty() || this.temp_max.isEmpty() ||
                this.temp_min.isEmpty() || this.sens_term.isEmpty() || this.temp_atual.isEmpty() || this.umidade.isEmpty()){
            return false;
        } else return true;
    }
}