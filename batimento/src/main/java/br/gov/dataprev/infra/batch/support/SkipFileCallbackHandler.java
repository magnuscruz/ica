package br.gov.dataprev.infra.batch.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import br.gov.dataprev.infra.batch.exception.BatchRuntimeException;

/**
 * Este componente é responsável por gravar linhas rejeitadas em um arquivo
 * separado.
 *
 * O componente foi projetado para ser ThreadSafe.
 *
 * <pre>
{@code
<bean id="eleitorSkipCallback"
    class="br.gov.dataprev.infra.batch.support.SkipFileCallbackHandler" scope=
"singleton">
    <property name="resource" value="file:MEU.ARQUIVO.SKIP.TXT" />
</bean>
}
 * </pre>
 * 
 * @see br.gov.dataprev.infra.batch.listener.SkipCheckingListener
 * @author DATAPREV/DIT/DEAT
 */
public class SkipFileCallbackHandler {

	private static final Logger LOG = Logger.getLogger(SkipFileCallbackHandler.class);

	private Resource resource;

	private Writer writer;

	private boolean appendAllowed = true;

	@PostConstruct
	public void init() {
		Assert.notNull(this.resource, "A propriedade resource é obrigatória");
	}

	public synchronized void logToSkipFile(final String line) {
		if (this.writer == null) {
			this.initWriter();
		}
		try {
			this.writer.write(line + "\n");
			this.writer.flush();
		} catch (final IOException e) {
			final String msg = "Erro gravando linha no arquivo de skip";
			LOG.error(msg);
			throw new BatchRuntimeException(msg);
		}
	}

	private void initWriter() {
		try {
			final File file = this.resource.getFile();
			final FileOutputStream fos = new FileOutputStream(file, this.appendAllowed);
			this.writer = new OutputStreamWriter(fos);
		} catch (final Exception e) {
			final String msg = "Erro inicializando o arquivo de skip";
			LOG.error(msg);
			throw new BatchRuntimeException(msg);
		}
	}

	@PreDestroy
	public void destroy() {
		if (this.writer != null) {
			try {
				this.writer.close();
			} catch (final IOException e) {
				LOG.warn("Erro fechando arquivo de skip", e);
			}
		}
	}

	/**
	 * Define o resource a ser usado na gravação do arquivo de skip.
	 * 
	 * @param resource
	 *            Resource a ser usado.
	 */
	public void setResource(final Resource resource) {
		this.resource = resource;
	}

	/**
	 * Define se um append será usado, caso o arquivo já exista. O valor default é
	 * 'true'.
	 * 
	 * @param appendAllowed
	 *            Se o append deve ser usado.
	 */
	public void setAppendAllowed(final boolean appendAllowed) {
		this.appendAllowed = appendAllowed;
	}

}
