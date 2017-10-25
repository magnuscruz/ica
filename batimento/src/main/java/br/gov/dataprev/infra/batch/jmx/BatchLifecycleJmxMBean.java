package br.gov.dataprev.infra.batch.jmx;

import java.util.Date;

import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;

/**
 * Bean JMX que disponbiliza informações sobre o jobExecution atual.
 *
 * @author DATAPREV/DIT/DEAT
 */
public interface BatchLifecycleJmxMBean {

    String getNomeJob();

    String getParametros();

    String getJobStatus();

    Date getInicioFase();

    // Operações
    String obterSumarioDeExecucao();

    String obterSumarioDeExecucaoCompleto();

    void stop() throws NoSuchJobExecutionException, JobExecutionNotRunningException;
}
