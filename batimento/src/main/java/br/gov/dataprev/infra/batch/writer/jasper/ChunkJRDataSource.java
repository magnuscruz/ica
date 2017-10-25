package br.gov.dataprev.infra.batch.writer.jasper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.log4j.Logger;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Implementa um Datasource do JasperReports de forma que possa integrar
 * com o processamento orientado a Chunk do Spring Batch.
 *
 * @author DATAPREV/DIT/DEAT
 */
public class ChunkJRDataSource implements JRDataSource {
    private static final Logger LOG = Logger.getLogger(ChunkJRDataSource.class);

    /** Fila de itens a serem informados para o Jasper Reports. */
    private final ProductionQueue<Object> itens;

    /** Parser para avaliar expressões de field. */
    private final ExpressionParser parser = new SpelExpressionParser();

    /** Contexto para avaliar expressões de field. */
    private EvaluationContext context;

    /**
     * Cria um Datasource com fila de tamanho prefinido.
     * @param maxSize Tamanho da fila.
     */
    public ChunkJRDataSource(final int maxSize) {
        itens = new DefaultProductionQueue<Object>(maxSize);
    }

    /**
     * Adiciona um item para o datasource.
     * @param obj item a ser adicionado.
     */
    public void addItem(final Object obj) {
        LOG.debug("Adicionando item");
        itens.make(obj);
    }

    private Object getItem() {
        LOG.debug("Obtendo item");
        return itens.consume();
    }

    /**
     * Aguarda o Jasper consumir todos os itens adicionados.
     */
    public void waitClose() {
        LOG.debug("Aguardando termino de consumo");
        itens.makeEnded();
        LOG.debug("Consumo terminado");
    }

    /**
     * Informa que os itens foram consumidos.
     */
    public void close() {
        LOG.debug("Fechando consumo");
        itens.consumeEnded();
    }

    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(final JRField field) throws JRException {
        try {
            final Expression exp = parser.parseExpression(field.getName());
            return exp.getValue(context);
        } catch (final Exception e) {
            throw new JRException("Erro avaliando campo do jasper", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean next() throws JRException {
        final Object item = getItem();
        if (item != null) {
            context = new StandardEvaluationContext(item);
        }
        return item != null;
    }
}
