package br.gov.dataprev.infra.batch.exception;


/**
 * Exceção Geral das aplicações Batch.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class BatchException extends Exception {
    private static final long serialVersionUID = 797100494914717757L;

    public BatchException(final String msg) {
        super(msg);
    }

    public BatchException(final String msg, final Exception e) {
        super(msg, e);
    }

    public BatchException(final Exception e) {
        super(e.getMessage(), e);
    }
}
