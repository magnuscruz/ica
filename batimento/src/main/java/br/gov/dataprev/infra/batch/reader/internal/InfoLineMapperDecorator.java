package br.gov.dataprev.infra.batch.reader.internal;

import org.springframework.batch.item.file.LineMapper;

import br.gov.dataprev.infra.batch.reader.LineNumberAware;
import br.gov.dataprev.infra.batch.reader.LineNumberItemReader;

/**
 * <b>Aten��o!</b> Classe interna do framework. Uso n�o recomendado, pois pode
 * sofrer modifica��o severa sem notifica��o pr�via.
 * <p>
 * Decorator de LineMapper que extrai informa��es de linha e n�mero da linha,
 * para ser usado na constru��o do arquivo de skip.
 * </p>
 * @author DATAPREV/DIT/DEAT
 *
 * @see LineNumberItemReader
 * @param <T>  Subclasse de {@link LineNumberAware}.
 */
public class InfoLineMapperDecorator<T extends LineNumberAware>
        implements LineMapper<LineNumberAware> {

    private final LineMapper<LineNumberAware> decoratedLineMapper;

    private LineStepInformationBean lineBean;

    public InfoLineMapperDecorator(final LineMapper<LineNumberAware> decoratedLineMapper) {
        super();
        this.decoratedLineMapper = decoratedLineMapper;
    }

    public LineNumberAware mapLine(final String line, final int lineNumber) throws Exception {
        this.lineBean.setLine(line);
        this.lineBean.setLineNumber(lineNumber);

        return this.decoratedLineMapper.mapLine(line, lineNumber);
    }


    public void setLineBean(final LineStepInformationBean lineBean) {
        this.lineBean = lineBean;

    }
}
