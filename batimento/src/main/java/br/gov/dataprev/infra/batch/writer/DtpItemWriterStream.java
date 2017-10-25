package br.gov.dataprev.infra.batch.writer;

import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;

/**
 * Interface que permite obter o {@link Resource} utilizado no ItemWriter.
 *
 * @param <T> Tipo do objeto a ser gravado.
 * @author DATAPREV/DIT/DEAT
 */
public interface DtpItemWriterStream<T> extends
        ResourceAwareItemWriterItemStream<T> {

    /**
     * @return Obtem o Recurso a ser gravado.
     */
    Resource getResource();
}
