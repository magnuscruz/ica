package br.gov.dataprev.infra.batch.reader;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import br.gov.dataprev.infra.batch.exception.BatchRuntimeException;
import br.gov.dataprev.infra.batch.reader.internal.InfoLineMapperDecorator;
import br.gov.dataprev.infra.batch.reader.internal.LineStepInformationBean;
import br.gov.dataprev.infra.batch.support.SkipFileCallbackHandler;

/**
 * ItemReader que faz leitura de entidades LineNumberEntity.
 * <p>
 * Este itemReader permite a identifica??o de linhas com defeito
 * em outras fases do job (PROCESSOR/WRITER).
 * </p>
 * Veja o {@link SkipFileCallbackHandler} para maiores detalhes.
 *
 * @see LineNumberAware
 * @author DATAPREV/DIT/DEAT, UDRJ
 * @param <T>  Implementa??o de de {@link LineNumberAware}.
 */
public class LineNumberItemReader<T extends LineNumberAware> extends
    DtpFlatFileItemReader<LineNumberAware> {
    private static final String ERRO_LENDO_LINHA = "dtp.refappbatch.ERRO_LENDO_LINHA";

    private static final Logger LOG = Logger.getLogger(LineNumberItemReader.class);

    private InfoLineMapperDecorator<LineNumberAware> lineMapperDecorator;

    @Autowired
    private ApplicationContext context;

    private LineStepInformationBean lineBean;

    private String filename;

    @PostConstruct
    public void init() {
        this.lineBean = this.context.getBean(LineStepInformationBean.class);
        this.lineMapperDecorator.setLineBean(this.lineBean);
    }

    /**
     * {@inheritDoc}
     * @see org.springframework.batch.item.file.FlatFileItemReader#doRead()
     */
    @Override
    public LineNumberAware doRead() throws Exception {
        try {
            final LineNumberAware entity = super.doRead();
            if (entity != null) {
                entity.setFileName(this.filename);
                entity.setLineNumber(this.lineBean.getLineNumber());
                entity.setLine(this.lineBean.getLine());
            }

            return entity;
        } catch (final Exception e) {
            final int lineNumber = this.lineBean.getLineNumber();
            this.getProductionHelper().log(ERRO_LENDO_LINHA,
                    Integer.toString(lineNumber));

            throw new LineNumberException(e, this.lineBean.getLineNumber(),
                    this.lineBean.getLine(), this.filename);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResource(final Resource resource) {
        super.setResource(resource);

        // Resolve o nome do arquivo.
        try {
            final File file = super.getResource().getFile();
            this.filename = this.getFileHelper().getFullPath(file);
        } catch (final IOException e) {
            final String msg = e.getMessage()+ "Erro obtendo informa??es sobre arquivo.";
            LOG.error(msg);
            throw new BatchRuntimeException(msg, e);
        }
    }

    @Override
    public void setLineMapper(final LineMapper<LineNumberAware> lineMapper) {
        // Envolve o lineMapper por um decorator que identificar? as linhas lidas.
        this.lineMapperDecorator = new InfoLineMapperDecorator<LineNumberAware>(lineMapper);
        super.setLineMapper(this.lineMapperDecorator);
    }

}
