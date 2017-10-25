package br.gov.dataprev.infra.batch.writer.jasper;

import java.util.concurrent.BlockingQueue;

/**
 *  Fila similar � {@link BlockingQueue}. A principal diferen�a � que a
 *  BlockingQueue n�o permite que o produtor avise ao consumidor que dados
 *  n�o ser�o mais produzidos.
 *  <p>
 *  A ProductionQueue implementa essa funcionalidade atrav�s dos m�todos
 *  {@link ProductionQueue#makeEnded()} e {@link ProductionQueue#consumeEnded()}
 *
 * @author DATAPREV/DIT/DEAT
 * @param <E> Classe que ser� enfileirada.
 */
public interface ProductionQueue<E> {
    /**
     * Produz um item para a fila. Se n�o houver espa�o, bloqueia a thread
     * at� a libera��o do espa�o.
     *
     * @param obj Objeto a ser inserido.
     * @throws InterruptedGenerationException Se a thread consumidora parou de consumir
     */
    void make(E obj) throws InterruptedGenerationException;

    /**
     * Consume um item da fila. Se o item n�o existir e a produ��o ainda
     * estiver ativa, bloqueia a thread at� a libera��o do objeto.
     *
     * @return Objeto consumido ou null se a produ��o terminou.
     */
    E consume();

    /**
     * Informa que a produ��o terminou. A thread � bloqueada at�
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
