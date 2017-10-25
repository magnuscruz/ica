package br.gov.dataprev.infra.batch.support.internal;

/**
 * <b>Atenção!</b> Classe interna do framework.
 * Uso não recomendado, pois pode sofrer modificação severa sem notificação prévia.
 * <p>
 * Permite que as implementações construam o sumário através de várias estratégias,
 * como no console ou num StringBuilder.
 *
 * @author DATAPREV/DIT/DEAT
 */
public interface SumarioBuilderCallback {
    void build(final String text);

    void buildStacktrace(String stackTrace);
}
