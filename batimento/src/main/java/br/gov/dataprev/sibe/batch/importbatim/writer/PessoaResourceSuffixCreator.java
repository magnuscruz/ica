package br.gov.dataprev.sibe.batch.importbatim.writer;

import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.stereotype.Component;

/**
 * Gerador de sufixos para a gera��o dos arquivos de candidatos.
 * @author DATAPREV/DIT/DEAT
 */
@Component("pessoaResourceSuffixCreator")
public class PessoaResourceSuffixCreator implements ResourceSuffixCreator {

    public String getSuffix(final int indice) {
        return String.format(".%03d.CSV", indice);
    }

}
