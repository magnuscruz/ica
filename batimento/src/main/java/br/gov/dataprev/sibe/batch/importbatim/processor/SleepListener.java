package br.gov.dataprev.sibe.batch.importbatim.processor;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Listener para realizar pausas durantes os commits e steps
 * <p>
 * Usado para testar o Stop e Restart.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component("sleepListener")
public class SleepListener implements StepExecutionListener, ChunkListener {

    private static final Logger LOG = Logger.getLogger(SleepListener.class);

    @Value("${importacaoBatimentoJob.chunk.sleep.time}")
    private long chunkSleepTime;

    @Value("${importacaoBatimentoJob.step.sleep.time}")
    private long stepSleepTime;

    public void beforeStep(final StepExecution stepExecution) {
        aguardar(this.stepSleepTime);
    }

    public void beforeChunk() {
        aguardar(this.chunkSleepTime);
    }

    public ExitStatus afterStep(final StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }

    private void aguardar(final long sleepTime) {
        try {
            if (sleepTime > 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Aguardando " + sleepTime + " ms");
                }
                Thread.sleep(sleepTime);
            }
        } catch (final InterruptedException e) {
            LOG.warn("Sleep interrompido", e);
        }
    }

    public void afterChunk() {
        // Sem implementacao.
    }

	@Override
	public void beforeChunk(ChunkContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterChunk(ChunkContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterChunkError(ChunkContext context) {
		// TODO Auto-generated method stub
		
	}

}
