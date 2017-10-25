package br.gov.dataprev.sibe.batch.importbatim;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import br.gov.dataprev.infra.batch.support.MainHelper;

/**
 * Classe responsavel por tratar e passar os parametros de linha de comando ao Spring Batch.
 *
 * @author DATAPREV/DIT/DEAT
 */
public final class MainClass {

    private static final Logger LOG = Logger.getLogger(MainClass.class);

    private static final String MSG_INIT_ERROR = "Erro geral. "
            + "Mais detalhes no LOG de desenvolvimento: [%s]. ";

    private MainClass() {
    }

    /** XML contendo o contexto da aplicacao Batch. */
    private static final String JOB_CONTEXT = "/config/jobs/job-main-context.xml";

    /** XML contendo definicao do pool dataSource. */
    private static final String DATA_SOURCE_POOL = "/config/jobs/data-source-pool.xml";

    /**
     * Inicia o DtpInfraCore e o Spring.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        if (args.length < 1 || "help".equals(args[0])) {
            MainHelper.printHelpText("/help.txt", Charset.forName("ISO-8859-1"));
        } else {
            int returnCode = 1;

            try {
                MainHelper.initDtpInfraCore();
                returnCode = MainHelper.iniciarJob(JOB_CONTEXT, DATA_SOURCE_POOL, args);

                // Caso necessario salvar o sumario de execucao, o mesmo
                // esta disponivel no metodo "MainHelper.getLastSummaryResult();"
            } catch (final Throwable th) {
                returnCode = 1;

                final String msg = String.format(MSG_INIT_ERROR, th.getMessage());
                LOG.fatal(msg, th);

                System.out.println(msg);
            } finally {
                MainHelper.safeSystemExit(returnCode);
            }
        }
    }
}
