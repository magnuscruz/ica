package br.gov.dataprev.infra.batch.support.internal;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import br.gov.dataprev.infra.batch.exception.BatchException;
import br.gov.dataprev.infra.batch.exception.BatchRuntimeException;
import br.gov.dataprev.infra.batch.exception.IncorrectConfigurationException;
import br.gov.dataprev.infra.batch.support.DtpJobStarter;
import br.gov.dataprev.infra.batch.support.ProductionHelper;

/**
 * <b>Aten??o!</b> Classe interna do framework. Uso n?o recomendado, pois pode
 * sofrer modifica??o severa sem notifica??o pr?via.
 * <p>
 * O componente ProcessManager ? respons?vel gerar um novo n?mero de processo,
 * caso o par?metro n?o tenha sido informado.
 * <p>
 * Ele gerencia tamb?m tudo o que for relacionado ao par?metro processo.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component
public class ProcessManager {

    private static final String MSG_GERANDO_NUMERO_PROCESSO =
            "dtp.infra.batch.GERANDO_NUMERO_PROCESSO";
    private static final String MSG_ERRO_PROCESSANDO_PARAMETROS =
            "dtp.infra.batch.ERRO_PROCESSANDO_PARAMETROS";
    private static final String MSG_INCLUINDO_PARAMETRO_PROCESSO =
            "dtp.infra.batch.INCLUINDO_PARAMETRO_PROCESSO";

    private static final Logger LOG = Logger.getLogger(ProcessManager.class);

    private String SELECT_MAIOR_PROCESSO;
    private String SELECT_ULTIMO_PROCESSO;

    @Autowired
    private SqlProvider sqlProvider;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired
    private LogHelper logHelper;

    @Autowired
    private ProductionHelper productionHelper;

    @PostConstruct
    private void init() {
        this.SELECT_MAIOR_PROCESSO = this.sqlProvider.readQuery("select-maior-processo.sql");
        this.SELECT_ULTIMO_PROCESSO = this.sqlProvider.readQuery("select-ultimo-processo.sql");
    }


    /**
     * Retorna o pr?ximo n?mero de processo associado ao job informado.
     * @param jobName Job no qual o parametro de processo deve ser obtido.
     * @return Valor do par?metro de processo a ser utilizado.
     */
    private Long obterProximoProcessoPara(final String jobName) {
        Long paramProcesso = obterMaiorProcessoPara(jobName);
        if (paramProcesso == null) {
            LOG.debug("Criando primeira execucao do processo.");
            paramProcesso = 1L;
        } else {
            LOG.debug("Criando novo numero de processo: ["+ paramProcesso+ "]");
            paramProcesso = paramProcesso + 1;
        }

        this.logHelper.log(MSG_GERANDO_NUMERO_PROCESSO, paramProcesso.toString());

        return paramProcesso;
    }

    /**
     * Retorna o maior n?mero de processo utilizado com o job informado.
     * @param jobName Nome do job no qual o maior n?mero de processo deve ser obtido.
     * @return Valor do maior par?metro de processo que foi utilizado.
     */
    private Long obterMaiorProcessoPara(final String jobName) {
        if (this.dataSource != null) {
            final JdbcTemplate template = new JdbcTemplate(this.dataSource);

            try {
                return template.queryForObject(this.SELECT_MAIOR_PROCESSO, Long.class, jobName);
            } catch (final DataAccessException e) {
                LOG.debug("Erro obtendo numero do processo. ", e);
            }
        } else {
            LOG.debug("Datasource inexistente para obter o numero do processo.");
        }

        return null;
    }

    private Long getParametroProcesso(final String[] args) {
        for (final String param : args) {
            if (param.startsWith("processo=")) {
                final String processo = param.substring(9);

                final Long numProcesso = getProcessoAsLong(processo);

                return numProcesso;
            }
        }

        return null;
    }

    private Long getProcessoAsLong(final String processo) {
        try {
            return Long.parseLong(processo);
        } catch (final NumberFormatException e) {
            final String msg = "O numero de processo [" + processo + "] ? inv?lido";
            LOG.error(msg, e);
            throw new IncorrectConfigurationException(msg, e);
        }
    }

    /**
     * Trata o n?mero de processo, passado como par?metro.
     * <p>
     *
     * @param jobName Nome do job a ser executado
     * @param jobParameters Parametros do job
     * @param opcaoMinuscula opcao de Execucao.
     * @return
     * @throws BatchException
     */
    public JobParameters tratarNumeroProcesso(final String jobName, String[] parameters,
            final String opcaoMinuscula) throws BatchException {

        Long numProcesso = getParametroProcesso(parameters);
        if (numProcesso == null) {
            LOG.info("O parametro processo n?o foi informado.");

            // Avalia op??es de administra??o
            if (DtpJobStarter.OPCAO_SHOW_SUMMARY.equals(opcaoMinuscula)) {
                final ProcessoStatus ps = obterUltimoProcessoPara(jobName);
                if (ps != null) {
                    parameters = incluirParametroProcesso(getProcessoAsLong(ps.processo), parameters);
                }
            } else {

                // Op??es autom?ticas
                if (DtpJobStarter.OPCAO_AUTO_START.equals(opcaoMinuscula)) {
                    numProcesso = tratarAutoStart(jobName, opcaoMinuscula);

                    parameters = incluirParametroProcesso(numProcesso, parameters);
                } else if (DtpJobStarter.OPCAO_AUTO_STOP.equals(opcaoMinuscula)) {
                    numProcesso = tratarAutoStop(jobName, opcaoMinuscula);

                    parameters = incluirParametroProcesso(numProcesso, parameters);
                } else if (DtpJobStarter.OPCAO_FORCE_START.equals(opcaoMinuscula)) {
                    // Gerar novo n?mero do processo.
                    numProcesso = obterProximoProcessoPara(jobName);

                    parameters = incluirParametroProcesso(numProcesso, parameters);
                } else if (DtpJobStarter.OPCAO_START.equals(opcaoMinuscula)) {
                    // Gerar novo n?mero do processo.
                    numProcesso = obterProximoProcessoPara(jobName);

                    parameters = incluirParametroProcesso(numProcesso, parameters);
                }
            }
        } else {
            if (numProcesso == 0L) {
                LOG.info("Removendo par?metro [processo=0].");
                final String[] newParameters = new String[parameters.length-1];
                for (int i = 0, j = 0; i < parameters.length; i++) {
                    if (parameters[i].startsWith("processo=")) {
                        continue;
                    }
                    newParameters[j++]=parameters[i];
                }

                return tratarNumeroProcesso(jobName, newParameters, opcaoMinuscula);
            }
        }

        final JobParameters jobParameters = criarJobParametersDe(parameters);
        return jobParameters;
    }

    private Long tratarAutoStart(final String jobName, final String opcaoMinuscula) {
        LOG.info("Preparando o start automatico");

        final ProcessoStatus processoStatus = obterUltimoProcessoPara(jobName);

        if (processoStatus == null) {
            LOG.debug("Criando primeira execucao do processo.");
            return 1L;
        }

        Long numProcesso;

        if (BatchStatus.COMPLETED.name().equals(processoStatus.status)) {
            LOG.info("A execu??o anterior completou com sucesso. Iniciando uma nova execu??o");

            numProcesso = obterProximoProcessoPara(jobName);
        } else {
            numProcesso = getProcessoAsLong(processoStatus.processo);
        }

        return numProcesso;
    }

    private JobParameters criarJobParametersDe(final String[] parameters) throws BatchException {
        try {
            final JobParametersConverter jobParametersConverter =
                    new DefaultJobParametersConverter();
            final JobParameters jobParameters = jobParametersConverter.getJobParameters(StringUtils
                    .splitArrayElementsIntoProperties(parameters, "="));

            return jobParameters;
        } catch (final Exception e) {
            this.productionHelper.log(MSG_ERRO_PROCESSANDO_PARAMETROS);
            throw new BatchException(e);
        }
    }

    private String[] incluirParametroProcesso(final Long paramProcesso, final String[] args) {
        final String[] retorno = new String[args.length + 1];

        System.arraycopy(args, 0, retorno, 0, args.length);

        retorno[args.length] = "processo=" + paramProcesso;

        this.productionHelper.log(MSG_INCLUINDO_PARAMETRO_PROCESSO, paramProcesso.toString());

        return retorno;
    }

    /**
     * Retorna o maior n?mero de processo utilizado com o job informado.
     * @param jobName Nome do job no qual o maior n?mero de processo deve ser obtido.
     * @return Valor do maior par?metro de processo que foi utilizado.
     */
    private ProcessoStatus obterUltimoProcessoPara(final String jobName) {
        if (this.dataSource != null) {
            final JdbcTemplate template = new JdbcTemplate(this.dataSource);

            try {
                final ProcessoStatus retorno = template.queryForObject(this.SELECT_ULTIMO_PROCESSO,
                        new RowMapper<ProcessoStatus>() {
                    public ProcessoStatus mapRow(final ResultSet rs, final int arg1) throws SQLException {
                        final ProcessoStatus retorno = new ProcessoStatus();

                        retorno.processo = rs.getString("NUM_PROCESSO");
                        retorno.status = rs.getString("STATUS");

                        return retorno;
                    }
                }, jobName);

                return retorno;

            } catch (final DataAccessException e) {
                LOG.debug("Erro obtendo numero do processo. ", e);
            }
        } else {
            LOG.debug("Datasource inexistente para obter o numero do processo.");
        }

        return null;
    }

    private Long tratarAutoStop(final String jobName, final String opcaoMinuscula) {
        LOG.info("Preparando o stop automatico");

        final ProcessoStatus processoStatus = obterUltimoProcessoPara(jobName);

        if (processoStatus == null) {
            throw new BatchRuntimeException("N?o foi identificado nenhum processo para ser interrompido. Favor informar o n?mero do processo");
        }

        Long numProcesso;

        if (BatchStatus.COMPLETED.name().equals(processoStatus.status)) {
            final String msg = "O ?ltimo processo ["+ processoStatus.processo+ "] j? foi completado. Favor informar o n?mero do processo";
            LOG.error(msg);
            throw new BatchRuntimeException(msg);
        } else {
            numProcesso = getProcessoAsLong(processoStatus.processo);
        }

        return numProcesso;
    }
}

class ProcessoStatus {
    String processo;
    String status;
}
