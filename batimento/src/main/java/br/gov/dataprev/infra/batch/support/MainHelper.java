package br.gov.dataprev.infra.batch.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;

import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.gov.dataprev.infra.batch.support.internal.ShutdownHook;
import br.gov.dataprev.sibe.batch.importbatim.utils.CachedConfigurationManager;

/**
 * Classe de ajuda na construção das classes Main das aplicações.
 * <p>
 * Para iniciar jobs use o DtpJobStarter
 *
 * @see DtpJobStarter
 * @author DATAPREV/DIT/DEAT
 */
public final class MainHelper {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MainHelper.class);

    private static final String ERRO_TEXTO_AJUDA = "Erro abrindo texto de ajuda";
    
    private static ApplicationContext applicationContext;
    
    /**
     * Proíbe instanciação.
     */
    private MainHelper() {
    }

    /**
     * Imprime um texto de ajuda no console.
     *
     * @param resource
     * Recurso a ser lido do classpath.
     * @param charSet
     * Charset utilizado pelo recurso.
     */
    public static void printHelpText(final String resource, final Charset charSet) {
        try {
            final InputStream is = MainHelper.class.getResourceAsStream(resource);
            final InputStreamReader reader = new InputStreamReader(is, charSet);
            final LineNumberReader lineReader = new LineNumberReader(reader);
            try {
                String linha = lineReader.readLine();
                while (linha != null) {
                    System.out.println(linha);
                    linha = lineReader.readLine();
                }
            } finally {
                lineReader.close();
            }
        } catch (final IOException e) {
            // Saida para a produção (sem stacktrace).
            System.out.println(ERRO_TEXTO_AJUDA);

            // Log com a exception, para os desenvolvedores.
            LOG.error(ERRO_TEXTO_AJUDA, e);

            System.exit(ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR);
        }
    }

    /**
     * Inicializa o DtpInfraCore sem imprimir no console.
     *
     * @see MainHelper#initDtpInfraCore(boolean)
     */
    public static void initDtpInfraCore() {
        initDtpInfraCore(false);
    }

    /**
     * Inicializa o DtpInfraCore através da chamada: <p>
     * <code>
     * ConfigurationManager.getInstance().getAllConfigurations()
     * </code>
     * <p>
     * Como a chamada acima gera log no console, este método permite que o batch
     * desabilite o log através do parâmetro initialLog.
     * <p>
     * @param initialLog Se o log de inicialização do DtpInfraCore deve ser habilitado.
     */
    public static void initDtpInfraCore(final boolean initialLog) {
        System.out.println("Inicializando o DtpInfraCore.");

        final ShutdownHook sh = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(sh);
        CachedConfigurationManager.getInstance();
        System.out.println("DtpInfraCore inicializado.");
    }

    /**
     * Informa o código de saída de maneira segura à produção.
     * <p>
     * A chamada a este método é preferível ao System.exit, porque
     * ele está alinhado ao ShutdownHook do JDK.
     *
     * @param code Codigo de saida.
     */
    public static void safeSystemExit(final int code) {
        ShutdownHook.setExitCode(code);
        System.exit(code);
    }

    /**
     * @deprecated Usar {@link #iniciarJob(ApplicationContext, String[])}
     */
    @Deprecated
    public static String[] prepararParametroProcesso(final ApplicationContext context,
            final String[] args) {
        throw new UnsupportedOperationException("DEPRECATED! Use o método iniciarJob()");
    }

    /**
     * Inicia o Job preparando o parâmetro de processo, caso não tenha sido informado.
     * Deprecated - Utilize iniciarJob(contextLocation, args)
     *
     * @param applicationContext Contexto do Spring
     * @param args Argumentos enviados pela linha de comando
     * @return Saida de retorno para o S.O.
     * @throws Exception Se ocorrer algum erro durante a incialização do Spring.
     *
     * @see DtpJobStarter
     */
    @Deprecated
    public static int iniciarJob(final ApplicationContext context, final String[] args)
            throws Exception {
    	MainHelper.applicationContext = context;
    	
    	final DtpJobStarter jobStarter = context.getBean(DtpJobStarter.class);

        final int returnCode = jobStarter.startJobWithArgs(args);

        return returnCode;
    }

    /**
     * Inicia o Job preparando o parâmetro de processo, caso não tenha sido informado.
     *
     * @param contextLocation Caminho do arquivo de Contexto do Spring
     * @param dataSourcePoolLocation Caminho do arquivo xml com definição do pool de datasource
     * @param args Argumentos enviados pela linha de comando
     * @return Saida de retorno para o S.O.
     * @throws Exception Se ocorrer algum erro durante a incialização do Spring.
     *
     * @see DtpJobStarter
     */
    public static int iniciarJob(final String contextLocation, final String dataSourcePoolLocation, final String[] args)
            throws Exception {
    	DtpJobStarter.setARGS(args); // Permite atribuição dos argumentos de linha de comando a Spring Expressions no XML do contexto
    								 // Atribuição necessária antes de carregar o XML do contexto

        // Permite sobrescrever o datasource do framework se informado
    	if (dataSourcePoolLocation != null) {
    		applicationContext = new ClassPathXmlApplicationContext(
    				contextLocation, dataSourcePoolLocation); 
    	} else {
    		applicationContext = new ClassPathXmlApplicationContext(contextLocation);
    	}

    	final DtpJobStarter jobStarter = applicationContext.getBean(DtpJobStarter.class);

        final int returnCode = jobStarter.startJobWithArgs(args);

        ((ClassPathXmlApplicationContext)applicationContext).close();
        
        return returnCode;
    }

    /**
     * Inicia o Job preparando o parâmetro de processo, caso não tenha sido informado.
     *
     * @param contextLocation Caminho do arquivo de Contexto do Spring
     * @param args Argumentos enviados pela linha de comando
     * @return Saida de retorno para o S.O.
     * @throws Exception Se ocorrer algum erro durante a incialização do Spring.
     *
     * @see DtpJobStarter
     */
    public static int iniciarJob(final String contextLocation, final String[] args)
            throws Exception {
    	return iniciarJob(contextLocation, null, args);
    }

    /**
     * Recupera o Sumário de execução em String. Este método deve ser chamado ao
     * final da execução do job.
     * <p>
     * É útil nos casos onde deve ser gerado um arquivo com o sumário de execução.
     *
     * @param applicationContext Contexto do Spring
     * @return Saida de retorno para o S.O.
     */
    @SuppressWarnings("deprecation")
    public static String getLastSummaryResult(final ApplicationContext context)
            throws Exception {
        final ProductionHelper pHelper = context.getBean(ProductionHelper.class);

        return pHelper.getLastSummaryResult();
    }
    
    
    /**
     * Recupera o Sumário de execução em String. Este método deve ser chamado ao
     * final da execução do job.
     * <p>
     * É útil nos casos onde deve ser gerado um arquivo com o sumário de execução.
     *
     * @return Saida de retorno para o S.O.
     */
    @SuppressWarnings("deprecation")
    public static String getLastSummaryResult()
            throws Exception {
        final ProductionHelper pHelper = applicationContext.getBean(ProductionHelper.class);

        return pHelper.getLastSummaryResult();
    }    

}
