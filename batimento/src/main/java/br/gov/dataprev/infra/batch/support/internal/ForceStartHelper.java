package br.gov.dataprev.infra.batch.support.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.support.ProductionHelper;

/**
 * <b>Atenção!</b> Classe interna do framework. Uso não recomendado, pois pode
 * sofrer modificação severa sem notificação prévia.
 * <p>
 * Este componente concentra toda a lógica do forceStart que é o modo de execução necessário
 * após o Job ter sofrido um kill. 
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component
public class ForceStartHelper {
	
    /* CONSTANTES */
    private static final String MSG_PREPARANDO_RESTART = "dtp.infra.batch.PREPARANDO_RESTART";
    private static final String MSG_WARNING_RESTART = "dtp.infra.batch.WARNING_RESTART";	
	
    private static final String RESOURCE_INDEX = "MultiResourceItemReader.resourceIndex";
    
    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;	
    
    @Autowired
    private ProductionHelper productionHelper;       
    
    @Value("${batch.autofix.partitions:true}")
    private boolean autofixPartitions;
    
	/**
     * O SpringBatch não força o stop porque o job pode estar
     * rodando concorrentemente em outra máquina.
     * <p/>
     * Como não é o caso da maioria dos batchs na Dataprev, esse método permite que o stop seja
     * forçado.
     *
     * @param id Id do JobExecution do Spring Batch.
     */
    public void forceStop(final long id) {
        final Date agora = new Date();

        final JobExecution jobe = this.jobExplorer.getJobExecution(id);
        jobe.setEndTime(agora);
        jobe.setExitStatus(ExitStatus.STOPPED);
        jobe.setStatus(BatchStatus.STOPPED);
        this.jobRepository.update(jobe);

        // Força stop os steps que estão pendentes e corrige possiveis inconsistencias das tabelas
        PartititionCountersFixer partititionCountersFixer = new PartititionCountersFixer();
        for (final StepExecution stepe : jobe.getStepExecutions()) {       	
            if (podeForcarStart(stepe.getStatus())) {
                stepe.setExitStatus(ExitStatus.STOPPED);
                stepe.setStatus(BatchStatus.STOPPED);
                stepe.setEndTime(agora);
                this.jobRepository.update(stepe);

                fixJiraBatch1798(stepe);
            }
            partititionCountersFixer.include(stepe);
        }
       
        partititionCountersFixer.solveErrors(jobRepository, autofixPartitions);
    }
    
    public void doForceStart(final String jobName, final JobParameters jobParameters) {
        final JobExecution lastExecution = this.jobRepository.getLastJobExecution(jobName,
                jobParameters);

        if (lastExecution != null) {
            this.productionHelper.log(MSG_PREPARANDO_RESTART, jobName);

            // Força o stop, se o job foi interrompido brutalmente
            if (podeForcarStart(lastExecution.getStatus())) {
                this.productionHelper.log(MSG_WARNING_RESTART);
                forceStop(lastExecution.getId());
            }
        }
    }
    
    private boolean podeForcarStart(final BatchStatus status) {
        return status.isRunning() || status.equals(BatchStatus.STOPPING) || status.equals(BatchStatus.ABANDONED);
    }

	/**
	 * Corrige o bug https://jira.springsource.org/browse/BATCH-1798 nas tabelas do Spring Batch.
	 * 
	 * @param stepExecution
	 */
	public void fixJiraBatch1798(StepExecution stepExecution) {
        if (stepExecution.getExecutionContext().containsKey(RESOURCE_INDEX)) {
            final int value =
                    stepExecution.getExecutionContext().getInt(RESOURCE_INDEX);
            if (value == -1) {
                // Solving https://jira.springsource.org/browse/BATCH-1798.
                stepExecution.getExecutionContext().put(RESOURCE_INDEX, 0);
                this.jobRepository.updateExecutionContext(stepExecution);
            }
        }		
	}    
    
}

/**
 * Classe Auxiliar para corrigir erros de atualização das tabelas do Spring ao usar Partition.
 *
 * Mais detalhes e contexto em http://www-git/framework-dataprev/dtp-infra-batch/issues/1
 */
class PartititionCountersFixer {
	private PartitionNode root = new PartitionNode();
	
	void include(StepExecution se) {
		// O Spring Batch separa os steps master dos filhos através do ':'
		String[] names = se.getStepName().split(":");
		List<String> lista = new ArrayList<String>(Arrays.asList(names));
		
		root.includeSteps(se, lista);
	}
	
	void solveErrors(JobRepository jobRepository, boolean autofixPartitions) {
		root.solveErrors(jobRepository, autofixPartitions);		
	}
}

/**
 * Arvore de steps master/filho.
 */
class PartitionNode {
	private static final Logger LOG = Logger.getLogger(PartitionNode.class);
	
    private StepExecution stepExecution;
    private Map<String, PartitionNode> children = new HashMap<String, PartitionNode>();
    
	/**
	 * Resolve erros nos contadores de step master/filho.
	 * @param autofixPartitions 
	 */
	void solveErrors(JobRepository jobRepository, boolean autofixPartitions) {
		// Verifica apenas os steps que tem filhos (master).
		if (children.size() != 0) {
			
			// Resolve erros recursivamente...
			for (PartitionNode node: children.values()) {
				node.solveErrors(jobRepository, autofixPartitions);
			}						
			
			// Confere a soma dos filhos e verifica a consistência.
			StepExecution seSomado = getStepExecutionSomado();
			if (seSomado != null) {
				boolean test = 
						seSomado.getReadCount() == stepExecution.getReadCount() &&
						seSomado.getFilterCount() == stepExecution.getFilterCount() &&
						seSomado.getWriteCount() == stepExecution.getWriteCount() &&
						seSomado.getReadSkipCount() == stepExecution.getReadSkipCount() &&
						seSomado.getProcessSkipCount() == stepExecution.getProcessSkipCount() &&
						seSomado.getWriteSkipCount() == stepExecution.getWriteSkipCount() &&
						seSomado.getCommitCount() == stepExecution.getCommitCount() &&
						seSomado.getRollbackCount() == stepExecution.getRollbackCount();
				
				if (!test) {
					
					if (autofixPartitions) {
						LOG.warn("Providenciando correção do Step Master: " + stepExecution.getStepName());
						
						stepExecution.setReadCount(seSomado.getReadCount());
						stepExecution.setFilterCount(seSomado.getFilterCount());
						stepExecution.setWriteCount(seSomado.getWriteCount());
			
						stepExecution.setReadSkipCount(seSomado.getReadSkipCount());
						stepExecution.setProcessSkipCount(seSomado.getProcessSkipCount());
						stepExecution.setWriteSkipCount(seSomado.getWriteSkipCount());
						
						stepExecution.setCommitCount(seSomado.getCommitCount());
						stepExecution.setRollbackCount(seSomado.getRollbackCount());
						
						jobRepository.update(stepExecution);
					} else {
						LOG.warn("Foi detectado problema no Step Master: " + stepExecution.getStepName() 
								+ ". Use batch.autofix.partitions=true nas propriedades do batch para corrigi-lo.");						
					}
				}
			}
		}
	}
	
	StepExecution getStepExecutionSomado() {
		if (stepExecution == null) {
			// Nó raiz.
			return null;
		}
		
		if (children.size() > 0) {
			StepExecution seSomado = new StepExecution(stepExecution.getStepName(),stepExecution.getJobExecution());
			for (PartitionNode nos: children.values()) {
				StepExecution seFilho = nos.getStepExecutionSomado();
						
				int readCount = seFilho.getReadCount() + seSomado.getReadCount();
				int filterCount = seFilho.getFilterCount() + seSomado.getFilterCount();
				int writeCount = seFilho.getWriteCount() + seSomado.getWriteCount();
	
				int readSkipCount = seFilho.getReadSkipCount() + seSomado.getReadSkipCount();
				int processSkipCount = seFilho.getProcessSkipCount() + seSomado.getProcessSkipCount();
				int writerSkipCount = seFilho.getWriteSkipCount() + seSomado.getWriteSkipCount();
						
				int commitCount = seFilho.getCommitCount() + seSomado.getCommitCount();
				int rollbackCount = seFilho.getRollbackCount() + seSomado.getRollbackCount();
				
				seSomado.setReadCount(readCount);
				seSomado.setFilterCount(filterCount);
				seSomado.setWriteCount(writeCount);
	
				seSomado.setReadSkipCount(readSkipCount);
				seSomado.setProcessSkipCount(processSkipCount);
				seSomado.setWriteSkipCount(writerSkipCount);
				
				seSomado.setCommitCount(commitCount);
				seSomado.setRollbackCount(rollbackCount);
			}
			
			return seSomado;
		} else {
			return stepExecution;
		}
	}
	
	/**
	 * Constroi a arvore de steps master/filho recursivamente.
	 */
	void includeSteps(StepExecution se, List<String> lista) {
		if (lista.size() > 0) {
			String mainName = lista.get(0);
			
			// Cria os ramos necessários
			PartitionNode node = children.get(mainName);
			if (node == null) {
				node = new PartitionNode();
				children.put(mainName, node);
			}
			
			// Testa se chegamos na folha da árvore.
			if (lista.size() == 1) {
				node.stepExecution = se;
			}

			// Avança mais um nível na arvore.
			lista.remove(0);
			node.includeSteps(se, lista);
		}
	}	
}