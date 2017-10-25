package br.gov.dataprev.infra.batch.reader;

import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

/**
 * Interface que permite obter o {@link Resource} utilizado no ItemReader.
 *
 * @param <T> Tipo do objeto a ser lido.
 * @author DATAPREV/DIT/DEAT
 */
public interface DtpItemReaderStream<T> extends
        ResourceAwareItemReaderItemStream<T> {

    /**
     * @return Obtem o Recurso a ser lido.
     */
    Resource getResource();
}
