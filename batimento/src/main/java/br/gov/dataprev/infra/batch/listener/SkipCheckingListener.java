package br.gov.dataprev.infra.batch.listener;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.dataprev.infra.batch.reader.LineNumberAware;
import br.gov.dataprev.infra.batch.reader.LineNumberException;
import br.gov.dataprev.infra.batch.reader.LineNumberItemReader;
import br.gov.dataprev.infra.batch.support.ProductionHelper;
import br.gov.dataprev.infra.batch.support.SkipFileCallbackHandler;

/**
 * Classe criada pelos autores do SpringBatch e adaptada para o uso no DtpInfraBatch.
 * <p>
 * Esta classe registra os erros de skip em um LOG separado para facilitar a análise
 * da produção. Através da propriedade skipCallbackHandler, é possível que seja
 * gerado um arquivo com as linhas rejeitadas.
 * </p>
 * <p>
 * Neste caso, é recomendado seu uso em conjunto com o {@link LineNumberItemReader}
 * </p>
 *
<pre>
{@code
<listeners>
    <listener>
        <bean class="br.gov.dataprev.infra.batch.listener.SkipCheckingListener"
            xmlns="http://www.springframework.org/schema/beans" >
              <property name="skipCallbackHandler" ref="eleitorSkipCallback" />
        </bean>
    </listener>
</listeners>
<chunk reader="eleitorFileItemReader" processor="pessoaProcessor"
    writer="eleitorWriter" commit-interval="100"  skip-limit="10" >
    <skippable-exception-classes>
        <include class="java.lang.Exception" />
    </skippable-exception-classes>
</chunk>
}
</pre>
 *
 * @see br.gov.dataprev.infra.batch.support.SkipFileCallbackHandler
 * @see br.gov.dataprev.infra.batch.reader.LineNumberItemReader
 * @author DATAPREV/DIT/DEAT, UDRJ e Dan Garrette
 */
public class SkipCheckingListener {

    /** Constante indicando status completado mas com registros ignorados. */
    public static final String COMPLETED_WITH_SKIPS = "COMPLETED WITH SKIPS";

    private static final Logger LOG = Logger.getLogger(SkipCheckingListener.class);
    private static final Logger SKIP_LOG = Logger.getLogger("skip");

    private static final String REGISTRO_REJEITADO = "dtp.infra.batch.REGISTRO_REJEITADO";
    private static int processSkips;

    @Autowired
    private ProductionHelper productionHelper;

    private SkipFileCallbackHandler skipCallbackHandler;

    @AfterStep
    public ExitStatus checkForSkips(final StepExecution stepExecution) {
        if (!stepExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())
                && stepExecution.getSkipCount() > 0) {

            stepExecution.getJobExecution().getExecutionContext()
                    .putString("dtp.infra.batch.TEM_SKIP", "true");
            return new ExitStatus(COMPLETED_WITH_SKIPS);
        } else {
            return null;
        }
    }

    /**
     * Convenience method for testing.
     * @return the processSkips
     */
    public static int getProcessSkips() {
        return processSkips;
    }

    /**
     * Convenience method for testing.
     */
    public static void resetProcessSkips() {
        processSkips = 0;
    }

    @OnSkipInRead
    public void skipRead(final Throwable t) {
        this.logMsg("Skipped on read", t);

        // Findbugs reclama, mas está ok.
        processSkips++;

        if (this.skipCallbackHandler != null) {
            if (t instanceof LineNumberException) {
                final LineNumberException p = (LineNumberException) t;
                this.writeToArquivoDeSkip(p.getLine());
            } else {
                LOG.warn("Esperava-se LineNumberException. Portanto, "
                        + "gravando toString() da exceção no arquivo de skip.");
                this.writeToArquivoDeSkip(t.toString());
            }
    }
    }

    @OnSkipInProcess
    public void skipProcess(final Object obj, final Throwable t) {
        this.logMsg("Skipped on process: [" + obj + "]", t);

        this.productionHelper.log(REGISTRO_REJEITADO, obj);

        // Findbugs reclama, mas está ok.
        processSkips++;

        if (this.skipCallbackHandler != null) {
            if (obj instanceof LineNumberAware) {
                final LineNumberAware lineNumberEntity = (LineNumberAware) obj;
                this.writeToArquivoDeSkip(lineNumberEntity.getLine());
            } else {
                LOG.warn("Esperava-se LineNumberEntity. Portanto, "
                        + "gravando toString() do objeto no arquivo de skip.");
                this.writeToArquivoDeSkip(obj.toString());
    }
        }
    }

    @OnSkipInWrite
    public void skipWrite(final Object obj, final Throwable t) {
        this.logMsg("Skipped on write: [" + obj + "]", t);

        this.productionHelper.log(REGISTRO_REJEITADO, obj);

        // Findbugs reclama, mas está ok.
        processSkips++;

        if (this.skipCallbackHandler != null) {
            if (obj instanceof LineNumberAware) {
                final LineNumberAware lineNumberEntity = (LineNumberAware) obj;
                this.writeToArquivoDeSkip(lineNumberEntity.getLine());
            } else {
                LOG.warn("Esperava-se LineNumberEntity. Portanto, "
                        + "gravando toString() do objeto no arquivo de skip.");
                this.writeToArquivoDeSkip(obj.toString());
    }
        }
    }

    public void onProcessError(final Object obj, final Exception exception) {
        this.logMsg("Error on Process", exception);
    }

    public void onWriteError(final Exception exception, final List<? extends Object> list) {
        this.logMsg("Error on Write", exception);
    }

    public void onReadError(final Exception exception) {
        this.logMsg("Error on Read", exception);
    }

    private void logMsg(final String logMsg, final Throwable ex) {
        LOG.info(logMsg, ex);
        SKIP_LOG.info(logMsg, ex);
    }

    private void writeToArquivoDeSkip(final String line) {
        this.skipCallbackHandler.logToSkipFile(line);
    }

    public void setSkipCallbackHandler(final SkipFileCallbackHandler skipCallbackHandler) {
        this.skipCallbackHandler = skipCallbackHandler;
    }
}
