package br.gov.dataprev.infra.batch.support.internal;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import br.gov.dataprev.infra.batch.jmx.BatchLifecycleJmx;
import br.gov.dataprev.infra.batch.support.ProductionHelper;

/**
 * <b>Atenção!</b> Classe interna do framework. Uso não recomendado, pois pode
 * sofrer modificação severa sem notificação prévia.
 * <p>
 * O componente SumarioExecucaoBuilder serve para gerar o sumario de execução
 * para uso via JMX ou pelo production helper.
 *
 * @see ProductionHelper
 * @see BatchLifecycleJmx
 * @author DATAPREV/DIT/DEAT
 */
@Component
public class SumarioExecucaoBuilder {

    private static final String ENTRADA_SAIDA_NAO_NOTIFICADA =
            "Não foi notificado para o sumário os arquivos de entrada/saida "
                    + "relativos ao Step. Favor, verificar a documentação da API ProductionHelper";
    private static final String ERRO_INESPERADO =
            "Erro inesperado. Favor consultar o log de desenvolvimento";
    /*
     * Constantes de mensagens
     */
    private static final String MSG_PARAM = "dtp.infra.batch.ITEM_PARAMETRO";
    private static final String MSG_PARAMETROS = "dtp.infra.batch.PARAMETROS";
    private static final String MSG_ERRO_STEP = "dtp.infra.batch.MSG_ERRO_STEP";
    private static final String MSG_STATUS_STEP = "dtp.infra.batch.STATUS_STEP";
    private static final String MSG_FIM_STEP = "dtp.infra.batch.FIM_STEP";
    private static final String MSG_ROLLBACK_COUNT = "dtp.infra.batch.ROLLBACK_COUNT";
    private static final String MSG_COMMIT_COUNT = "dtp.infra.batch.COMMIT_COUNT";
    private static final String MSG_ITENS_REJEITADOS = "dtp.infra.batch.ITENS_REJEITADOS";
    private static final String MSG_ITENS_FILTRADOS = "dtp.infra.batch.ITENS_FILTRADOS";
    private static final String MSG_ITENS_GRAVADOS = "dtp.infra.batch.ITENS_GRAVADOS";
    private static final String MSG_ITENS_LIDOS = "dtp.infra.batch.ITENS_LIDOS";
    private static final String MSG_INICIO_STEP = "dtp.infra.batch.INICIO_STEP";
    private static final String MSG_NOME_STEP = "dtp.infra.batch.NOME_STEP";
    private static final String MSG_STATUS_FASE = "dtp.infra.batch.STATUS_FASE";
    private static final String MSG_FIM_FASE = "dtp.infra.batch.FIM_FASE";
    private static final String MSG_SEPARATOR = "dtp.infra.batch.SEPARATOR";
    private static final String MSG_INICIO_FASE = "dtp.infra.batch.INICIO_FASE";
    private static final String MSG_NOME_FASE = "dtp.infra.batch.NOME_FASE";
    private static final String MSG_SUMARIO = "dtp.infra.batch.SUMARIO";
    private static final String MSG_PARAMETROS_DE_SISTEMA = "dtp.infra.batch.PARAMETROS_SISTEMA";
    private static final String MSG_ITEM_PARAMETRO_SISTEMA =
            "dtp.infra.batch.ITEM_PARAMETRO_SISTEMA";
    private static final String MSG_ERRO_ENCONTRANDO_MAIN_CLASS =
            "dtp.infra.batch.ERRO_ENCONTRANDO_MAIN_CLASS";
    private static final String MSG_NOME_DO_PROGRAMA = "dtp.infra.batch.NOME_DO_PROGRAMA";
    private static final String MSG_START_SEPARATOR = "dtp.infra.batch.START_SEPARATOR";
    private static final String MSG_ID_DE_EXECUCAO = "dtp.infra.batch.ID_DE_EXECUCAO";
    private static final String MSG_ERRO_STEP_SEM_MSG = "dtp.infra.batch.ERRO_STEP_SEM_MSG";
    private static final String MSG_FIM_STEP_SEM_DATA = "dtp.infra.batch.FIM_STEP_SEM_DATA";
    private static final String MSG_FIM_FASE_SEM_DATA = "dtp.infra.batch.FIM_FASE_SEM_DATA";
    private static final String MSG_ENTRADA_SAIDA_NAO_NOTIFICADA =
            "dtp.infra.batch.ENTRADA_SAIDA_NAO_NOTIFICADA";
    private static final String MSG_ENTRADA = "dtp.infra.batch.MSG_ENTRADA";
    private static final String MSG_PROCESSAMENTO = "dtp.infra.batch.MSG_PROCESSAMENTO";
    private static final String MSG_SAIDA = "dtp.infra.batch.MSG_SAIDA";
    private static final String MSG_EXIT_DESCRIPTION = "dtp.infra.batch.EXIT_DESCRIPTION";
    private static final String MSG_NOVA_LINHA = "dtp.infra.batch.NOVA_LINHA";    
    private static final String MSG_OUTROS = "dtp.infra.batch.MSG_OUTROS";
    private static final String MSG_VERSION = "dtp.infra.batch.VERSION";
    private static final String MSG_CONTEXTO_EXECUCAO = "dtp.infra.batch.CONTEXTO_EXECUCAO";
    private static final String MSG_CONTEXTO_EXECUCAO_VAZIO = "dtp.infra.batch.CONTEXTO_EXECUCAO_VAZIO";    
    private static final String MSG_CONTEXTO_EXECUCAO_PROPRIEDADE = "dtp.infra.batch.CONTEXTO_EXECUCAO_PROPRIEDADE";

    /*
     * Loggers
     */
    private static final Logger LOGGER = Logger.getLogger(SumarioExecucaoBuilder.class);

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private LogHelper logHelper;

    @Resource
    private MessageSource messageSource;

    @Autowired
    private StepSummerHelper stepSummerHelper;

    private String programName;
    
    @Value("${batch.detailed.execution.context:false}")
    private boolean detailedExecutionContext;

    private void build(final SumarioBuilderCallback builder, final String msg, final Object... params) {
        final String msgForm = this.messageSource.getMessage(
                msg, params, Locale.getDefault());
        builder.build(msgForm);
    }

    /**
     * Gera o sumário completo da execução, incluindo restarts anteriores.
     * @param first Execução para gerar sumário.
     *
     * @return Sumario de execucao, com execucoes de outros restarts.
     */
    public void gerarSumarioCompleto(final SumarioBuilderCallback builder, final JobExecution first) {
        // Obtém execuções anteriores
        final List<JobExecution> execucoes =
                this.jobExplorer.getJobExecutions(first.getJobInstance());

        this.stepSummerHelper.initSummerFor(execucoes);
        try {
            this.gerarCabecalho(first, builder);

            final ListIterator<JobExecution> it =
                    execucoes.listIterator(execucoes.size());
            for (int i = 1; i < execucoes.size(); i++) {
                final JobExecution jobExecution = it.previous();

                this.build(builder, MSG_START_SEPARATOR);
                this.build(builder, MSG_ID_DE_EXECUCAO, i, jobExecution.getId());

                this.gerarSumarioExecucao(jobExecution, builder);
            }

            // Gera o log do ultimo item em separado, pq
            // ele possui atributos de erro a serem exibidos.
            this.build(builder, MSG_START_SEPARATOR);
            this.build(builder, MSG_ID_DE_EXECUCAO, execucoes.size(), first.getId());
            this.gerarSumarioExecucao(first, builder);

        } catch (final Exception e) {
            // Log sem stacktrace para a produção.
            this.logHelper.logDirect(ERRO_INESPERADO);

            // Log com stacktrace para a equipe de desenvolvimento.
            LOGGER.error(ERRO_INESPERADO, e);
        }
    }

    /**
     * Gera o sumário apenas da execução atual. Para um sumário completo
     * use o método {@link #gerarSumarioCompleto(JobExecution)}
     * @param first Execução para gerar sumário.
     *
     * @return Sumario da execucao atual.
     */
    public void gerarSumario(final SumarioBuilderCallback builder, final JobExecution first) {
        // Obtém execuções anteriores
        final List<JobExecution> execucoes =
                this.jobExplorer.getJobExecutions(first.getJobInstance());

        this.stepSummerHelper.initSummerFor(execucoes);
        try {
            this.gerarCabecalho(first, builder);

            this.build(builder, MSG_START_SEPARATOR);
            this.build(builder, MSG_ID_DE_EXECUCAO, execucoes.size(), first.getId());
            this.gerarSumarioExecucao(first, builder);

        } catch (final Exception e) {
            // Log sem stacktrace para a produção.
            this.logHelper.log(ERRO_INESPERADO);

            // Log com stacktrace para a equipe de desenvolvimento.
            LOGGER.error(ERRO_INESPERADO, e);
        }
    }

    private void gerarCabecalho(final JobExecution first, final SumarioBuilderCallback builder) {
        this.build(builder, MSG_NOVA_LINHA);
        this.build(builder, MSG_SUMARIO);
        this.build(builder, MSG_NOME_DO_PROGRAMA, this.programName);
        this.build(builder, MSG_VERSION, getVersion());
        this.build(builder, MSG_NOME_FASE, first.getJobInstance().getJobName());
        this.gerarSumarioParametros(first, builder);
        this.gerarSumarioParametrosSistema(builder);
    }

    private void gerarSumarioExecucao(final JobExecution jobExecution,
            final SumarioBuilderCallback builder) {
        this.build(builder, MSG_INICIO_FASE, jobExecution.getStartTime());
        
       	this.gerarExecutionContext(jobExecution.getExecutionContext(), builder);

        for (final StepExecution stepExecution : this.obterListaStepOrdenada(
                jobExecution.getStepExecutions())) {
            final StepExecution stepCompleto = this.jobExplorer.getStepExecution(
                    jobExecution.getId(), stepExecution.getId());
            this.gerarSumarioStep(stepCompleto, builder);
        }
        this.build(builder, MSG_SEPARATOR);
        if (jobExecution.getEndTime() != null) {
            this.build(builder, MSG_FIM_FASE, jobExecution.getEndTime());
        } else {
            this.build(builder, MSG_FIM_FASE_SEM_DATA);
        }

        String statusFase = jobExecution.getExitStatus().getExitCode();
        if (jobExecution.getStatus().equals(BatchStatus.STARTING)
                || jobExecution.getStatus().equals(BatchStatus.STOPPING)) {
            statusFase += " [" + jobExecution.getStatus() + "]";
        }

        this.build(builder, MSG_STATUS_FASE, statusFase);
    }

    private void gerarExecutionContext(ExecutionContext executionContext, SumarioBuilderCallback builder) {
    	if (detailedExecutionContext) {
	    	if (executionContext.isEmpty()) {
	    		this.build(builder, MSG_CONTEXTO_EXECUCAO_VAZIO);    		
	    	} else {
	    		this.build(builder, MSG_CONTEXTO_EXECUCAO);
	    		
				for (Entry<String, Object> entry: executionContext.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
		
					// Logar apenas chaves que não estão sendo logadas em outros locais.
					if (!key.startsWith(StepInformationHelper.DTP_SUMMARY_PREFIX)) {
						this.build(builder, MSG_CONTEXTO_EXECUCAO_PROPRIEDADE, key, value);
					}
				}	    		
	    	}	    	
    	}
	}

	private Collection<StepExecution> obterListaStepOrdenada(
            final Collection<StepExecution> stepExecutions) {
        Collection<StepExecution> retorno = stepExecutions;
        if (stepExecutions.size() > 1) {
            final Iterator<StepExecution> it = stepExecutions.iterator();
            final StepExecution primeiro = it.next();
            final StepExecution segundo = it.next();
            if (segundo.getId() < primeiro.getId()) {
                // É necessário reverter.
                retorno = new ArrayList<StepExecution>(stepExecutions);
                Collections.reverse((ArrayList<StepExecution>) retorno);
            }

        }
        return retorno;
    }

    private boolean ehDoInteresseDaProducao(final String chave) {
        return "pid".equals(chave);
    }

    private void buildWithTotal(final SumarioBuilderCallback builder, final String msg, final int atual, final int total) {
        if (atual == total) {
            this.build(builder, msg, atual, "");
        } else {
            this.build(builder, msg, " / (" + atual + " nesta execucao)", total);
        }
    }

    private void gerarSumarioStep(final StepExecution stepExecution, final SumarioBuilderCallback builder) {
        this.build(builder, MSG_SEPARATOR);
        this.build(builder, MSG_NOME_STEP, stepExecution.getStepName(), stepExecution.getId());
        this.build(builder, MSG_INICIO_STEP, stepExecution.getStartTime());

        this.gerarSumarioDeIO(stepExecution.getExecutionContext(), builder);

        // Informação sem summer, porque o número de itens lidos/filtrados no total
        // não faz sentido quando há rollbacks.
        this.build(builder, MSG_ITENS_LIDOS, stepExecution.getReadCount());
        this.build(builder, MSG_ITENS_FILTRADOS, stepExecution.getFilterCount());

        //
        this.buildWithTotal(builder, MSG_ITENS_GRAVADOS, stepExecution.getWriteCount(),
                this.stepSummerHelper.getSummerFor(stepExecution).getWriteCount());

        this.build(builder, MSG_ITENS_REJEITADOS, stepExecution.getSkipCount());
        this.build(builder, MSG_COMMIT_COUNT, stepExecution.getCommitCount());
        this.build(builder, MSG_ROLLBACK_COUNT, stepExecution.getRollbackCount());

        this.gerarSumarioCustomizado(stepExecution.getExecutionContext(), builder);

        if (stepExecution.getEndTime() != null) {
            this.build(builder, MSG_FIM_STEP, stepExecution.getEndTime());
        } else {
            this.build(builder, MSG_FIM_STEP_SEM_DATA);
        }
        this.build(builder, MSG_STATUS_STEP, stepExecution.getExitStatus().getExitCode());
        if (StringUtils.hasText(stepExecution.getExitStatus().getExitDescription())) {
            String fullMsg = stepExecution.getExitStatus().getExitDescription();
            fullMsg = fullMsg.replaceAll("\r", "\n");
            fullMsg = fullMsg.replaceAll("\n\n", "\n");

            String shortMsg = fullMsg.split("\n")[0];
            final int pos = shortMsg.indexOf("nested exception");
            if (pos != -1) {
                shortMsg = shortMsg.substring(0, pos);
            }

            this.build(builder, MSG_EXIT_DESCRIPTION, shortMsg);
            builder.buildStacktrace(fullMsg + "\n");
        }

        int contador = 1;
        for (final Throwable t : stepExecution.getFailureExceptions()) {
            if (StringUtils.hasText(t.getMessage())) {
                this.build(builder, MSG_ERRO_STEP, contador, t.getMessage());
            } else {
                this.build(builder, MSG_ERRO_STEP_SEM_MSG, contador, t.getClass().getSimpleName());
            }
            contador++;
        }
        
        gerarExecutionContext(stepExecution.getExecutionContext(), builder);
    }

    private void gerarSumarioCustomizado(final ExecutionContext executionContext,
            final SumarioBuilderCallback builder) {
        for (final Entry<String, Object> entry : executionContext.entrySet()) {
            if (entry.getKey().startsWith(StepInformationHelper.SUMMARY_CUSTOM)) {
                final String key = entry.getKey().substring(
                        StepInformationHelper.SUMMARY_CUSTOM.length() + 1);
                final String value = entry.getValue().toString();

                this.build(builder, MSG_OUTROS, key + "=" + value);
            }
        }
    }

    private void gerarSumarioDeIO(final ExecutionContext executionContext,
            final SumarioBuilderCallback builder) {
        boolean informou = false;

        if (executionContext.containsKey(StepInformationHelper.SUMMARY_INPUT)) {
            final String entrada =
                    executionContext.getString(StepInformationHelper.SUMMARY_INPUT);
            this.build(builder, MSG_ENTRADA, entrada);
            informou = true;
        }

        if (executionContext.containsKey(StepInformationHelper.SUMMARY_PROCESS)) {
            final String processamento =
                    executionContext.getString(StepInformationHelper.SUMMARY_PROCESS);
            this.build(builder, MSG_PROCESSAMENTO, processamento);
            informou = true;
        }

        if (executionContext.containsKey(StepInformationHelper.SUMMARY_OUTPUT)) {
            final String saida =
                    executionContext.getString(StepInformationHelper.SUMMARY_OUTPUT);
            this.build(builder, MSG_SAIDA, saida);
            informou = true;
        }

        if (!informou) {
            LOGGER.warn(ENTRADA_SAIDA_NAO_NOTIFICADA);
            this.build(builder, MSG_ENTRADA_SAIDA_NAO_NOTIFICADA);
        }
    }

    private void gerarSumarioParametros(final JobExecution jobExecution,
            final SumarioBuilderCallback builder) {
        this.build(builder, MSG_PARAMETROS);
        for (final String param : jobExecution.getJobParameters().getParameters()
                .keySet()) {
            this.build(builder, MSG_PARAM, param,
                    jobExecution.getJobParameters()
                            .getParameters().get(param).getValue());
        }
    }

    private void gerarSumarioParametrosSistema(final SumarioBuilderCallback builder) {
        this.build(builder, MSG_PARAMETROS_DE_SISTEMA);
        final Properties p = System.getProperties();
        for (final Entry<Object, Object> entry : p.entrySet()) {
            final String parametro = entry.getKey() + "=" + entry.getValue();

            if (this.ehDoInteresseDaProducao(entry.getKey().toString())) {
                this.build(builder, MSG_ITEM_PARAMETRO_SISTEMA, parametro);
            }
        }
    }

    @PostConstruct
    public void initProgramName() {
        try {
            final Class<?> applicationMainClass = this.obterMainClass();

            String local = applicationMainClass.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().toString();

            local = local.replaceFirst("file:", "");
            this.programName = local;
        } catch (final ClassNotFoundException e) {
            this.logHelper.log(MSG_ERRO_ENCONTRANDO_MAIN_CLASS);
        } catch (final URISyntaxException e) {
            this.logHelper.log(MSG_ERRO_ENCONTRANDO_MAIN_CLASS);
        }
    }

    private Class<?> obterMainClass() throws ClassNotFoundException {
        final Exception e = new Exception();
        final StackTraceElement ste = e.getStackTrace()[e.getStackTrace().length - 1];

        return Class.forName(ste.getClassName());
    }
    
    //ISSUE #33 : http://www-git/framework-dataprev/company-pom/issues/33
    private String getVersion() {
        Package aPackage = getClass().getPackage();
        String version = null;
        version = aPackage.getImplementationVersion() != null ? aPackage.getImplementationVersion() :  
        			aPackage.getSpecificationVersion() != null ? aPackage.getSpecificationVersion() : 
        				"nao definida";
        return version;
    }
}
