package br.gov.dataprev.infra.batch.support.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;


/**
 * <b>Atenção!</b> Classe interna do framework.
 * Uso não recomendado, pois pode sofrer modificação severa sem notificação prévia.
 * <p>
 * O componente StepSummerHelper é responsável por informar contadores globais
 * para várias execuções.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component
public class StepSummerHelper {
	private final ThreadLocal<HashMap<Long, StepExecution>> tlStepsSomado = new ThreadLocal<HashMap<Long,StepExecution>>();

    private HashMap<Long, StepExecution> getStepsSomado() {
        return this.tlStepsSomado.get();
    }

    private void setStepsSomado(final HashMap<Long, StepExecution> stepsSomado) {
        this.tlStepsSomado.set(stepsSomado);
    }

	public void initSummerFor(final List<JobExecution> executions) {
		final HashMap<String, ArrayList<StepExecution>> tempMap =
				new HashMap<String, ArrayList<StepExecution>>();

		for (final JobExecution je: executions) {
			final Collection<StepExecution> steps = je.getStepExecutions();

			for (final StepExecution se: steps) {
				ArrayList<StepExecution> lista;
				if (tempMap.containsKey(se.getStepName())) {
					lista = tempMap.get(se.getStepName());
				} else {
					lista = new ArrayList<StepExecution>();
					tempMap.put(se.getStepName(), lista);
				}
				lista.add(se);
			}
		}

		setStepsSomado(new HashMap<Long, StepExecution>());
		for (final ArrayList<StepExecution> lista: tempMap.values()) {
			initFor(lista);
		}
	}

	private void initFor(final ArrayList<StepExecution> lista) {
		for (int i = lista.size()-1; i >= 0; i--) {
			final StepExecution base = lista.get(i);

			int readCount = base.getReadCount();
			int filterCount = base.getFilterCount();
			int writeCount = base.getWriteCount();

			int readSkipCount = base.getReadSkipCount();
			int processSkipCount = base.getProcessSkipCount();
			int writerSkipCount = base.getWriteSkipCount();
					
			int commitCount = base.getCommitCount();
			int rollbackCount = base.getRollbackCount();		
			
			for (int j = lista.size()-1; j > i; j--) {
				final StepExecution anterior = lista.get(j);

				readCount += anterior.getReadCount();
				filterCount += anterior.getFilterCount();
				writeCount += anterior.getWriteCount();

				readSkipCount += anterior.getReadSkipCount();
				processSkipCount += anterior.getProcessSkipCount();
				writerSkipCount += anterior.getWriteSkipCount();
				
				commitCount += anterior.getCommitCount();
				rollbackCount += anterior.getCommitCount();
			}

			final StepExecution somado = new StepExecution(base.getStepName(), base.getJobExecution());
			somado.setReadCount(readCount);
			somado.setFilterCount(filterCount);
			somado.setWriteCount(writeCount);

			somado.setReadSkipCount(readSkipCount);
			somado.setProcessSkipCount(processSkipCount);
			somado.setWriteSkipCount(writerSkipCount);
			
			somado.setCommitCount(commitCount);
			somado.setRollbackCount(rollbackCount);

			getStepsSomado().put(base.getId(), somado);
		}
	}

	public StepExecution getSummerFor(final StepExecution stepExecution) {
		final StepExecution retorno = getStepsSomado().get(stepExecution.getId());
		if (retorno == null) {
			throw new NullPointerException();
		}
		return retorno;
	}

    public Collection<StepExecution> getStepExecutions() {
        return this.tlStepsSomado.get().values();
    }
}
