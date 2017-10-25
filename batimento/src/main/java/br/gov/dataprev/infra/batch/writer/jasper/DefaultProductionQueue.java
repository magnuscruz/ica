package br.gov.dataprev.infra.batch.writer.jasper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import br.gov.dataprev.infra.batch.exception.BatchRuntimeException;

/**
 * Implementação padrão da {@link ProductionQueue}.
 *
 * @param <E> Objeto da fila de produção.
 * @author DATAPREV/DIT/DEAT
 */
public class DefaultProductionQueue<E> implements ProductionQueue<E> {
    /** Timeout em milisegundos para evitar deadlocks. */
    private static final int WAIT_TIMEOUT = 1000;

    /** Informa se a fila ainda está ativa. */
    private boolean ativa;

    /** Armazena os itens na fila. */
    private final BlockingQueue<E> itens;

    /** Bloqueio para o produtor aguardar os trabalhos do consumidor. */
    private final CountDownLatch todosLidos = new CountDownLatch(1);

    /**
     * Gera uma fila do tamanho especificado.
     * @param maxSize Tamanho da fila de prioridade.
     */
    public DefaultProductionQueue(final int maxSize) {
        itens = new LinkedBlockingQueue<E>(maxSize);
        ativa = true;
    }

    /**
     * {@inheritDoc}
     */
    public void make(final E obj) {
        try {
            boolean sucesso;
            do {
                sucesso = itens.offer(obj, WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
            } while (!sucesso && ativa);

        } catch (final InterruptedException e) {
            ativa = false;
            throw new BatchRuntimeException(e);
        }

        if (!ativa) {
            throw new InterruptedGenerationException("O consumidor foi interrompido.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public E consume() {
        try {
            E obj;
            do {
                obj = itens.poll(WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
            } while ((obj == null) && (ativa || itens.size() > 0));

            return obj;
        } catch (final InterruptedException e) {
            ativa = false;
            throw new BatchRuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void makeEnded() {
        ativa = false;

        try {
            todosLidos.await();
        } catch (final InterruptedException e) {
            throw new BatchRuntimeException("Erro aguardando consumidor", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void consumeEnded() {
        ativa = false;
        todosLidos.countDown();
    }
}
