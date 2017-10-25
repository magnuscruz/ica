package br.gov.dataprev.infra.batch.jmx;

import java.util.Date;

import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;

/**
 * Bean JMX que disponbiliza informa��es sobre o jobExecution atual.
 *
 * @author DATAPREV/DIT/DEAT
 */
public interface BatchLifecycleJmxMBean {

    String getNomeJob();

    String getParametros();

    String getJobStatus();

    Date getInicioFase();

    // Opera��es
    String obterSumarioDeExecucao();

    String obterSumarioDeExecucaoCompleto();

    void stop() throws NoSuchJobExecutionException, JobExecutionNotRunningException;
}
