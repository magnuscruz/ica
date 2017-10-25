package br.gov.dataprev.infra.batch.support.internal;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * <b>Aten??o!</b> Classe interna do framework. Uso n?o recomendado, pois pode
 * sofrer modifica??o severa sem notifica??o pr?via.
 * <p>
 * O componente DtpConfigInfoExtractor ? respons?vel por extrair informa??es do
 * DtpInfraCore e tratar suas evolu??es.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component
public class DtpConfigInfoExtractor {

    private static final Logger LOG = Logger.getLogger(DtpConfigInfoExtractor.class);

    private static final String ERRO_JDBC_REFERENCE = "Erro ao localizar o arquivo de Jdbc";
    private static final String ERRO_JDBC_REFERENCE_NAO_ENCONTRADO =
            "A referencia jdbc [%s] n?o foi encontrada";
    private static final String ERRO_SERVICO_NAO_ENCONTRADO =
            "A referencia de servi?o [%s] n?o foi encontrada";


    /**
     * @param jdbcReferenceName
     *            Nome da Referencia.
     * @return A Url de conex?o JDBC informada.
     */
    public String obterJdbcUrl(final String jdbcReferenceName) {

        return "jdbc:derby://localhost:1527/sample;create=true;upgrade=true";
    }

    
}
