package br.gov.dataprev.infra.batch.support;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.exception.BatchException;
import br.gov.dataprev.infra.batch.exception.IncorrectConfigurationException;
import br.gov.dataprev.infra.batch.jmx.internal.JmxManager;
import br.gov.dataprev.infra.batch.support.internal.ForceStartHelper;
import br.gov.dataprev.infra.batch.support.internal.ProcessManager;
import br.gov.dataprev.infra.batch.support.internal.ShutdownHook;

/**
 * Bean do Spring para inicializacao de jobs, conforme o padrão Dataprev.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component("dtpJobStarter")
public class DtpJobStarter {

	// Permite atribuição dos argumentos de linha de comando a Spring Expressions no XML do contexto
    public static String[] ARGS = {}; 
	
    /* CONSTANTES */
    private static final String MSG_JOB_NAO_EXISTE = "dtp.infra.batch.JOB_NAO_EXISTE";
    private static final String MSG_JOB_NAO_REINICIAVEL = "dtp.infra.batch.JOB_NAO_REINICIAVEL";
    private static final String MSG_JOB_JA_FINALIZADO = "dtp.infra.batch.JOB_JA_FINALIZADO";
    private static final String MSG_PARAMETRO_INVALIDO = "dtp.infra.batch.PARAMETRO_INVALIDO";
    private static final String MSG_JOB_INSTANCE_EM_EXECUCAO =
            "dtp.infra.batch.JOB_INSTANCE_EM_EXECUCAO";
    private static final String MSG_ERRO_GERAL = "dtp.infra.batch.ERRO_GERAL";
    private static final String MSG_JOB_FINALIZADO = "dtp.infra.batch.JOB_FINALIZADO";
    private static final String MSG_JOB_NAO_ESTA_EM_EXECUCAO =
            "dtp.infra.batch.JOB_NAO_ESTA_EM_EXECUCAO";
    private static final String MSG_JOB_INTERROMPIDO = "dtp.infra.batch.JOB_INTERROMPIDO";
    private static final String MSG_STOP_JA_SOLICITADO = "dtp.infra.batch.STOP_JA_SOLICITADO";
    private static final String MSG_ERRO_OPCAO_INVALIDA = "dtp.infra.batch.ERRO_OPCAO_INVALIDA";
    private static final String MSG_NENHUMA_OPCAO_INFORMADA =
            "dtp.infra.batch.NENHUMA_OPCAO_INFORMADA";
    private static final String MSG_JOB_NAO_FOI_INICIADO = "dtp.infra.batch.JOB_NAO_FOI_INICIADO";
    private static final String MSG_PURGE_MESES_OBRIGATORIO =
            "dtp.infra.batch.PURGE_MESES_OBRIGATORIO";
    private static final String MSG_PURGE_ERRO = "dtp.infra.batch.PURGE_ERRO";
    private static final String MSG_INICIANDO_MODO_EXECUCAO =
            "dtp.infra.batch.INICIANDO_MODO_EXECUCAO";
    private static final String MSG_NOVA_LINHA = "dtp.infra.batch.NOVA_LINHA";
    private static final String MSG_LOG_EXECUCAO = "dtp.infra.batch.LOG_EXECUCAO";

    private static final String MSG_DISK_SPACE_INITIALIZER =
            "A fase não verificou o espaco em disco com o componente DiskSpaceInitializer";

    private static final Logger LOG = LogManager.getLogger(DtpJobStarter.class);

    /* Opcoes de job. */
    public static final String OPCAO_START = "-start".toLowerCase();
    public static final String OPCAO_STOP = "-stop".toLowerCase();
    public static final String OPCAO_FORCE_START = "-forceStart".toLowerCase();
    public static final String OPCAO_SHOW_SUMMARY = "-showSummary".toLowerCase();
    public static final String OPCAO_PURGE = "-purge".toLowerCase();
    public static final String OPCAO_FORCE_PURGE = "-forcePurge".toLowerCase();
    public static final String OPCAO_AUTO_START = "-autoStart".toLowerCase();
    public static final String OPCAO_AUTO_STOP = "-autoStop".toLowerCase();

    private static final String OPCAO_DEFAULT = OPCAO_FORCE_START;

    private static final Set<String> OPCOES_SET = new HashSet<String>();
    {
        OPCOES_SET.add(OPCAO_START);
        OPCOES_SET.add(OPCAO_STOP);
        OPCOES_SET.add(OPCAO_FORCE_START);
        OPCOES_SET.add(OPCAO_SHOW_SUMMARY);
        OPCOES_SET.add(OPCAO_PURGE);
        OPCOES_SET.add(OPCAO_FORCE_PURGE);
        OPCOES_SET.add(OPCAO_AUTO_START);
        OPCOES_SET.add(OPCAO_AUTO_STOP);
    }
    
    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProcessManager processManager;

    @Autowired
    private JobLauncher launcher;

    @Autowired
    private JobLocator jobLocator;

    @Autowired
    private JobOperator jobOperator;
    
    @Autowired
    private ForceStartHelper forceStartHelper;
    
    @Resource(name = "dtpJobParametersSet")
    private Set<DtpJobParametersBean> dtpJobParamsSet;

    @Resource(name = "dtpExitCodeMapper")
    private ExitCodeMapper exitCodeMapper;

    @Autowired
    private ProductionHelper productionHelper;

    @Autowired
    private JmxManager jmxManager;
    
    /**
     * Inicia um Job do spring batch no modo forceStart.
     *
     * @param jobName
     * Job a ser executado
     * @param parameters
     * Parametros do job
     * @return Saida de retorno para o S.O.
     */
    public int start(final String jobName, final String[] parameters) {
        this.productionHelper.log(MSG_NENHUMA_OPCAO_INFORMADA);
        return safeStartCall(jobName, OPCAO_DEFAULT, parameters);
    }

    /**
     * Inicia um Job do spring batch. As opcoes são especificadas pelo
     * método {@link DtpJobStarter#startJobWithArgs(String[])}
     *
     * @param jobName
     * Job a ser executado
     * @param opcao
     * Parametro de inicializacao do job.
     * @param parameters
     * Parametros do job
     * @return Saida de retorno para o S.O.
     * @throws BatchException Se houver erros na execução.
     */
    public int start(final String jobName, final String opcao, final String[] parameters)
            throws BatchException {
        checkFreeSpace();
        this.productionHelper.log(MSG_NOVA_LINHA);
        this.productionHelper.log(MSG_LOG_EXECUCAO);

        String opcaoMinuscula = opcao.toLowerCase();

        if (!OPCOES_SET.contains(opcaoMinuscula)) {
            this.productionHelper.log(MSG_ERRO_OPCAO_INVALIDA, opcao);
            return ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR;
        }

        this.productionHelper.log(MSG_INICIANDO_MODO_EXECUCAO, opcao);

        // Gera os parâmetros
        final JobParameters jobParameters =
                this.processManager.tratarNumeroProcesso(jobName, parameters, opcaoMinuscula);

        // Avalia opções de administração
        if (OPCAO_PURGE.equals(opcaoMinuscula)) {
            return doPurge(jobName, jobParameters, false);
        } else if (OPCAO_FORCE_PURGE.equals(opcaoMinuscula)) {
            return doPurge(jobName, jobParameters, true);
        } else if (OPCAO_SHOW_SUMMARY.equals(opcaoMinuscula)) {
            return doShowSummary(jobName, jobParameters);
        }

        // Redefine as opções automáticas (uma vez que o parâmetro processo já terá sido gerado).
        if (OPCAO_AUTO_STOP.equals(opcaoMinuscula)) {
            opcaoMinuscula = OPCAO_STOP;
        } else if (OPCAO_AUTO_START.equals(opcaoMinuscula)) {
            opcaoMinuscula = OPCAO_FORCE_START;
        }

        // Avalia as opções de execucao
        if (OPCAO_STOP.equals(opcaoMinuscula)) {
            return doStop(jobName, jobParameters);
        } else {
            if (OPCAO_FORCE_START.equals(opcaoMinuscula)) {
            	forceStartHelper.doForceStart(jobName, jobParameters);
            } else if (!OPCAO_START.equals(opcaoMinuscula)) {
                this.productionHelper.log(MSG_ERRO_GERAL);
                throw new IncorrectConfigurationException("Opção inesperada: "
                        + opcao);
            }

            fixStatus(jobName, jobParameters);

            return doStart(jobName, jobParameters);
        }
    }

    private void checkFreeSpace() throws BatchException {
        if (!ShutdownHook.getSpaceChecked()) {
            System.out.println(MSG_DISK_SPACE_INITIALIZER);
            throw new BatchException(MSG_DISK_SPACE_INITIALIZER);
        }
    }

    private int doPurge(final String jobName, final JobParameters jobParameters,
            final boolean forcar) throws BatchException {
        final String mesesStr = jobParameters.getString("meses");
        int meses;
        try {
            meses = Integer.parseInt(mesesStr);
        } catch (final Exception e) {
            this.productionHelper.log(MSG_PURGE_MESES_OBRIGATORIO);
            return ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR;
        }

        final Date hoje = new Date();

        try {
            this.productionHelper.expurgar(jobName, meses, hoje, forcar);
            return ExitCodeMapper.JVM_EXITCODE_COMPLETED;
        } catch (final Exception e) {
            this.productionHelper.log(MSG_PURGE_ERRO, jobName);
            throw new BatchException(e);
        }
    }

    private void fixStatus(final String jobName, final JobParameters jobParameters) {
        JobExecution lastExecution = this.jobRepository.getLastJobExecution(jobName,
                jobParameters);

        if (lastExecution != null) {
            LOG.debug("Verificando integridade do metadados do SpringBatch");

            lastExecution = this.jobExplorer.getJobExecution(lastExecution.getId());

            for (final StepExecution stepExecution : lastExecution.getStepExecutions()) {
                final BatchStatus status = stepExecution.getStatus();

                if (status == BatchStatus.STOPPED || status == BatchStatus.FAILED) {
                	forceStartHelper.fixJiraBatch1798(stepExecution);
                }
            }
        }
    }

    private int doStart(final String jobName, final JobParameters jobParameters)
            throws BatchException {
        try {
            this.jmxManager.registerJob(jobName, jobParameters);

            // Executa o job
            final Job job = this.jobLocator.getJob(jobName);
            final JobExecution jobExecution = this.launcher.run(job, jobParameters);
            final String exitCode = jobExecution.getExitStatus().getExitCode();
            this.productionHelper.log(MSG_JOB_FINALIZADO, exitCode);

            // Prepara o sumário.
            //
            // Obtem o JobExecution através do JobExplorer para garantir
            // que os stepExecutions estejam sincronizados com o banco.
            final JobExecution updatedJobExecution =
                    this.jobExplorer.getJobExecution(jobExecution.getId());
            this.productionHelper.gerarSumario(updatedJobExecution);

            // Mapeia o resultado para a saída da JVM.
            return this.exitCodeMapper.intValue(exitCode);
        } catch (final NoSuchJobException e) {
            this.productionHelper.log(MSG_JOB_NAO_EXISTE, jobName);

            throw new BatchException(e);
        } catch (final JobExecutionAlreadyRunningException e) {
            this.productionHelper.log(MSG_JOB_INSTANCE_EM_EXECUCAO);

            throw new BatchException(e);
        } catch (final JobRestartException e) {
            this.productionHelper.log(MSG_JOB_NAO_REINICIAVEL, jobName);

            throw new BatchException(e);
        } catch (final JobInstanceAlreadyCompleteException e) {
            this.productionHelper.log(MSG_JOB_JA_FINALIZADO, jobName);

            throw new BatchException(e);
        } catch (final JobParametersInvalidException e) {
            this.productionHelper.log(MSG_PARAMETRO_INVALIDO, e.getMessage());

            throw new BatchException(e);
        } catch (final Exception e) {
            this.productionHelper.log(MSG_ERRO_GERAL);
            throw new BatchException(e);
        }
    }

    private int doShowSummary(final String jobName, final JobParameters jobParameters) {
        // Verifica se já não existe uma instancia desse job em execução
        JobExecution lastExecution = this.jobRepository.getLastJobExecution(jobName,
                jobParameters);

        if (lastExecution != null) {
            // Obtem mais informações
            lastExecution = this.jobExplorer.getJobExecution(lastExecution.getId());

            this.productionHelper.gerarSumarioCompleto(lastExecution);
            return ExitCodeMapper.JVM_EXITCODE_COMPLETED;
        } else {
            // Não existe execução.
            this.productionHelper.log(MSG_JOB_NAO_FOI_INICIADO, jobName);
            return ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR;
        }
    }

    private int doStop(final String jobName, final JobParameters jobParameters)
            throws BatchException {
        // Verifica se já não existe uma instancia desse job em execução
        final JobExecution lastExecution = this.jobRepository.getLastJobExecution(jobName,
                jobParameters);

        if (lastExecution != null) {
            try {
                this.jobOperator.stop(lastExecution.getId());
            } catch (final NoSuchJobExecutionException e) {
                this.productionHelper.log(MSG_JOB_NAO_EXISTE, jobName);
                throw new BatchException(e);
            } catch (final JobExecutionNotRunningException e) {
                if (lastExecution.getStatus().equals(BatchStatus.STOPPING)) {
                    this.productionHelper.log(MSG_STOP_JA_SOLICITADO, jobName);
                } else {
                    this.productionHelper.log(MSG_JOB_NAO_ESTA_EM_EXECUCAO, jobName);
                }
                throw new BatchException(e);
            }

            this.productionHelper.log(MSG_JOB_INTERROMPIDO, jobName);
            return ExitCodeMapper.JVM_EXITCODE_COMPLETED;
        } else {
            // Não existe execução.
            this.productionHelper.log(MSG_JOB_NAO_FOI_INICIADO, jobName);
            return ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR;
        }
    }

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
    	forceStartHelper.forceStop(id);
    }

    private static final int INICIO_PARAMETROS = 1;

    /**
     * Helper para iniciar jobs com o array de argumentos vindos da linha de comando.
     * Espera-se que os argumentos venham no seguinte formato:
     * <p>
     * <code>"JOBNAME propriedade1=valor1 propriedade2=valor2 ... [-opcao]"</code>
     * <p>
     * Por exemplo: <code>eleicaoJob eleitorFile=file:./eleitores.csv</code>
     * <p>
     * O retorno para o S.O pode ser configurado pela classe {@link DtpExitCodeMapper}. O parâmetro
     * opcao é opcional e aceita as opções definidas :
     * <p>
     * <table border="1" cellpadding="5">
     * <tr>
     * <td>start</td>
     * <td>Inicia ou reinicia o Job</td>
     * </tr>
     * <tr>
     * <td>stop</td>
     * <td>Interrompe o job normalmente, permitindo que o próximo restart não seja forçado.</td>
     * </tr>
     * <tr>
     * <td>forceStart</td>
     * <td>Inicia um Job mesmo se o status for STARTED. Necessário se o job anterior foi matado pelo
     * S.O.</td>
     * </tr>
     * <tr>
     * <td>showSummary</td>
     * <td>Exibe o sumário de execução no console.</td>
     * </tr>
     * <tr>
     * <td>purge</td>
     * <td>Expurga informações de execucao do Spring anteriores ao número de meses especificados.
     * Por exemplo: Para expurgar dados com mais de 6 meses de eleicaoJob use
     * "eleicaoJob meses=6 -purge"</td>
     * </tr>
     * <tr>
     * <td>autoStart</td>
     * <td>Se o número do processo foi informado, funciona igual ao -forceStart. Caso contrário 
     * prepara o restart da última execução (caso a mesma seja reiniciável). Se a execução 
     * anterior já foi completada, inicia um novo processo do inicio. 
     * (Atenção! Funcionalidade disponível apenas na versão 4.0.1 ou superior)</td>
     * </tr>
     * <td>autoStop</td>
     * <td>Se o número do processo foi informado, funciona igual ao -stop. Caso contrário 
     * solicita que a última execução em andamento seja encerrado no próximo commit. 
     * (Atenção! Funcionalidade disponível apenas na versão 4.0.1 ou superior)</td>
     * </tr>
     * </table>
     * <p>
     * "-forceStart" será assumido como default se nenhum dos parametros for informado.
	 *
     * <p>
     * Também é possível iniciar o job sem passar nenhum argumento e configurando o bean DtpJobParametersBean
     * contendo os parâmetros de entrada. O bean só é utilizado se nenhum argumento for informado.
     * 
     * <p>
     * <b>Observação:</b> Na ausência total de argumentos (ou seja, args.length==0 e não houver bean de parâmetros), o método irá
     * imprimir o arquivo de ajuda (help.txt) no console utilizando o Encoding ISO-8859-1.
     * <p>
     *
     * @param args
     * Argumentos vindos da linha de comando.
     * @return Saida de retorno para o S.O.
     * @see DtpExitCodeMapper
     * @see MainHelper
     */
    public int startJobWithArgs(String[] args) {
    	if (args.length == 0 || "help".equals(args[0])) { // Se não recebi parâmetros ou solicitou help
            MainHelper.printHelpText("help.txt", Charset.forName("ISO-8859-1"));
            return ExitCodeMapper.JVM_EXITCODE_COMPLETED;
    	}

        final String jobName = args[0];

        // Determino a forma de execução. Se a chamada passou algum argumento no estilo 'propriedade=valor', então
        // executo o job na forma tradicional e desconsidero bean de parametros.
        Boolean legacyMode = false;
        for (int i = 1; i < args.length; i++) { //i=1, desconsidera primeiro argumento que é o nome do job
    		if (args[i].contains("=")) {
    			LOG.debug("Pares de chave=valor informados como argumentos. Utilizando execucao tradicional");
    			legacyMode = true;
    			break;
    		}
    	}

        if (!legacyMode) { // Se espera-se execução na nova forma, utilizando bean de parâmetros
        	DtpJobParametersBean params = getJobParams(jobName);
	        if (params != null) { // Espera-se que o bean de parâmetros tenha sido configurado
	        	LOG.warn("Executando o job a partir do bean de parametros");
	        	args = params.getParametersAsArgs(); // Utilizo o bean como argumento de entrada
	        } else {
	        	LOG.warn("Execucao sem argumentos"); // deixo executar sem argumentos
	        }
        }

        // Separa os argumentos especiais
        int fimParametros;
        String opcao = args[args.length - 1];
        if (!opcao.startsWith("-")) {
            this.productionHelper.log(MSG_NENHUMA_OPCAO_INFORMADA);
            opcao = OPCAO_DEFAULT;
            fimParametros = 0;
        } else {
            fimParametros = 1;
        }

        // Separa os parametros do Spring em outro Array.
        final String[] parameters = new String[args.length - INICIO_PARAMETROS
                - fimParametros];
        System.arraycopy(args, INICIO_PARAMETROS, parameters, 0, args.length
                - INICIO_PARAMETROS - fimParametros);

        return safeStartCall(jobName, opcao, parameters);
    }
    
    /**
     * Método assessor para obtenção do bean de parâmetros do job chamado
     * @param jobName
     */
    private DtpJobParametersBean getJobParams(String jobName) {
    	DtpJobParametersBean param = null;
    	for (DtpJobParametersBean bean : dtpJobParamsSet) {
			if (jobName.equals(bean.getJobName())) {
				if (bean.validaConfig()) { // Aproveito e valido o bean de parâmetros
					param = bean;
				}
			}
		}
    	return param;
    }

    /**
     * Método de conveniência para iniciar jobs sem precisar
     * de criar um array de String.
     * <p>
     * Esse método apenas delega para o outro método start com
     * <p>
     * <code>
     * return start(jobName, commandLine.split(" "));
     * </code>
     *
     * @param jobName
     * Job a ser executado
     * @param commandLine
     * Parametros para o job separados por espaços.
     * Ex: "param1=a param2=b"
     * @return Saida de retorno para o S.O.
     * @see DtpJobStarter#start(String, String[])
     */
    public int start(final String jobName, final String commandLine) {
        try {
            return start(jobName, commandLine.split(" "));
        } catch (final Exception e) {
            LOG.error("Ocorreu erro ao iniciar o job", e);
            return ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR;
        }
    }

    /**
     * Método de conveniência para iniciar jobs sem precisar
     * de criar um array de String.
     * <p>
     * Esse método apenas delega para o outro método start com
     * <p>
     * <code>
     * return start(jobName, opcao, commandLine.split(" "));
     * </code>
     *
     * @param jobName
     * Job a ser executado
     * @param opcao
     * Opcao de execucao selecionada. Ex: "-start", "-stop".
     * @param commandLine
     * Parametros para o job separados por espaços.
     * Ex: "param1=a param2=b"
     * @return Saida de retorno para o S.O.
     * @see DtpJobStarter#start(String, String[])
     */
    public int start(final String jobName, final String opcao, final String commandLine) {
        return safeStartCall(jobName, opcao, commandLine.split(" "));
    }

    private int safeStartCall(final String jobName, final String opcao, final String[] parameters) {
        try {
            return start(jobName, opcao, parameters);
        } catch (final Exception e) {
            LOG.error("Ocorreu erro ao iniciar o job", e);

            if (!(e instanceof BatchException)) {
                this.productionHelper.log(MSG_ERRO_GERAL);
            }

            return ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR;
        }
    }
 
    
	public static void setARGS(String[] arguments) {
		ARGS = arguments;
	}
	
	public static void setARGS(String commandLine) {
		ARGS = commandLine.split(" ");
	}

}
