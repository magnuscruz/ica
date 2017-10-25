package br.gov.dataprev.infra.batch.support.internal;

/**
 * <b>Aten��o!</b> Classe interna do framework.
 * Uso n�o recomendado, pois pode sofrer modifica��o severa sem notifica��o pr�via.
 * <p>
 * Permite que as implementa��es construam o sum�rio atrav�s de v�rias estrat�gias,
 * como no console ou num StringBuilder.
 *
 * @author DATAPREV/DIT/DEAT
 */
public interface SumarioBuilderCallback {
    void build(final String text);

    void buildStacktrace(String stackTrace);
}
