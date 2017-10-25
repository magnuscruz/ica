package br.gov.dataprev.infra.batch.writer;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import br.gov.dataprev.infra.batch.support.FileHelper;
import br.gov.dataprev.infra.batch.support.ProductionHelper;

/**
 * ItemWriter de arquivos que faz log no console de produção.
 *
 * Aceita as mesmas propriedades que o {@link FlatFileItemWriter}.
 *
 * @param <T> Tipo do objeto a ser gravado.
 * @author DATAPREV/DIT/DEAT
 */
public class DtpFlatFileItemWriter<T> extends FlatFileItemWriter<T> implements
        DtpItemWriterStream<T> {

    private static final Logger LOG = Logger.getLogger(DtpFlatFileItemWriter.class);

    private static final String MSG_WRITER_ARQUIVO = "dtp.infra.batch.WRITER_ARQUIVO";
    private static final String MSG_ERRO_INFORMACOES = "dtp.infra.batch.ERRO_INFORMACOES";

    // Dependencias.
    @Autowired
    private ProductionHelper pHelper;

    @Autowired
    private FileHelper fileHelper;

    // Atributos
    private Resource resource;

    /**
     * {@inheritDoc}
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResource(final Resource resource) {
        this.resource = resource;
        super.setResource(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(final ExecutionContext executionContext)
            throws ItemStreamException {
        String filename = "";
        try {
            filename = this.resource.getFilename();
            final File file = this.resource.getFile();

            this.pHelper.log(MSG_WRITER_ARQUIVO,
                    this.fileHelper.getFullPath(file));

        } catch (final IOException e) {
            LOG.error("Erro obtendo informações sobre arquivo.", e);
            this.pHelper.log(MSG_ERRO_INFORMACOES, filename);
        }

        super.open(executionContext);
    }
}
