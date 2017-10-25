package br.gov.dataprev.infra.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * ItemWriter util para processamentos de validação que gravam nada.
 *
 * @author DATAPREV/DIT/DEAT
 *
 * @param <T> Classe a ser gravada.
 */
@Lazy
@Component("passThroughItemWriter")
public class PassThroughItemWriter<T> implements ItemWriter<T> {

    /**
     * {@inheritDoc}
     */
    public void write(final List<? extends T> arg0) throws Exception {
        // Ok... Passando direto.
    }

}
