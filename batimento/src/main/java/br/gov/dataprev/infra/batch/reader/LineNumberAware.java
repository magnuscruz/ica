package br.gov.dataprev.infra.batch.reader;


/**
 * <p>
 * Contrato que armazena o arquivo, o n�mero da linha e o texto original.
 * </p>
 * <p>
 * Esta interface � colocada como uma alternativa que evita a heran�a
 * do {@link LineNumberEntity} pelas aplica��es.
 * </p>
 * @see LineNumberEntity
 * @see LineNumberItemReader
 * @author DATAPREV/DIT/DEAT
 */
public interface LineNumberAware {

    /**
     * Obt�m o n�mero da linha. Sobrescreva o m�todo caso queira persistir
     * esta informa��o no JPA.
     * @return n�mero da linha.
     */
    int getLineNumber();

    /**
     * Altera o n�mero da linha. Este m�todo n�o deve ser chamado pelas aplica��es.
     * @param lineNumber Numero da linha.
     */
    void setLineNumber(int lineNumber);

    /**
     * Obt�m o nome do arquivo. Sobrescreva o m�todo caso queira persistir
     * esta informa��o no JPA.
     * @return n�mero da linha.
     */
    String getFileName();

    /**
     * Altera o nome do arquivo. Este m�todo n�o deve ser chamado pelas aplica��es.
     * @param fileName Nome do arquivo
     */
    void setFileName(String fileName);

    /**
     * Obt�m a linha lida do arquivo. Sobrescreva o m�todo caso queira persistir
     * esta informa��o no JPA.
     * @return n�mero da linha.
     */
    String getLine();

    /**
     * Altera o nome do arquivo. Este m�todo n�o deve ser chamado pelas aplica��es.
     * @param line Linha lida.
     */
    void setLine(String line);
}
