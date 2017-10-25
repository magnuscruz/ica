package br.gov.dataprev.infra.batch.support.internal;

import java.io.DataInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import br.gov.dataprev.infra.batch.exception.BatchRuntimeException;

@Component
public class SqlProvider {
    private static String SQL_PLACE="/sql/infra-batch/";
    
    @Value("${batch.spring.tablePrefix}")
    private String tablePrefix;
    
    public String readQuery(final String scriptName) {
        try {
            final InputStream is = ProcessManager.class.getResourceAsStream(
                    SQL_PLACE + scriptName);
            final String fullScript = readStreamAsString(is, "ISO-8859-1");
            
            return StringUtils.replace(fullScript, "%PREFIX%", this.tablePrefix);
        } catch (final Exception e) {
            throw new BatchRuntimeException("Erro lendo script sql ["
                 + scriptName + "]",e);
        }
    }    
    
    public static String readStreamAsString(final InputStream stream,
            final String encoding) throws Exception {
        final DataInputStream dis = new DataInputStream(stream);
        try {
            final long len = stream.available();
            if (len > Integer.MAX_VALUE) {
                throw new RuntimeException ("Stream muito grande: " + len);
            }
            final byte[] bytes = new byte[(int) len];
            dis.readFully(bytes);
            return new String(bytes, encoding);
        } finally {
            dis.close();
        }
    }    
}
