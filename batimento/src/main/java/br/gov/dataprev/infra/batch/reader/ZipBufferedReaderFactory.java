package br.gov.dataprev.infra.batch.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

import br.gov.dataprev.infra.batch.exception.BatchRuntimeException;


/**
 * Buffered Reader Factory para leitura de arquivos ".zip".
 * <p>
 * Util em particular, na impossibilidade de armazenar arquivos grandes no servidor.
 * </p>
 * Exemplo de uso:
 *
 * <pre>
 * &lt;bean id="PNEReader" class="br.gov.dataprev.infra.batch.reader.DtpFlatFileItemReader"
 *  scope="step">
 *     &lt;property name="lineMapper">
 *         ...
 *     &lt;/property>
 *      &lt;property name="resource" value="file:#{jobParameters['arquivo']}" />
 *     &lt;property name="bufferedReaderFactory" ref="dtpZipReaderFactory" />
 * &lt;/bean>
 * </pre>
 * @author DATAPREV/DIT/DEAT, Lucas Duarte/DMP3
 */
public class ZipBufferedReaderFactory implements BufferedReaderFactory {

    private class ZipBufferedReader extends BufferedReader {
        private final ZipFile zipFile;

        public ZipBufferedReader(final Reader in, final ZipFile zipFile) {
            super(in);
            this.zipFile = zipFile;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.zipFile.close();
        }
    }

    /**
     * {@inheritDoc}
     * @see org.springframework.batch.item.file.BufferedReaderFactory#create(
     * org.springframework.core.io.Resource, java.lang.String)
     */
    public BufferedReader create(final Resource resource, final String encoding)
            throws UnsupportedEncodingException, IOException {
        String extension = "";
        final String filename = resource.getFile().getName();
        final int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = resource.getFile().getName().substring(i + 1);
        }
        if (extension.equalsIgnoreCase("zip")) {
            final ZipFile zipFile = new ZipFile(resource.getFile());
            if (zipFile.size() != 1) {
            	zipFile.close();
                throw new BatchRuntimeException(
                        "Só é possível ler um único arquivo compactado por arquivo .zip");
            }
            final ZipEntry entry = zipFile.entries().nextElement();
            return new ZipBufferedReader(new InputStreamReader(
                    zipFile.getInputStream(entry)), zipFile);

        }

        return new BufferedReader(new InputStreamReader(
                resource.getInputStream(), encoding));
    }
}
