package br.gov.dataprev.sibe.batch.importbatim.to;

import br.gov.dataprev.infra.batch.reader.LineNumberEntity;

/**
 * Mapeia as colunas TRAILER do arquivo csv de eleitores.
 * @author DATAPREV/DIT/DEAT
 */
public class PessoaTrailerTO extends LineNumberEntity {
    private String tipo;
    private String qntPessoas;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(final String tipo) {
        this.tipo = tipo;
    }

    public String getQntPessoas() {
        return qntPessoas;
    }

    public void setQntPessoas(final String qntPessoas) {
        this.qntPessoas = qntPessoas;
    }

}
