package br.gov.dataprev.infra.batch.exception;

/**
 * Exceção Geral de Runtime das aplicações batch.
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
