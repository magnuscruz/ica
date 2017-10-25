package br.gov.dataprev.infra.batch.support;

import java.util.List;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import br.gov.dataprev.infra.batch.support.internal.DeprecatedApiDetector;

/**
 * Realiza a composição de um ou mais {@link JobParametersValidator}.
 * <p>
 * Exemplo de uso.
 *
 * <pre>
 * {@code
 *  <bean id="meuJobValidator" class="br.gov.dataprev.infra.batch.support.JobParametersComposite">
 *       <property name="validators">
 *           <list>
 *               <ref>fooValidator</ref>
 *               <ref>barValidator</ref>
 *           </list>
 *       </property>
 *   </bean>
 * }
 * </pre>
 * @author DATAPREV/DIT/DEAT
 * @see org.springframework.batch.core.job.CompositeJobParametersValidator
 * @deprecated O SpringBatch disponibilizou esta funcionalidade na versao 2.1.8.
 *             Portanto é melhor usar a classe CompositeJobParametersValidator.
 */
@Deprecated
public class JobParametersComposite implements JobParametersValidator {

    private static final String MSG_DEPRECATED = "O componente "
            + "CompositeJobParametersValidator está descontinuado";
    private static final String MSG_ALTERNATIVA =
            "O SpringBatch disponibilizou esta funcionalidade na versao 2.1.8 "
                    + "com a classe CompositeJobParametersValidator";

    private List<JobParametersValidator> validators;

    public JobParametersComposite() {
        DeprecatedApiDetector.showWarning(MSG_DEPRECATED, MSG_ALTERNATIVA);
    }

    public void validate(final JobParameters parameters)
            throws JobParametersInvalidException {
        for (final JobParametersValidator validator : validators) {
            validator.validate(parameters);
        }
    }

    /**
     * Especifica a lista de validadores a serem usados.
     * @param validators Lista de validadores.
     */
    public void setValidators(final List<JobParametersValidator> validators) {
        this.validators = validators;
    }
}
