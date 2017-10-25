package br.gov.dataprev.sibe.batch.importbatim.to;

import java.io.Serializable;

import br.gov.dataprev.infra.batch.reader.LineNumberEntity;

/**
 * Mapeia as colunas DETAIL do arquivo csv de eleitores.
 * @author DATAPREV/DIT/DEAT
 */
public class PessoaTO extends LineNumberEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;

    private String nit;

    private String tituloEleitoral;

    private String candidato;

    private String nomeZonaEleitoral;

    private String idMunicipioPrev;

    private String tipo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(final String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(final String nit) {
        this.nit = nit;
    }

    public String getTituloEleitoral() {
        return tituloEleitoral;
    }

    public void setTituloEleitoral(final String tituloEleitoral) {
        this.tituloEleitoral = tituloEleitoral;
    }

    public String getCandidato() {
        return candidato;
    }

    public void setCandidato(final String candidato) {
        this.candidato = candidato;
    }

    public String getNomeZonaEleitoral() {
        return nomeZonaEleitoral;
    }

    public void setNomeZonaEleitoral(final String nomeZonaEleitoral) {
        this.nomeZonaEleitoral = nomeZonaEleitoral;
    }

    public String getIdMunicipioPrev() {
        return idMunicipioPrev;
    }

    public void setIdMunicipioPrev(final String idMunicipioPrev) {
        this.idMunicipioPrev = idMunicipioPrev;
    }
}
