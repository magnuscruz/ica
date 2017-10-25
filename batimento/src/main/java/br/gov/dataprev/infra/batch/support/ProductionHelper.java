package br.gov.dataprev.infra.batch.support;

import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import br.gov.dataprev.infra.batch.exception.IncorrectConfigurationException;
import br.gov.dataprev.infra.batch.listener.IOListener;
import br.gov.dataprev.infra.batch.support.internal.LogHelper;
import br.gov.dataprev.infra.batch.support.internal.StepInformationHelper;
import br.gov.dataprev.infra.batch.support.internal.SumarioBuilderCallback;
import br.gov.dataprev.infra.batch.support.internal.SumarioExecucaoBuilder;

/**
 * Classe de ajuda para o envio de mensagens padronizadas � produ��o.
 *
 * @see DtpJobStarter
 * @author DATAPREV/DIT/DEAT
 */
public class ProductionHelper {

    /*
     * Constantes de mensagens
     */
    private static final String MSG_PURGE_FORCE_WARNING = "dtp.infra.batch.PURGE_FORCE_WARNING";
    private static final String MSG_PURGE_INDISPONIVEL = "dtp.infra.batch.PURGE_INDISPONIVEL";
    private static final String MSG_PURGE_MENOR_QUE_6MESES =
            "dtp.infra.batch.PURGE_MENOR_QUE_6MESES";

    /*
     * Loggers
     */
    static final Logger LOGGER = Logger.getLogger(ProductionHelper.class);

    @Autowired
    private ApplicationContext context;

    @Autowired(required = false)
    private RepositoryHelper repositoryHelper;

    @Autowired
    private LogHelper logHelper;

    @Autowired
    private SumarioExecucaoBuilder sumarioBuilder;

    private String lastSummary;

    /**
     * Gera mensagem de log para a produ��o.
     * <p>
     * O log � gerado para a category "producao" e deve preferencialmente ser
     * lan�ado ao console.
     *
     * @param msg Chave do arquivo de mensagens.
     * @param params Parametros para o texto de mensagem.
     */
    public void log(final String msg, final Object... params) {
        this.logHelper.log(msg, params);
    }

    /**
     * Este m�todo est� disponibilizado apenas para facilitar o desenvolvimento,
     * uma vez que n�o busca a mensagem no arquivo de propriedades.
     * <p>
     * O m�todo recomendado � {@link #log(String, Object...)}.
     *
     * @param texto Texto a ser imprimido no console.
     * @deprecated � recomendado que use o m�todo {@link #log(String, Object...)}.
     */
    @Deprecated
    public void logDirect(final String texto) {
        this.logHelper.logDirect(texto);
    }

    /**
     * Gera o sum�rio completo da execu��o, incluindo restarts anteriores.
     * @param first Execu��o para gerar sum�rio.
     */
    public void gerarSumarioCompleto(final JobExecution first) {
        final LogSumarioBuilderCallback callback = new LogSumarioBuilderCallback(this.logHelper);
        this.sumarioBuilder.gerarSumarioCompleto(callback, first);
        this.lastSummary = callback.getSummaryAsString();
    }

    /**
     * Gera o sum�rio apenas da execu��o atual. Para um sum�rio completo
     * use o m�todo {@link #gerarSumarioCompleto(JobExecution)}
     * @param first Execu��o para gerar sum�rio.
     */
    public void gerarSumario(final JobExecution first) {
        this.logSystemProperties();

        final LogSumarioBuilderCallback callback = new LogSumarioBuilderCallback(this.logHelper);
        this.sumarioBuilder.gerarSumario(callback, first);
        this.lastSummary = callback.getSummaryAsString();
    }

    /**
     * Informa artefato de entrada/sa�da para exibi��o no sum�rio de execu��o.
     * <p>
     * Exemplo: <p>
     * <code>notifyIO(StepPhase.READ, "Arquivos da pasta foo")</code>
     *
     * @param fase Fase relativo ao artefato
     * @param msg Mensagem para exibi��o ao operador do batch.
     *
     * @see IOListener
     */
    public void notifyIO(final StepPhase fase, final String msg) {
        final StepInformationHelper stepIoHelper =
                this.context.getBean(StepInformationHelper.class);
        stepIoHelper.notifyIO(fase, msg);
    }

    /**
     * Informa a URL de conex�o JDBC de entrada/sa�da para
     * exibi��o no sum�rio de execu��o.
     * <p>
     * Exemplo: <p>
     * <code>notifyIO(StepPhase.WRITE, "algumJdbcReference")</code>
     *
     * @param fase Fase relativo ao artefato
     * @param reference JdbcReference usado do arquivo jdbc.xml
     *
     * @see IOListener
     */
    public void notifyDB(final StepPhase fase, final String reference) {
        final StepInformationHelper stepIoHelper =
                this.context.getBean(StepInformationHelper.class);
        stepIoHelper.notifyDB(fase, reference);
    }

    public void expurgar(final String jobName, final int meses, final Date dataDeReferencia,
            final boolean forcar) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format(
                    "Avaliando a possibilidade do expurgo de dados anteriores a %d meses", meses));
        }

        if (this.repositoryHelper == null) {
            log(MSG_PURGE_INDISPONIVEL);
            throw new IncorrectConfigurationException("Expurgo indispon�vel");
        }

        if (meses < 6) {
            if (forcar) {
                log(MSG_PURGE_FORCE_WARNING, meses);
            } else {
                log(MSG_PURGE_MENOR_QUE_6MESES);
                throw new IncorrectConfigurationException("Expurgo indispon�vel");
            }
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTime(dataDeReferencia);
        cal.add(Calendar.MONTH, -meses);

        final Date dataLimite = cal.getTime();
        this.repositoryHelper.expurgarDadosAntesDe(dataLimite, jobName);
    }

    /**
     * Publicar uma informa��o personalizada no sum�rio, para o step atual.
     * <p>
     * O valor a ser publicado no sum�rio de execu��o �
     * o toString do objeto.
     *
     * @param key Chave a ser publicada.
     * @param value Valor a ser publicado.
     */
    public void publicarNoSumario(final String key, final Object value) {
        final StepInformationHelper stepIoHelper =
                this.context.getBean(StepInformationHelper.class);
        stepIoHelper.publicarNoSumario(key, value);
    }

    private void logSystemProperties() {
        LOGGER.debug("Parametros do sistema");

        final Properties p = System.getProperties();
        for (final Entry<Object, Object> entry : p.entrySet()) {
            final String parametro = entry.getKey() + "=" + entry.getValue();

            LOGGER.debug(parametro);
        }
    }

    /**
     * N�o � recomendado o uso deste m�todo. Use o MainHelper.getLastSummaryResult
     *
     * @return �ltimo sum�rio de execu��o processado
     * @see MainHelper
     */
    @Deprecated
    public String getLastSummaryResult() {
        return this.lastSummary;
    }
}

class LogSumarioBuilderCallback implements SumarioBuilderCallback {

    private final LogHelper logHelper;
    private final StringBuilder builder = new StringBuilder();

    public LogSumarioBuilderCallback(final LogHelper logHelper) {
        this.logHelper = logHelper;
    }

    public void build(final String text) {
        this.logHelper.logDirect(text);
        this.builder.append(text);
        this.builder.append("\n");
    }

    public void buildStacktrace(final String stackTrace) {
        ProductionHelper.LOGGER.info(stackTrace);
    }

    public String getSummaryAsString() {
        return this.builder.toString();
    }
}
