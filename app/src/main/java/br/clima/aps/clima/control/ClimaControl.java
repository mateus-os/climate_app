package br.clima.aps.clima.control;

import br.clima.aps.clima.model.ClimaModel;

/**
 * Created by deoliveira.mateus on 25/09/2017.
 */

public class ClimaControl {

    private ClimaModel clima;

    void inserirClima(int id_clima, String cidade, String estado, String temp_max, String temp_min, String sens_term, String temp_atual, String umidade)
    {
        clima.setId_clima(id_clima);
        clima.setMunicipio(cidade);
        clima.setEstado(estado);
        clima.setTemp_max(temp_max);
        clima.setTemp_min(temp_min);
        clima.setSens_term(sens_term);
        clima.setTemp_atual(temp_atual);
        clima.setUmidade(umidade);
    }

    ClimaModel consultarClima(String cidade, String estado)
    {
//        clima = new ClimaModel();

        return clima;
    }

    void excluirClima(String cidade, String estado) //
    {
//        clima = new ClimaModel();

        clima.setMunicipio(cidade);
        clima.setEstado(estado);
    }

    void atualizarClima(int id_clima, String cidade, String estado, String temp_max, String temp_min, String sens_term, String temp_atual, String umidade)
    {
        clima.setId_clima(id_clima);
        clima.setMunicipio (cidade);
        clima.setEstado(estado);
        clima.setTemp_max(temp_max);
        clima.setTemp_min(temp_min);
        clima.setSens_term(sens_term);
        clima.setTemp_atual(temp_atual);
        clima.setUmidade(umidade);
    }
}
