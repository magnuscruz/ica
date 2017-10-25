package br.gov.dataprev.infra.batch.jmx;

import java.util.Date;

import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.dataprev.infra.batch.jmx.internal.JmxManager;

/**
 * Implementação de Bean JMX que disponbiliza
 * informações sobre o ultimo StepExecution atualizado.
 *
 * @see BatchLifecycleJmx
 * @author DATAPREV/DIT/DEAT
 */
public class BatchStepAttributes implements BatchStepAttributesMBean {

    @Autowired
    private final JmxManager manager;
    private final String stepName;

    private static final String OBJECT_NAME =
            "br.gov.dataprev.infra.batch:type=%name%MXBean";

    public BatchStepAttributes(final JmxManager manager, final String stepName) throws Exception {
        this.manager = manager;
        this.stepName = stepName;

        manager.registerJmx(this, OBJECT_NAME.replace("%name%", stepName));
    }

    private StepExecution getStepExecution() {
        return this.manager.getStepExecution(this.stepName);
    }

    public Date getInicioExecucaoStep() {
        return this.getStepExecution().getStartTime();
    }

    public Date getUltimaAtualizacao() {
        return this.getStepExecution().getLastUpdated();
    }

    public String getStepName() {
        return this.getStepExecution().getStepName();
    }

    public int getItensLidos() {
        return this.getStepExecution().getReadCount();
    }

    public int getItensFiltrados() {
        return this.getStepExecution().getFilterCount();
    }

    public int getItensGravados() {
        return this.getStepExecution().getWriteCount();
    }

    public int getItensRejeitados() {
        return this.getStepExecution().getSkipCount();
    }

    public int getQntCommit() {
        return this.getStepExecution().getCommitCount();
    }

    public int getQntRollback() {
        return this.getStepExecution().getRollbackCount();
    }

    public String getStepStatus() {
        return this.getStepExecution().getStatus().name();
    }

    public long getTempoDeExecucao() {
        return calcTempoDeExecucao(getStepExecution());
    }

    private long calcTempoDeExecucao(final StepExecution se) {
        final long inicio = se.getStartTime().getTime();

        long fim;
        if (se.getEndTime() != null) {
            fim = se.getEndTime().getTime();
        } else {
            fim = System.currentTimeMillis();
        }

        return fim - inicio;
    }

    public double getVazaoGravadosPorMinutos() {
        final StepExecution se = this.getStepExecution();

        final double itensGravados = se.getWriteCount();
        final long tempoEmMilisegundos = calcTempoDeExecucao(se);

        final double tempoEmMinutos = tempoEmMilisegundos / 60000.0;

        return itensGravados / tempoEmMinutos;
    }

    public String getItensGravadosNoHistorico() {
        return "Indisponivel";
    }
}
