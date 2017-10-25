package br.gov.dataprev.infra.batch.support.internal;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <b>Atenção!</b> Classe interna do framework. Uso não recomendado, pois pode
 * sofrer modificação severa sem notificação prévia.
 * <p>
 * O componente DeprecatedApiDetector serve para detectar e informar aos desenvolvedores
 * que o framework recomenda o uso de uma nova API.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class DeprecatedApiDetector {

    private static final String WARN_MESSAGE = "ATENÇÃO!!!! Foi detectado o uso "
            + "de API deprecated do framework batch.";

    private static final Logger LOG = Logger.getLogger(DeprecatedApiDetector.class);

    private boolean prodWarning;
    private String mensagem;
    private String alternativa;

    @Autowired
    private LogHelper logHelper;

    @PostConstruct
    public void showMessage() {
        if (prodWarning) {
            logHelper.logDirect(WARN_MESSAGE);
        }
        LOG.warn("DEPRECATED!!!! " + mensagem);
        LOG.warn("Alternativa de uso: " + alternativa);
    }

    public void setMensagem(final String mensagem) {
        this.mensagem = mensagem;
    }

    public void setAlternativa(final String alternativa) {
        this.alternativa = alternativa;
    }

    public void setProdWarning(final boolean prodWarning) {
        this.prodWarning = prodWarning;
    }

    public static void showWarning(final String msg, final String alternativa) {
        DeprecatedApiDetector detector = new DeprecatedApiDetector();
        detector.setMensagem(msg);
        detector.setAlternativa(alternativa);

        detector.showMessage();
    }
}
