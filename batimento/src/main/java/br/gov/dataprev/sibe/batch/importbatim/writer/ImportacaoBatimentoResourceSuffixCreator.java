package br.gov.dataprev.sibe.batch.importbatim.writer;

import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.stereotype.Component;

/**
 * Gerador de sufixos para a geracao dos arquivos de candidatos.
 * @author DATAPREV/DIT/DEAT
 */
@Component("importacaoBatimentoResourceSuffixCreator")
public class ImportacaoBatimentoResourceSuffixCreator implements ResourceSuffixCreator {

    public String getSuffix(final int indice) {
        return String.format(".%03d.CSV", indice);
    }

}
