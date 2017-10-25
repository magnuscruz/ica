package br.gov.dataprev.infra.batch.reader;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import br.gov.dataprev.infra.batch.support.FileHelper;
import br.gov.dataprev.infra.batch.support.ProductionHelper;

/**
 * ItemReader de arquivos que faz log dos arquivos que estão sendo abertos
 * para a produção.
 * <br/>
 * O componente aceita as mesmas propriedades que o {@link FlatFileItemReader}.
 *
 * @param <T> Tipo do objeto a ser lido.
 * @author DATAPREV/DIT/DEAT
 */
public class DtpFlatFileItemReader<T> extends FlatFileItemReader<T> implements
        DtpItemReaderStream<T> {

    private static final Logger LOG = Logger.getLogger(DtpFlatFileItemReader.class);

    private static final String MSG_READER_ARQUIVO = "dtp.infra.batch.READER_ARQUIVO";
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

            this.getProductionHelper().log(MSG_READER_ARQUIVO,
                    this.getFileHelper().getFullPath(file));

        } catch (final IOException e) {
            LOG.error("Erro obtendo informações sobre arquivo.", e);
            this.getProductionHelper().log(MSG_ERRO_INFORMACOES, filename);
        }

        super.open(executionContext);
    }

    ProductionHelper getProductionHelper() {
        return pHelper;
    }

    FileHelper getFileHelper() {
        return fileHelper;
    }
}
