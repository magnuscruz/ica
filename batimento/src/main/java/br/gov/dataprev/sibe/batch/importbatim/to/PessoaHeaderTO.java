package br.gov.dataprev.sibe.batch.importbatim.to;

import br.gov.dataprev.infra.batch.reader.LineNumberEntity;


/**
 * Mapeia as colunas HEADER do arquivo csv de eleitores.
 * @author DATAPREV/DIT/DEAT
 */
public class PessoaHeaderTO extends LineNumberEntity {
    private String tipo;
    private String competencia;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(final String tipo) {
        this.tipo = tipo;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(final String competencia) {
        this.competencia = competencia;
    }
}
