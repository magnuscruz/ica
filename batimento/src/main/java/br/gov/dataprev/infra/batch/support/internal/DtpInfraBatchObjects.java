package br.gov.dataprev.infra.batch.support.internal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * <b>Aten��o!</b> Classe interna do framework. Uso n�o recomendado, pois pode
 * sofrer modifica��o severa sem notifica��o pr�via.
 * <p>
 * O componente DtpInfraBatchObjects permite que objetos n�o gerenciados
 * pelo Spring acessem componentes do DtpInfraBatch.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component
@Lazy(value = false)
public class DtpInfraBatchObjects {

    private static DtpInfraBatchObjects instance;

    @Autowired
    private ApplicationContext context;

    public static DtpInfraBatchObjects getInstance() {
        return instance;
    }

    public LogHelper getLogHelper() {
        return this.context.getBean(LogHelper.class);
    }

    public DtpConfigInfoExtractor getDtpConfigInfoExtractor() {
        return this.context.getBean(DtpConfigInfoExtractor.class);
    }

    public ApplicationContext getApplicationContext() {
        return this.context;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        // Findbugs reclama, mas est� ok.
        instance = this;
    }
}
