package br.gov.dataprev.infra.batch.initializer;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import br.gov.dataprev.infra.batch.exception.BatchException;
import br.gov.dataprev.infra.batch.support.FileHelper;
import br.gov.dataprev.infra.batch.support.internal.LogHelper;
import br.gov.dataprev.infra.batch.support.internal.ShutdownHook;

/**
 * Inicializador para verificação de espaço em disco.
 * <p>
 * Este componente soma o espaço livre de todos os caminhos indicados pela propriedade
 * <b>paths</b> e verifica se a soma é maior que o tamanho mínimo estabelecido na propriedade
 * <b>minimum</b> em kilobytes.
 * <p>
 * Exemplo de utilização:
 * <pre>
  {@code
    <bean id="diskSpaceInitializer"
    class="br.gov.dataprev.infra.batch.initializer.DiskSpaceInitializer" >
        <property name="diskMap">
            <map>
                <!-- Confere se a pasta arquivo tem 30 MB livres -->
                <entry key="./arquivos" value="30000"/>

                <!-- Confere se o disco referenciado tem 10 MB livres -->
                <entry key="/path/em/outro/disco" value="10000"/>
            </map>
        </property>
    </bean>
    }
   </pre>
 *
 * @author DATAPREV/DIT/DEAT
 */
public class DiskSpaceInitializer implements InitializingBean {

    private static final String ESPACO_INSUFICIENTE = "Espaco em disco insuficiente.";

    // Constantes para o arquivo de mensagens.
    private static final String MSG_ESPACO_INSUFICIENTE = "dtp.infra.batch.ESPACO_INSUFICIENTE";
    private static final String MSG_ESPACO_PATH = "dtp.infra.batch.ESPACO_PATH";

    private Map<String, Long> diskMap;

    // Dependencias
    @Autowired
    private FileHelper fileHelper;

    @Autowired
    private LogHelper logHelper;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.diskMap, "a propriedade diskMap é obrigatória");

        checkTotalSize();
    }

    private void checkTotalSize() throws BatchException {
        boolean isOk = true;

        for (final Entry<String, Long> entry : this.diskMap.entrySet()) {
            final String path = entry.getKey();
            final long minimum = entry.getValue();

            final long freeSpace = this.fileHelper.getFreeSpaceKb(entry.getKey());
            if (freeSpace < minimum) {
                isOk = false;
                this.logHelper.log(MSG_ESPACO_INSUFICIENTE, freeSpace, path, minimum);
            } else {
                this.logHelper.log(MSG_ESPACO_PATH, freeSpace, path);
            }
        }

        ShutdownHook.setSpaceChecked(true);

        if (!isOk) {
            throw new BatchException(ESPACO_INSUFICIENTE);
        }
    }

    public Map<String, Long> getDiskMap() {
        return this.diskMap;
    }

    public void setDiskMap(final Map<String, Long> diskMap) {
        this.diskMap = diskMap;
    }

}
