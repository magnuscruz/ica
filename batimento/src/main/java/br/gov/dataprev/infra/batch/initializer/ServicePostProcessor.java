package br.gov.dataprev.infra.batch.initializer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Componente respons?vel por tratar a anota??o @Service dentro do spring. <p>
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component
public class ServicePostProcessor implements BeanPostProcessor {

    /** Pacote no qual ser? aceito anota??es @Service. */
    private static final String DTP_PACKAGE = "br.gov.dataprev";


//    private final br.gov.dataprev.infra.batch.internal.BatchServiceInjectionProvider provider =
//            new br.gov.dataprev.infra.batch.internal.BatchServiceInjectionProvider();

    /**
     * {@inheritDoc}
     */
    public Object postProcessAfterInitialization(final Object obj, final String name)
            throws BeansException {
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    public Object postProcessBeforeInitialization(final Object obj, final String name)
            throws BeansException {
        // Buscar por @Service apenas nas classes da empresa
        final Package pacote = obj.getClass().getPackage();
//        if (pacote != null && pacote.getName().startsWith(DTP_PACKAGE)) {
//            this.provider.inject(obj, null);
//        }
        return obj;
    }
}
