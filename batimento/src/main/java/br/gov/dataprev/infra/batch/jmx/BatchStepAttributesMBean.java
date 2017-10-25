package br.gov.dataprev.infra.batch.jmx;

import java.util.Date;


/**
 * Bean JMX que disponbiliza informações sobre o ultimo StepExecution atualizado.
 *
 * @author DATAPREV/DIT/DEAT
 */
public interface BatchStepAttributesMBean {
    Date getInicioExecucaoStep();
    Date getUltimaAtualizacao();
    String getStepName();

    int getItensLidos();
    int getItensFiltrados();
    int getItensGravados();
    int getItensRejeitados();
    int getQntCommit();
    int getQntRollback();
    String getStepStatus();

    long getTempoDeExecucao();

    double getVazaoGravadosPorMinutos();
    String getItensGravadosNoHistorico();
}
