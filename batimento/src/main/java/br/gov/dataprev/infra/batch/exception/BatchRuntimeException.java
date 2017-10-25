package br.gov.dataprev.infra.batch.exception;

/**
 * Exce��o Geral de Runtime das aplica��es batch.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class BatchRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BatchRuntimeException(final String msg) {
        super(msg);
    }

    public BatchRuntimeException(final String msg, final Exception e) {
        super(msg, e);
    }

    public BatchRuntimeException(final Exception e) {
        super(e);
    }
}
