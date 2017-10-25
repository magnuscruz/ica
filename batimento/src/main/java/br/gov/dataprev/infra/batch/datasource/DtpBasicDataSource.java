package br.gov.dataprev.infra.batch.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.dataprev.infra.batch.exception.IncorrectConfigurationException;
import br.gov.dataprev.infra.batch.support.internal.LogHelper;

/**
 * Integra o BasicDataSource com o arquivo de configura??o do framework.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class DtpBasicDataSource extends BasicDataSource implements InitializingBean {

    private static final String PROPRIEDADE_READ_ONLY =
            "Propriedade read-only. Favor utilizar a propriedade jdbcReference";
    private static final String MSG_DATASOURCE = "dtp.infra.batch.ABRINDO_DATASOURCE";

    private String jdbcReference;

    @Autowired
    private LogHelper logHelper;

    private boolean hasLogged;

    /**
     * Gets the jdbc reference.
     *
     * @return the jdbc reference
     */
    public String getJdbcReference() {
        return this.jdbcReference;
    }

    /**
     * Sets the jdbc reference.
     *
     * @param jdbcReference the new jdbc reference
     */
    public void setJdbcReference(final String jdbcReference) {
        this.jdbcReference = jdbcReference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDriverClassName(final String driverClassName) {
        throw new IncorrectConfigurationException(PROPRIEDADE_READ_ONLY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUrl(final String url) {
        throw new IncorrectConfigurationException(PROPRIEDADE_READ_ONLY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUsername(final String username) {
        throw new IncorrectConfigurationException(PROPRIEDADE_READ_ONLY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPassword(final String password) {
        throw new IncorrectConfigurationException(PROPRIEDADE_READ_ONLY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultAutoCommit(final boolean value) {
        throw new IncorrectConfigurationException(PROPRIEDADE_READ_ONLY);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
//        final JdbcReferenceType jRef = this.dtpExtractor.obterJdbcReference(this.jdbcReference);
//
        super.setUsername("user");
        super.setPassword("user");
        super.setUrl("jdbc:derby://localhost:1527/sample;create=true;upgrade=true");
        super.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
        super.setDefaultAutoCommit(false);
        
        // Tunning itens
        // TODO - Revisar e externalizar.
        /*
        super.setTestWhileIdle(true);
        super.setTestOnBorrow(true);
        super.setTestOnReturn(false);
        if (getDriverClassName().contains("oracle")) {
            super.setValidationQuery("SELECT 1 FROM DUAL");
        } else {
            super.setValidationQuery("SELECT 1");
        }
        super.setTimeBetweenEvictionRunsMillis(5000);
        super.setMaxActive(20);
        super.setMinIdle(1);
        super.setMaxWait(10000);
        super.setInitialSize(1);
        super.setRemoveAbandoned(true);
        super.setRemoveAbandonedTimeout(60);
        super.setLogAbandoned(true);
        super.setMinEvictableIdleTimeMillis(30000);
        */
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!this.hasLogged) {
            this.logHelper.log(MSG_DATASOURCE, getUrl());
            this.hasLogged = true;
        }
        return super.getConnection();
    }

}
