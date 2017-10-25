package br.gov.dataprev.sibe.batch.importbatim.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import br.gov.dataprev.sibe.batch.importbatim.to.BatimentoHeaderTO;

/**
 * Classe responsavel por mapear a linha header do arquivo.
 * <p>
 * Utiliza o framework SpringBatch.
 * @author DATAPREV/DIT/DEAT
 */
@Component("batimentoHeaderFieldSetMapper")
public class BatimentoHeaderFieldSetMapper implements FieldSetMapper<BatimentoHeaderTO> {

    /**
     * {@inheritDoc}
     */
    public BatimentoHeaderTO mapFieldSet(final FieldSet fs) throws BindException {
        final BatimentoHeaderTO header = new BatimentoHeaderTO();
        header.setId(fs.readString("id"));
        header.setDataSolicitacao(fs.readString("dataSolicitacao"));
        header.setOrientacaoLimite(fs.readString("orientacaoLimite"));
        header.setParteVariavel(fs.readString("parteVariavel"));
        header.setTamanhoInformacao(fs.readString("tamanhoInformacao"));
        header.setTipoDispo(fs.readString("tipoDispo"));
        header.setTotalRegistros(fs.readString("totalRegistros"));

        return header;
    }

}
