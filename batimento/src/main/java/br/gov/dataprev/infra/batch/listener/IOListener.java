package br.gov.dataprev.infra.batch.listener;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.dataprev.infra.batch.support.FileHelper;
import br.gov.dataprev.infra.batch.support.ProductionHelper;
import br.gov.dataprev.infra.batch.support.StepPhase;

/**
 * Componente que registra junto às tabelas de metadados do Spring, os
 * artefatos de entrada e saída do Step.
 * <p>
 * Tais artefatos são especificados através das propriedades entrada, processamento
 * e saída.
 * <p>
 * Todos os parametros aceitam os seguintes prefixos especiais.
 * <li>dtpservice: Buscará a URL da referencia do service locator</li>
 * <li>dtpjdbc: Buscará a URL da referencia jdbc</li>
 * <li>dtpfile: Informará o caminho completo do arquivo referenciado.</li>
 * <li>dtpio: Informa um texto livre qualquer. Por exemplo, o servidor LDAP acessado </li>
 * <p>
 * Caso nenhum prefixo seja informado, o componente assumirá que é uma pasta ou arquivo.
 * Exemplo de configuração:
 * <pre>
 *  &lt;listener>
 *    &lt;bean class="br.gov.dataprev.infra.batch.listener.IOListener" scope="step"
 *     xmlns="http://www.springframework.org/schema/beans">
 *       &lt;property name="entrada" value="${refappbatch.arquivo.zona.eleitoral.dir}" />
 *       &lt;property name="processamento" value="dtpservice:sdc" />
 *       &lt;property name="saida" value="dtpjdbc:default" />
 *    &lt;/bean>
 * &lt;/listener>
 * </pre>
 *
 * @see ProductionHelper
 * @author DATAPREV/DIT/DEAT
 */
public class IOListener implements StepExecutionListener {

    private static final Logger LOG = Logger.getLogger(IOListener.class);

    @Autowired
    private ProductionHelper pHelper;

    @Autowired
    private FileHelper fileHelper;

    private String entrada;

    private String processamento;

    private String saida;

    /**
     * {@inheritDoc}
     */
    public void beforeStep(final StepExecution stepExecution) {
        this.notify(StepPhase.READ, this.entrada);
        this.notify(StepPhase.PROCESS, this.processamento);
        this.notify(StepPhase.WRITE, this.saida);
    }

    public ExitStatus afterStep(final StepExecution stepExecution) {
        return null;
    }

    private void notify(final StepPhase fase, final Object obj) {
        if (obj != null) {
            final String fullMsg = obj.toString();
            this.tratarMsg(fase, fullMsg);
        }
    }

    private void tratarMsg(final StepPhase fase, final String msg) {
        if (msg.startsWith("dtpjdbc:")) {
            final String reference = msg.substring(8);
            if ("DEFAULT".equals(reference.toUpperCase())) {
                this.pHelper.notifyDB(fase, null);
            } else {
                this.pHelper.notifyDB(fase, reference);
            }
        } else if (msg.startsWith("dtpio:")) {
            final String reference = msg.substring(6);
            this.pHelper.notifyIO(fase, reference);
        } else {
            String caminho;
            if (msg.startsWith("dtpfile:")) {
                caminho = msg.substring(8);
            } else {
                LOG.warn("Assumindo que 'dtpfile:' foi usado. Para textos genéricos, use 'dtpio:'");
                caminho = msg;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Obtendo path completo para " + caminho);
            }

            final File f = new File(caminho);
            String reference = this.fileHelper.getFullPath(f);

            if (f.isDirectory()) {
                reference = "Arquivos na pasta [" + reference + "]";
            }

            this.pHelper.notifyIO(fase, reference);
        }
    }

    /**
     * Especifica um artefato de entrada. Ver especificação principal para mais detalhes.
     * @param entrada Artefato de entrada
     */
    public void setEntrada(final String entrada) {
        this.entrada = entrada;
    }

    /**
     * Especifica um artefato de processamento. Ver especificação principal para mais detalhes.
     * @param processamento Artefato de processamento
     */
    public void setProcessamento(final String processamento) {
        this.processamento = processamento;
    }

    /**
     * Especifica um artefato de saida. Ver especificação principal para mais detalhes.
     * @param saida Artefato de saida
     */
    public void setSaida(final String saida) {
        this.saida = saida;
    }


}
