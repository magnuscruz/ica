package br.gov.dataprev.infra.batch.writer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import br.gov.dataprev.infra.batch.exception.IncorrectConfigurationException;
import br.gov.dataprev.infra.batch.support.ProductionHelper;

/**
 * ItemWriter responsável por gerenciar arquivos de impressão.
 * <p>
 * Este componente preenche o campo de data de geração do relatório (AAAAMMDD)
 * e o campo relativo ao PID (9999999) do arquivo
 * <p>
 * Exemplo de configuração:
 *
 * <pre>
 * {@code
 * <!-- Não é necessário informar os campos AAAAAMMDD e 9999999. Eles serão preenchidos
 * dinamicamente. -->
 * <bean id="errorWriter" class="br.gov.dataprev.infra.batch.writer.DelegatingPidItemWriter"
 *  scope="step" >
 *     <property name="resourceName" value="file:./D.REF.BAT.001.REL.001PDREFBAT001.PDF"/>
 *     <property name="delegate" ref="outroItemWriter"/>
 * </bean>
 * }
 * </pre>
 *
 * @author DATAPREV/DIT/DEAT
 * @param <T> Classe do item a ser gravado
 */
public class DelegatingPidItemWriter<T> implements ItemStreamWriter<T>, InitializingBean {

    private static final Logger LOG = Logger.getLogger(DelegatingPidItemWriter.class);

    private static final String MSG_PID_NAO_INFORMADO = "dtp.infra.batch.PID_NAO_INFORMADO";
    private static final String CAMPO_REL_NAO_ENCONTRADO =
            "Atenção! Não foi encontrado o campo REL no nome do arquivo.";
    private static final String ERRO_RENOMEANDO_ARQUIVO =
            "Erro na etapa de renomear o arquivo antigo.";
    private static final int PREFIX_POSITION = 4;
    private static final int SUFIX_POSITION = 5;

    /** Constante para recuperar o nome antigo durante o restart. */
    private static final String RESOURCE_NAME = "resourceName";

    /** Classe que fará a gravação. */
    private ResourceAwareItemWriterItemStream<T> delegate;

    /** The resource name. */
    private String resourceName;

    /** The loader. */
    @Autowired
    private ResourceLoader loader;

    @Autowired
    private ProductionHelper producaoHelper;

    /**
     * Gets the delegate.
     *
     * @return the delegate
     */
    public ResourceAwareItemWriterItemStream<T> getDelegate() {
        return this.delegate;
    }

    /**
     * Sets the delegate.
     *
     * @param delegate the new delegate
     */
    public void setDelegate(final ResourceAwareItemWriterItemStream<T> delegate) {
        this.delegate = delegate;
    }

    /**
     * Gets the resource name.
     *
     * @return the resource name
     */
    public String getResourceName() {
        return this.resourceName;
    }

    /**
     * Sets the resource name.
     *
     * @param resource the new resource name
     */
    public void setResourceName(final String resource) {
        this.resourceName = resource;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws ItemStreamException {
        this.delegate.close();
    }

    /**
     * {@inheritDoc}
     */
    public void update(final ExecutionContext context) throws ItemStreamException {
        this.delegate.update(context);
    }

    /**
     * {@inheritDoc}
     */
    public void open(final ExecutionContext context) throws ItemStreamException {
        final String resourceWithPid = generateResourceNameWithPid();
        final Resource resource = this.loader.getResource(resourceWithPid);

        // Verifica se é um restart
        if (context.containsKey(RESOURCE_NAME)) {
            LOG.debug("Preparando restart de arquivo com PID");
            final Resource old = this.loader.getResource(context.getString(RESOURCE_NAME));

            // Renomeia o arquivo
            try {
                final String fileName = resource.getFile().getAbsolutePath();
                final File file = new File(fileName);

                if (!old.getFile().renameTo(file)) {
                    throw new ItemStreamException(ERRO_RENOMEANDO_ARQUIVO);
                }
            } catch (final IOException e) {
                throw new ItemStreamException(ERRO_RENOMEANDO_ARQUIVO, e);
            }
        }

        // Atribui recurso ao ItemWriter
        this.delegate.setResource(resource);
        context.putString(RESOURCE_NAME, resourceWithPid);

        this.delegate.open(context);
    }

    /**
     * Gera o nome do arquivo com PID conforme padrão divulgado pela DIT.
     * <p>
     * Exemplo caso seja 12/06/2011 com -Dpid=1234: </p>
     *
     * D.REF.BAT.001.REL.001PDREFBAT001.PDF -->
     * D.REF.BAT.001.REL<b>.20110612.0001234</b>.001PDREFBAT001.PDF
     *
     * @return Nome do arquivo seguindo o padrão da DIT.
     */
    private String generateResourceNameWithPid() {
        final int index = this.resourceName.lastIndexOf(".REL.");
        if (index == -1) {
            LOG.error(CAMPO_REL_NAO_ENCONTRADO);
            throw new ItemStreamException(CAMPO_REL_NAO_ENCONTRADO);
        } else {
            // Separa o nome do arquivo em duas partes.
            final String sufixo = this.resourceName.substring(index + SUFIX_POSITION);
            final String prefixo = this.resourceName.substring(0, index + PREFIX_POSITION);

            // Gera os campos Data e Pid no nome do arquivo.
            final String resourceWithPid = String.format("%s.%tY%<tm%<td.%07d.%s", prefixo,
                    new Date(), obterPid(), sufixo);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Gerando nome [" + resourceWithPid
                        + "] para [" + this.resourceName + "]");
            }

            return resourceWithPid;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void write(final List<? extends T> lista) throws Exception {
        this.delegate.write(lista);
    }

    /**
     * Obter pid.
     *
     * @return the string
     */
    public Integer obterPid() {
        final String pidProperty = System.getProperty("pid");

        Integer pid = null;
        try {
            pid = Integer.parseInt(pidProperty);
        } catch (final NumberFormatException e) {
            LOG.error("Erro convertendo o PID para numerico.", e);
            pid = null;
        }

        if (pid == null) {
            this.producaoHelper.log(MSG_PID_NAO_INFORMADO);
            throw new IncorrectConfigurationException("PID não informado");
        }

        return pid;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.resourceName, "A propriedade resourceName é obrigatória");
        Assert.notNull(this.delegate, "A propriedade delegate é obrigatória");
    }
}
