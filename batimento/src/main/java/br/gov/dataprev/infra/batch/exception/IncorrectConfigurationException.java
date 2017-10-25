package br.gov.dataprev.infra.batch.exception;

/**
 * Exceção indicando algum erro de configuracao dos beans do DtpInfraBatch.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class IncorrectConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IncorrectConfigurationException(final String msg) {
        super(msg);
    }

    public IncorrectConfigurationException(final String msg, final Exception e) {
        super(msg, e);
    }
}
