package br.gov.dataprev.sibe.batch.importbatim.to;

/**
 * Mapeia as colunas do arquivo csv de Zonas Eleitorias.
 * @author DATAPREV/DIT/DEAT
 */
public class ZonaEleitoralTO {
    private String nome;
    private String idMuniciPrev;

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public String getIdMuniciPrev() {
        return idMuniciPrev;
    }

    public void setIdMuniciPrev(final String idMuniciPrev) {
        this.idMuniciPrev = idMuniciPrev;
    }
}
