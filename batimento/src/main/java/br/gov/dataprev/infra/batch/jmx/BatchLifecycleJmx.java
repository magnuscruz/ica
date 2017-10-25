package br.gov.dataprev.infra.batch.jmx;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.dataprev.infra.batch.jmx.internal.JmxManager;

/**
 * Implementação de Bean JMX que disponbiliza
 * informações sobre o jobExecution atual.
 *
 * @see BatchStepAttributes
 * @author DATAPREV/DIT/DEAT
 */
public class BatchLifecycleJmx implements BatchLifecycleJmxMBean {

    private static final String OBJECT_NAME =
            "br.gov.dataprev.infra.batch:type=BatchLifecycleJmxMBean";

    @Autowired
    private JmxManager jmxManager;

    @PostConstruct
    public void init() throws Exception {
        this.jmxManager.registerJmx(this, OBJECT_NAME);
    }

    @PreDestroy
    public void unregister() throws Exception {
        this.jmxManager.unregister(OBJECT_NAME);
    }

    private JobExecution getJobExecution() {
        return this.jmxManager.getJobExecution();
    }

    public void stop() throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        this.jmxManager.stop();
    }

    public String obterSumarioDeExecucao() {
        return this.jmxManager.obterSumarioDeExecucao();
    }

    public String obterSumarioDeExecucaoCompleto() {
        return this.jmxManager.obterSumarioDeExecucaoCompleto();
    }

    public String getNomeJob() {
        return this.jmxManager.getJobExecution().getJobInstance().getJobName();
    }

    public String getParametros() {
        return this.jmxManager.getJobExecution().getJobParameters().toString();
    }

    public Date getInicioFase() {
        return getJobExecution().getStartTime();
    }

    public String getJobStatus() {
        return getJobExecution().getStatus().name();
    }
}

