package br.gov.dataprev.infra.batch.support;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Mapeador de código de saída do spring batch para a JVM.
 * <p/>
 * Util se for necessário combinar códigos especiais com a Produção/DIT.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class DtpExitCodeMapper implements ExitCodeMapper, InitializingBean {
    protected static final Logger LOG = LogManager.getLogger(DtpExitCodeMapper.class);

    private static final String MSG_SEM_MAPEAMENTO = "dtp.infra.batch.MSG_SEM_MAPEAMENTO";

    private Map<String, Integer> mapping;

    @Autowired
    private ProductionHelper producaoHelper;

    public Map<String, Integer> getMapping() {
        return this.mapping;
    }

    public void setMapping(final Map<String, Integer> mapping) {
        this.mapping = mapping;
    }

    public int intValue(final String exitCode) {
        final Integer statusCode = this.mapping.get(exitCode);
        if (statusCode == null) {
            // Codigo não foi mapeado pela aplicação.
            this.producaoHelper.log(MSG_SEM_MAPEAMENTO,
                    exitCode, JVM_EXITCODE_GENERIC_ERROR);

            return JVM_EXITCODE_GENERIC_ERROR;
        } else {
            // Retorna código para uso nos jobs da produção.
            if (LOG.isDebugEnabled()) {
                LOG.debug("Retornando codigo de saida [" + statusCode + "] para ["
                        + exitCode + "]");
            }

            return statusCode.intValue();
        }
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.mapping, "A propriedade [mapping] é obrigatória");

        // Adiciona os códigos padrões.
        this.mapping.put(ExitStatus.COMPLETED.getExitCode(), JVM_EXITCODE_COMPLETED);
        this.mapping.put(ExitStatus.FAILED.getExitCode(), JVM_EXITCODE_GENERIC_ERROR);
        this.mapping.put(ExitCodeMapper.JOB_NOT_PROVIDED, JVM_EXITCODE_JOB_ERROR);
        this.mapping.put(ExitCodeMapper.NO_SUCH_JOB, JVM_EXITCODE_JOB_ERROR);

        this.mapping.put(ExitStatus.STOPPED.getExitCode(), JVM_EXITCODE_GENERIC_ERROR);
    }

}
