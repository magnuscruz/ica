package br.gov.dataprev.infra.batch.jmx.internal;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.jmx.BatchLifecycleJmx;
import br.gov.dataprev.infra.batch.jmx.BatchStepAttributes;
import br.gov.dataprev.infra.batch.support.internal.LogHelper;
import br.gov.dataprev.infra.batch.support.internal.SumarioBuilderCallback;
import br.gov.dataprev.infra.batch.support.internal.SumarioExecucaoBuilder;

/**
 * <b>Aten??o!</b> Classe interna do framework. Uso n?o recomendado, pois pode
 * sofrer modifica??o severa sem notifica??o pr?via.
 * <p>
 * Gerenciador de JMX do framework batch. Esta classe ? respons?vel por organizar
 * todos os beans JMX gerados pelo dtpInfraBatch.
 *
 * @author DEAT
 */
@Component
public class JmxManager {

    private static final Logger LOG = Logger.getLogger(JmxManager.class);
    static final String MSG_JMX_CALLED = "dtp.infra.batch.JMX_CALLED";

    @Autowired
    private JmxState jmxState;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private SumarioExecucaoBuilder sumarioExecucaoBuilder;

    @Autowired
    private LogHelper logHelper;

    @Autowired
    private ApplicationContext context;

    public void registerJmx(final Object obj, final String name) throws Exception {
        LOG.debug("Inicializando bean JMX ["+ name+ "]");

        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        final ObjectName objectName = new ObjectName(name);
        if (mbs.isRegistered(objectName)) {
            unregister(name);
        }
        mbs.registerMBean(obj, objectName);
    }

    public void unregister(final String name) throws Exception {
        LOG.debug("Removendo bean JMX ["+ name+ "]");

        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        final ObjectName objectName = new ObjectName(name);
        mbs.unregisterMBean(objectName);
    }

    public JobExecution getJobExecution() {
        return this.jmxState.getJobExecution();
    }

    public StepExecution getStepExecution(final String stepName) {
        return this.jmxState.getStepExecution(stepName);
    }

    public void stop() throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        this.logHelper.log(MSG_JMX_CALLED, "stop()");
        this.jobOperator.stop(getJobExecution().getId());
    }

    public String obterSumarioDeExecucao() {
        this.logHelper.log(MSG_JMX_CALLED, "obterSumarioDeExecucao");

        final JobExecution je = this.jobExplorer.getJobExecution(getJobExecution().getId());

        final StringSumarioBuilderCallback callback = new StringSumarioBuilderCallback();
        this.sumarioExecucaoBuilder.gerarSumario(callback, je);

        return callback.getText();
    }

    public String obterSumarioDeExecucaoCompleto() {
        this.logHelper.log(MSG_JMX_CALLED, "obterSumarioDeExecucaoCompleto");

        final JobExecution je = this.jobExplorer.getJobExecution(getJobExecution().getId());

        final StringSumarioBuilderCallback callback = new StringSumarioBuilderCallback();
        this.sumarioExecucaoBuilder.gerarSumarioCompleto(callback, je);

        return callback.getText();
    }

    public void registerJob(final String jobName, final JobParameters jobParameters) throws Exception {
        this.jmxState.registerJob(jobName, jobParameters);

        // Inicia beans JMX
        this.context.getBean(BatchLifecycleJmx.class);

        final Object job = this.context.getBean(jobName);

        if (job instanceof FlowJob) {
            final FlowJob flowJob = (FlowJob) job;

            // Cria um JMX por step.
            for (final String stepName: flowJob.getStepNames()) {
                new BatchStepAttributes(this, stepName);
            }
        } else {
            LOG.warn("Sem suporte completo de JMX para [" + job.getClass() + "]");
        }
    }


}

class StringSumarioBuilderCallback implements SumarioBuilderCallback {

    StringBuilder builder = new StringBuilder();

    public void build(final String text) {
        this.builder.append(text);
        this.builder.append("\n");
    }

    public String getText() {
        return this.builder.toString();
    }

    public void buildStacktrace(final String stackTrace) {
        this.builder.append(stackTrace);
        this.builder.append("\n");
    }
}
