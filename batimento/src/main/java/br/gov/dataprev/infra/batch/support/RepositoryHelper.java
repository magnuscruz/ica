package br.gov.dataprev.infra.batch.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Classe de ajuda para a manutenção do repositorio do Spring.
 * <p>
 * Os métodos só estarão disponíveis para aplicações que guardam o estado dos
 * componentes do Spring Batch em tabela.
 *
 * @see ProductionHelper
 * @author DATAPREV/DIT/DEAT
 */
public class RepositoryHelper {

    private static final Logger LOG = Logger.getLogger(RepositoryHelper.class);
    private static final int JOB_INSTANCE_CHUNK_SIZE = 1000;

    @Value("${batch.spring.tablePrefix}")
    private String tablePrefix;

    @Autowired
    private JobExplorer jobExplorer;

    private JdbcTemplate jdbcTemplate;

    private TransactionTemplate transactionTemplate;

    // --------------- Queries ---------------------------
    private String deleteJobInstanceQuery =
            "delete from %PREFIX%JOB_INSTANCE where JOB_INSTANCE_ID ";
    private String deleteJobParamsQuery =
            "delete from %PREFIX%JOB_EXECUTION_PARAMS where ";

    private String deleteJobExecutionQuery =
            "delete from %PREFIX%JOB_EXECUTION where ";
    private String deleteJobExecutionContextQuery =
            "delete from %PREFIX%JOB_EXECUTION_CONTEXT where ";

    private String deleteStepExecutionQuery =
            "delete from %PREFIX%STEP_EXECUTION where ";
    private String deleteStepExecutionContextQuery =
            "delete from %PREFIX%STEP_EXECUTION_CONTEXT where ";

    private String selectStepExecutionsQuery =
            "select STEP_EXECUTION_ID from %PREFIX%STEP_EXECUTION where JOB_EXECUTION_ID=?";

    private String selectExpiredJobInstancesQuery =
            "Select INST.JOB_ID from"
                    + " (select JE.JOB_INSTANCE_ID as JOB_ID, MAX(JE.JOB_EXECUTION_ID) as LAST_JE "
                    + "    from %PREFIX%JOB_EXECUTION JE, %PREFIX%JOB_INSTANCE JI "
                    + "    where JE.JOB_INSTANCE_ID = JI.JOB_INSTANCE_ID "
                    + "    and JI.JOB_NAME=? "
                    + "    group by JE.JOB_INSTANCE_ID "
                    + "    having MAX(JE.LAST_UPDATED) < ? "
                    + "  ) INST, BATCH_JOB_EXECUTION JE2 "
                    + " where INST.LAST_JE = JE2.JOB_EXECUTION_ID "
                    + " AND JE2.END_TIME is not null "
                    + " OR JE2.STATUS = 'ABANDONED' "
                    + " order by JOB_ID";

    @PostConstruct
    public void postConstruct() {
        Assert.notNull(this.tablePrefix, "A propriedade 'batch.spring.tablePrefix' "
                + "em batch-config.properties é obrigatória.");

        this.deleteJobInstanceQuery = getQuery(this.deleteJobInstanceQuery);
        this.deleteJobParamsQuery = getQuery(this.deleteJobParamsQuery);
        this.deleteStepExecutionContextQuery = getQuery(this.deleteStepExecutionContextQuery);
        this.deleteStepExecutionQuery = getQuery(this.deleteStepExecutionQuery);
        this.deleteJobExecutionContextQuery = getQuery(this.deleteJobExecutionContextQuery);
        this.deleteJobExecutionQuery = getQuery(this.deleteJobExecutionQuery);
        this.selectStepExecutionsQuery = getQuery(this.selectStepExecutionsQuery);
        this.selectExpiredJobInstancesQuery = getQuery(this.selectExpiredJobInstancesQuery);
    }

    /**
     * Acerta as queries para ter o mesmo prefixo configurado pela aplicação.
     * @param prefixo Prefixo das tabelas
     * @return Query com o prefixo corrigido.
     */
    private String getQuery(final String prefixo) {
        return StringUtils.replace(prefixo, "%PREFIX%", this.tablePrefix);
    }

    /**
     * Expurga dados de restart do Spring relacionados ao JobName que forem mais
     * antigas que a data de referência.
     * <p>
     * Este método não expurga dados de JobInstances em execução.
     *
     * @param data
     * Data de referencia
     * @param jobName
     * Nome do job cujos dados de restart serão expurgados.
     */
    public void expurgarDadosAntesDe(final Date data, final String jobName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Iniciando o expurgo de dados do job [%s] anteriores a [%tc]",
                    jobName, data));
        }

        this.transactionTemplate
                .execute(new TransactionCallbackWithoutResult() {

                    @Override
                    protected void doInTransactionWithoutResult(
                            final TransactionStatus status) {
                        limparRepositorio(jobName, data);
                    }
                });

        LOG.debug("Expurgo finalizado.");
    }

    private void limparRepositorio(final String jobName, final Date dataMaxima) {
        while (true) {
            this.jdbcTemplate.setMaxRows(JOB_INSTANCE_CHUNK_SIZE);

            LOG.debug("Consultando Jobs expirados");
            final List<Long> jobInstanceList = this.jdbcTemplate.query(
                    this.selectExpiredJobInstancesQuery, new IdRowMapper(), jobName, dataMaxima);

            if (jobInstanceList.size() == 0) {
                break;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format(
                        "Foi identificado [%d] jobInstances a serem excluídos",
                        jobInstanceList.size()));
            }

            // Volta ao valor default do JDBC.
            this.jdbcTemplate.setMaxRows(0);

            removeAllJobInstances(jobInstanceList);
        }
    }

    private void removeAllJobInstances(final List<Long> jobInstanceList) {
        String sql;

        LOG.debug("Limpando a tabela STEP_EXECUTION_CONTEXT");
        sql = getQuery(this.deleteStepExecutionContextQuery
                + buildQueryForStepExecutions(jobInstanceList));
        this.jdbcTemplate.execute(sql);

        LOG.debug("Limpando a tabela STEP_EXECUTION");
        sql = getQuery(this.deleteStepExecutionQuery
                + buildQueryForStepExecutions(jobInstanceList));
        this.jdbcTemplate.execute(sql);

        LOG.debug("Limpando a tabela JOB_EXECUTION_CONTEXT");
        sql = getQuery(this.deleteJobExecutionContextQuery
                + buildQueryForJobExecutions(jobInstanceList));
        this.jdbcTemplate.execute(sql);

        LOG.debug("Limpando a tabela JOB_EXECUTION_PARAMS");
        sql = getQuery(this.deleteJobParamsQuery
                + buildQueryForJobExecutions(jobInstanceList));
        this.jdbcTemplate.execute(sql);
        
        LOG.debug("Limpando a tabela JOB_EXECUTION");
        sql = getQuery(this.deleteJobExecutionQuery
                + buildQueryForJobExecutions(jobInstanceList));
        this.jdbcTemplate.execute(sql);

        LOG.debug("Limpando a tabela JOB_INSTANCE");
        sql = getQuery(this.deleteJobInstanceQuery
                + buildInClausule(jobInstanceList));
        this.jdbcTemplate.execute(sql);
    }

    private String buildQueryForStepExecutions(final List<Long> jobInstanceList) {
        String query = "select SE.STEP_EXECUTION_ID "
                + "from %PREFIX%JOB_EXECUTION JE, %PREFIX%STEP_EXECUTION SE "
                + "where JE.JOB_EXECUTION_ID = SE.JOB_EXECUTION_ID "
                + "AND JOB_INSTANCE_ID " + buildInClausule(jobInstanceList);

        return "STEP_EXECUTION_ID in (" + query + ")";
    }

    private String buildQueryForJobExecutions(final List<Long> jobInstanceList) {
        String query = "select JE.JOB_EXECUTION_ID "
                + "from %PREFIX%JOB_EXECUTION JE "
                + "where JOB_INSTANCE_ID " + buildInClausule(jobInstanceList);

        return "JOB_EXECUTION_ID in (" + query + ")";
    }

    private String buildInClausule(final List<Long> jobInstanceList) {
        StringBuilder builder = new StringBuilder();

        for (Long num : jobInstanceList) {
            builder.append(", ");
            builder.append(num);
        }

        builder.append(")");
        return "in (" + builder.substring(2);
    }

    /**
     * Remove um jobInstance das tabelas do Spring Batch.
     * Recomenda-se o uso do método
     * {@link RepositoryHelper#expurgarDadosAntesDe(Date, String)}
     * para limpeza em massa das tabelas do Spring Batch.
     * <p>
     * Use o método
     * {@link RepositoryHelper#removeJobInstanceByJobExecutionId(Long)}
     * caso conheça apenas o id do jobExecution (que é a situação mais comum).
     *
     * @param id Id do JobInstance a ser removido
     * @return true se o JobInstance foi removido.
     */
    public boolean removeJobInstance(final Long id) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Removendo o JobInstance [%d]", id));
        }

        boolean retorno =
                this.transactionTemplate.execute(
                        new TransactionCallback<Boolean>() {

                            public Boolean doInTransaction(
                                    final TransactionStatus arg0) {
                                return doRemoveJobInstance(id);
                            }
                        });

        return retorno;
    }

    /**
     * Remove um jobInstance das tabelas do Spring, caso apenas o id
     * de um dos jobExecutions seja conhecido.
     *
     * Para limpeza em massa, recomenda-se o uso do método
     * {@link RepositoryHelper#expurgarDadosAntesDe(Date, String)}.
     *
     * @param id Id de um JobExecution do JobInstance a ser removido
     * @return true se o JobInstance foi removido.
     */
    public boolean removeJobInstanceByJobExecutionId(final Long id) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Removendo o JobInstance do JobExecution [%d]", id));
        }

        JobExecution jobExecution = jobExplorer.getJobExecution(id);
        if (jobExecution == null) {
            LOG.debug(String.format("JobExecution [%d] inexistente", id));
            return false;
        }

        long idJobInstance = jobExecution.getJobId();
        return removeJobInstance(idJobInstance);
    }

    private boolean doRemoveJobInstance(final Long id) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Removendo o JobInstance [%d]", id));
        }

        final JobInstance ji = this.jobExplorer.getJobInstance(id);
        final List<JobExecution> listJe = this.jobExplorer.getJobExecutions(ji);
        final JobExecution lastJob = listJe.get(listJe.size() - 1);
        if (lastJob.isRunning()) {
            LOG.warn("O jobInstance [" + ji.getJobName() + " "
                    + lastJob.getJobParameters()
                    + "] não foi excluído porque ainda pode estar em execução.");

            return false;
        } else {
            // Remove todas as jobExecutions.
            for (final JobExecution je : listJe) {
                removeJobExecutions(je);
            }

            // Remove a JobInstance.
//            this.jdbcTemplate.update(this.deleteJobParamsQuery + " = ?", id);
            this.jdbcTemplate.update(this.deleteJobInstanceQuery + " = ?", id);

            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("JobInstance [%d] removida", id));
            }

            return true;
        }
    }

    private void removeJobExecutions(final JobExecution jobExecution)
            throws DataAccessException {
        // Obtém lista de StepExecutions
        final List<Long> stepExecutionIds = this.jdbcTemplate.query(
                this.selectStepExecutionsQuery, new IdRowMapper(), jobExecution.getId());

        // Limpa dados no escopo de step.
        for (final Long stepExecutionId : stepExecutionIds) {
            this.jdbcTemplate.update(this.deleteStepExecutionContextQuery
                    + "STEP_EXECUTION_ID=?", stepExecutionId);

            this.jdbcTemplate.update(this.deleteStepExecutionQuery
                    + "STEP_EXECUTION_ID=?", stepExecutionId);
        }
        
        //limpa dados de parametro do JOB
        this.jdbcTemplate.update(this.deleteJobParamsQuery
                + "JOB_EXECUTION_ID=?", jobExecution.getId());
        // Limpa dados no escopo de Job.
        this.jdbcTemplate.update(this.deleteJobExecutionContextQuery
                + "JOB_EXECUTION_ID=?", jobExecution.getId());
        this.jdbcTemplate.update(this.deleteJobExecutionQuery
                + "JOB_EXECUTION_ID=?", jobExecution.getId());
    }

    @Autowired
    public final void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public final void setTransactionManager(
            final PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    private static final class IdRowMapper implements RowMapper<Long> {

        public Long mapRow(final ResultSet rs, final int rowNum)
                throws SQLException {
            return rs.getLong(1);
        }
    }

}
