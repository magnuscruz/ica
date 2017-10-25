package br.gov.dataprev.infra.batch.support;

import br.gov.dataprev.infra.batch.support.internal.StepInformationHelper;

/**
 * Fase de execução do Step.
 * <p>
 * Utilizado para distinguir artefatos de entrada/saida no sumário
 * <p>
 * @author DATAPREV/DIT/DEAT
 *
 * @see ProductionHelper
 * @see StepInformationHelper
 */
public enum StepPhase {
    READ,
    PROCESS,
    WRITE
}
