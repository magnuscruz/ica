package br.gov.dataprev.sibe.batch.importbatim.writer;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;

import br.gov.dataprev.infra.batch.writer.DtpItemWriterStream;
import br.gov.dataprev.sibe.batch.importbatim.to.BatimentoTO;

/**
 * ItemWriter para gravar arquivo csv com pessoas.
 * <p/>
 * Grava o estado de registros lidos, para gravacao do trailer e restart.
 * @author DATAPREV/DIT/DEAT
 */
public class ImportacaoBatimentoItemWriter implements ResourceAwareItemWriterItemStream<BatimentoTO>,
        FlatFileFooterCallback, FlatFileHeaderCallback {

    /** ItemWriter que ira fazer o trabalho de gerar o csv. */
    private DtpItemWriterStream<BatimentoTO> delegate;

    /** Contador para totalizar o numero de registros gravados no trailer. */
    private static final String WRITEN_FIELD = "writen";
    private int writen;
    private String names;

    /**
     * {@inheritDoc}
     */
    public synchronized void write(final List<? extends BatimentoTO> pessoas) throws Exception {
        this.delegate.write(pessoas);

        this.writen += pessoas.size();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void writeHeader(final Writer writer) throws IOException {
        writer.append("H,");

        final SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        writer.append(sdf.format(new Date())+"\n");
        writer.append(getNames());
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void writeFooter(final Writer writer) throws IOException {
        writer.append("T,");
        writer.append(Integer.toString(this.writen));
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void close() throws ItemStreamException {
        this.delegate.close();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void open(final ExecutionContext context) throws ItemStreamException {
        this.delegate.open(context);
        this.writen = context.getInt(WRITEN_FIELD, 0);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void update(final ExecutionContext context) throws ItemStreamException {
        this.delegate.update(context);
        context.put(WRITEN_FIELD, this.writen);
    }

    /**
     * {@inheritDoc}
     */
    public void setResource(final Resource resource) {
        this.delegate.setResource(resource);
    }

    public void setDelegate(final DtpItemWriterStream<BatimentoTO> delegate) {
        this.delegate = delegate;
    }

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

}
