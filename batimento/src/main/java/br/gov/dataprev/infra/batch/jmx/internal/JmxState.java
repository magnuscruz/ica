package br.gov.dataprev.infra.batch.jmx.internal;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.support.internal.LogHelper;
import br.gov.dataprev.infra.batch.support.internal.StepSummerHelper;


/**
 * <b>Aten??o!</b> Classe interna do framework. Uso n?o recomendado, pois pode
 * sofrer modifica??o severa sem notifica??o pr?via.
 * <p>
 * Mant?m o estado dos objetos JMX, evitando que o banco de dados do Spring seja
 * consultado excessivamente.
 *
 * @author DEAT
 */
@Component
public class JmxState {

    private static final Logger LOG = Logger.getLogger(JmxState.class);

    // ------------------------------------------------------

    // Estado de baixo n?vel
    private String jobName;
    private JobParameters jobParameters;

    // Estado de alto n?vel
    private JobExecution jobExecution;
    private final Map<String,StepExecution> stepExecutionMap = new ConcurrentHashMap<String, StepExecution>();

    // Idade do estado.
    private long idade;

    // -------------- Configura??o ------------------------

    @Value("${batch.jmx.update.time:10000}")
    private long jmxUpdateTime;

    // -------------- Dependencias ------------------------
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private LogHelper logHelper;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private StepSummerHelper stepSummerHelper;

    public JobExecution getJobExecution() {
        if (this.jobExecution == null) {
            LOG.info("Obtendo hist?rico de execu??o para beans JMX");
            this.jobExecution = this.jobRepository.getLastJobExecution(
                    this.jobName, this.jobParameters);

            // Adiciona hist?rico de execu??o.
            final List<JobExecution> execucoes =
                    this.jobExplorer.getJobExecutions(this.jobExecution.getJobInstance());

            final ListIterator<JobExecution> it =
                    execucoes.listIterator(execucoes.size());
            while (it.hasPrevious()) {
                final JobExecution je = it.previous();
                for (final StepExecution se: je.getStepExecutions()) {

                    this.stepExecutionMap.put(se.getStepName(), se);
                }
            }
        }
        return this.jobExecution;
    }

    public StepExecution getStepExecution(final String stepName) {
        if (expirou()) {
            this.logHelper.log(JmxManager.MSG_JMX_CALLED, "obterInformacaoDeStep");

            this.jobExecution = this.jobExplorer.getJobExecution(getJobExecution().getId());
            for (final StepExecution se: this.jobExecution.getStepExecutions()) {
                this.stepExecutionMap.put(se.getStepName(), se);
            }
            this.idade = System.currentTimeMillis();
        }

        StepExecution stepExecution = this.stepExecutionMap.get(stepName);
        if (stepExecution == null) {
            // Cria um stepExecution vazio
            stepExecution = new StepExecution(stepName, this.jobExecution);
            this.stepExecutionMap.put(stepName, stepExecution);
        }

        return stepExecution;
    }

    private boolean expirou() {
        return System.currentTimeMillis() - this.idade > this.jmxUpdateTime;
    }

    public void registerJob(final String jobName, final JobParameters jobParameters) {
        this.jobName = jobName;
        this.jobParameters = jobParameters;
    }
}
