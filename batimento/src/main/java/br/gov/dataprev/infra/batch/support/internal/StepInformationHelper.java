package br.gov.dataprev.infra.batch.support.internal;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.support.StepPhase;

/**
 * <b>Atenção!</b> Classe interna do framework.
 * Uso não recomendado, pois pode sofrer modificação severa sem notificação prévia.
 * <p>
 * O componente StepIoHelper é responsável por gravar, no scopo de Step, informações
 * que localizem os artefatos de entrada, saída e informações customizadas pelo usuario.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component
@Scope("step")
public class StepInformationHelper {
    /*
     * Prefixos reservados para gravar no meta-dados do Spring
     */
    public static final String DTP_SUMMARY_PREFIX = "dtpInfraSummary.";
    public static final String SUMMARY_INPUT = DTP_SUMMARY_PREFIX + "summaryInput";
    public static final String SUMMARY_OUTPUT = DTP_SUMMARY_PREFIX + "summaryOutput";
    public static final String SUMMARY_PROCESS = DTP_SUMMARY_PREFIX + "summaryProcess";
    public static final String SUMMARY_CUSTOM = DTP_SUMMARY_PREFIX + "custom";

    /**
     * ExecutionContext a ser atualizado.
     */
    @Value("#{stepExecution.ExecutionContext}")
    private ExecutionContext context;

    @Autowired
    private DtpConfigInfoExtractor dtpExtractor;

    public void notifyIO(final StepPhase fase, final String entrada) {
        context.put(getPrefix(fase), entrada);
    }

    private String getPrefix(final StepPhase fase) {
        switch (fase) {
            case PROCESS:
                return SUMMARY_PROCESS;
            case READ:
                return SUMMARY_INPUT;
            case WRITE:
                return SUMMARY_OUTPUT;
            default:
                return null;
        }
    }

    public void notifyDB(final StepPhase fase, final String referencia) {
        final String ref = dtpExtractor.obterJdbcUrl(referencia);
        notifyIO(fase, ref);
    }

    public void publicarNoSumario(final String key, final Object value) {
        String finalKey = SUMMARY_CUSTOM + "." + key;
        context.put(finalKey, value);
    }
}
