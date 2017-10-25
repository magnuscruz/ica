package br.gov.dataprev.sibe.batch.importbatim.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import br.gov.dataprev.sibe.batch.importbatim.to.ZonaEleitoralTO;

/**
 * Classe respons�vel por mapear uma linha do arquivo CSV de zonas eleitorais para uma entidade.
 * <p>
 * Utiliza o framework SpringBatch.
 * @author DATAPREV/DIT/DEAT
 */
@Component("zonaEleitoralFieldSetMapper")
public class ZonaEleitoralFieldSetMapper implements FieldSetMapper<ZonaEleitoralTO> {

    public ZonaEleitoralTO mapFieldSet(final FieldSet fs) throws BindException {
        final ZonaEleitoralTO zonaEleitoral = new ZonaEleitoralTO();
        zonaEleitoral.setNome(fs.readString("nome"));
        zonaEleitoral.setIdMuniciPrev(fs.readString("idMuniciPrev"));

        return zonaEleitoral;
    }

}
