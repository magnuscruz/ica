package br.gov.dataprev.sibe.batch.importbatim.exception;

import br.gov.dataprev.infra.batch.reader.LineNumberEntity;

/**
 * Exce��o de valida��o.
 * <p>
 * Usada principalmente para em valida��es de entidades {@link LineNumberEntity}.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class ValidacaoException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidacaoException(final String msg, final LineNumberEntity entity) {
        super(String.format(msg, entity.getLineNumber()));
    }

    public ValidacaoException(final String msg) {
        super(msg);
    }
}
