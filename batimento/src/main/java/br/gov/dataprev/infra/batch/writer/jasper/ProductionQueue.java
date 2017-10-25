package br.gov.dataprev.infra.batch.writer.jasper;

import java.util.concurrent.BlockingQueue;

/**
 *  Fila similar à {@link BlockingQueue}. A principal diferença é que a
 *  BlockingQueue não permite que o produtor avise ao consumidor que dados
 *  não serão mais produzidos.
 *  <p>
 *  A ProductionQueue implementa essa funcionalidade através dos métodos
 *  {@link ProductionQueue#makeEnded()} e {@link ProductionQueue#consumeEnded()}
 *
 * @author DATAPREV/DIT/DEAT
 * @param <E> Classe que será enfileirada.
 */
public interface ProductionQueue<E> {
    /**
     * Produz um item para a fila. Se não houver espaço, bloqueia a thread
     * até a liberação do espaço.
     *
     * @param obj Objeto a ser inserido.
     * @throws InterruptedGenerationException Se a thread consumidora parou de consumir
     */
    void make(E obj) throws InterruptedGenerationException;

    /**
     * Consume um item da fila. Se o item não existir e a produção ainda
     * estiver ativa, bloqueia a thread até a liberação do objeto.
     *
     * @return Objeto consumido ou null se a produção terminou.
     */
    E consume();

    /**
     * Informa que a produção terminou. A thread é bloqueada até
     * o consumidor ler os ultimos componentes da fila e avisar que
     * o consumo terminou
     *
     * @see ProductionQueue#consumeEnded()
     */
    void makeEnded();

    /**
     * Informa que o consumo terminou de fazer o que precisava,
     * liberando a thread do produtor continuar seu processamento normal.
     *
     * @see ProductionQueue#makeEnded()
     */
    void consumeEnded();
}
