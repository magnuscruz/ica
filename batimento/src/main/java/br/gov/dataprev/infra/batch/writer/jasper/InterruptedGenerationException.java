package br.gov.dataprev.infra.batch.writer.jasper;

/**
 * Exce��o indicando que a gera��o do produtor foi interrompida.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class InterruptedGenerationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InterruptedGenerationException(final String msg) {
        super(msg);
    }
}
