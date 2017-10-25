package br.gov.dataprev.infra.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

/**
 * Componente do Spring que acusa se o SkipCheckingListener detectou registrou algum skip,
 * para qualquer step no job.
 * <p>
 * Retorna COMPLETED WITH SKIPS caso algum Step tenha rejeitado registros com o
 * SkipCheckingListener.
 * <p>
 * Retorna COMPLETED caso contrario.
 * <p>
 * Exemplo de utilização na configuração do Job:
 *
 * <pre>
 * {@code
 * <decision id="foo" decider="dtpSkipCheckDecider">
 *     <next on="COMPLETED WITH SKIPS" to="bar" />
 *     <end on="COMPLETED"/>
 * </decision>
 * <step id="bar">
 *     (... tratar registros skipados. Ex: Mandar e-mail, etc ...)
 *     <end on="COMPLETED" exit-code="COMPLETED WITH SKIPS" />
 * </step>
 * }
 * </pre>
 *
 * @see SkipCheckingListener
 * @author DATAPREV/DIT/DEAT
 */
@Component("dtpSkipCheckDecider")
public class DtpSkipCheckDecider implements JobExecutionDecider {

    /**
     * {@inheritDoc}
     */
    public FlowExecutionStatus decide(final JobExecution jobExec,
            final StepExecution stepExec) {
        if (jobExec.getExecutionContext().get("dtp.infra.batch.TEM_SKIP") == null) {
            return FlowExecutionStatus.COMPLETED;
        } else {
            return new FlowExecutionStatus("COMPLETED WITH SKIPS");
        }
    }
}
