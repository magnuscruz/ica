/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.gov.dataprev.infra.batch.initializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Inicializador de datasource.
 * <p>
 * Classe útil para gerar o esquema de BD esperado pelo spring batch.
 * <p>
 * Essa classe foi adaptada para uso pelo DtpInfraBatch.
 *
 * Exemplo de utilização:
 * <pre>
  {@code
    <bean id="dataSourceInitializer"
    class="br.gov.dataprev.infra.batch.initializer.DataSourceInitializer" >
        <property name="dataSource" ref="dataSource"/>
        <property name="initialize" value="true"/>
        <property name="initScripts">
            <list>
                <value>classpath:/meuScript1.sql</value>
                <value>classpath:/meuScript2.sql</value>
            </list>
        </property>
    </bean>
    }
   </pre>
 *
 * @author DATAPREV/DIT/DEAT e autores do Spring Batch.
 */
public class DataSourceInitializer implements InitializingBean, DisposableBean {

    private Resource[] initScripts;

    private Resource destroyScript;

    private DataSource dataSource;

    private boolean initialize = false;

    private static final Logger LOG = Logger.getLogger(DataSourceInitializer.class);

    private boolean initialized = false;

    public void setInitialize(final boolean initialize) {
        this.initialize = initialize;
    }

    public void destroy() throws Exception {
        if (!this.initialized) {
            return;
        }
        try {
            if (this.destroyScript != null) {
                doExecuteScript(this.destroyScript);
                this.initialized = false;
            }
        } catch (final Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Could not execute destroy script ["
                        + this.destroyScript + "]", e);
            } else {
                LOG.warn("Could not execute destroy script ["
                        + this.destroyScript + "]");
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.dataSource);
        LOG.info("Initializing with scripts: " + Arrays.asList(this.initScripts));
        if (!this.initialized && this.initialize) {
            try {
                doExecuteScript(this.destroyScript);
            } catch (final Exception e) {
                LOG.debug("Could not execute destroy script ["
                        + this.destroyScript + "]", e);
            }
            if (this.initScripts != null) {
                for (final Resource initScript : this.initScripts) {
                    LOG.info("Executing init script: " + initScript);
                    doExecuteScript(initScript);
                }
            }
            this.initialized = true;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void doExecuteScript(final Resource scriptResource) {
        if (scriptResource == null || !scriptResource.exists()) {
            return;
        }
        final TransactionTemplate transactionTemplate = new TransactionTemplate(
                new DataSourceTransactionManager(this.dataSource));
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(final TransactionStatus status) {
                final JdbcTemplate jdbcTemplate =
                        new JdbcTemplate(DataSourceInitializer.this.dataSource);
                String[] scripts;
                try {
                    scripts = StringUtils.delimitedListToStringArray(
                            stripComments(IOUtils.readLines(scriptResource
                                    .getInputStream())), ";");
                } catch (final IOException e) {
                    throw new BeanInitializationException(
                            "Cannot load script from [" + scriptResource + "]",
                            e);
                }
                for (final String script2 : scripts) {
                    final String script = script2.trim();
                    if (StringUtils.hasText(script)) {
                        try {
                            jdbcTemplate.execute(script2);
                        } catch (final DataAccessException e) {
                            if (!script.toUpperCase().startsWith("DROP")) {
                                throw e;
                            }
                        }
                    }
                }
                return null;
            }

        });

    }

    private String stripComments(final List<String> list) {
        final StringBuffer buffer = new StringBuffer();
        for (final String line : list) {
            if (!line.startsWith("//") && !line.startsWith("--")) {
                buffer.append(line);
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    public Class<DataSource> getObjectType() {
        return DataSource.class;
    }

    public void setInitScripts(final Resource[] initScripts) {
        // Findbugs reclama, mas está ok. (Este parametro é fornecido pelo Spring).
        this.initScripts = initScripts;
    }

    public void setDestroyScript(final Resource destroyScript) {
        this.destroyScript = destroyScript;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
