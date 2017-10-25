package br.gov.dataprev.infra.batch.reader;


/**
 * <p>
 * Contrato que armazena o arquivo, o número da linha e o texto original.
 * </p>
 * <p>
 * Esta interface é colocada como uma alternativa que evita a herança
 * do {@link LineNumberEntity} pelas aplicações.
 * </p>
 * @see LineNumberEntity
 * @see LineNumberItemReader
 * @author DATAPREV/DIT/DEAT
 */
public interface LineNumberAware {

    /**
     * Obtém o número da linha. Sobrescreva o método caso queira persistir
     * esta informação no JPA.
     * @return número da linha.
     */
    int getLineNumber();

    /**
     * Altera o número da linha. Este método não deve ser chamado pelas aplicações.
     * @param lineNumber Numero da linha.
     */
    void setLineNumber(int lineNumber);

    /**
     * Obtém o nome do arquivo. Sobrescreva o método caso queira persistir
     * esta informação no JPA.
     * @return número da linha.
     */
    String getFileName();

    /**
     * Altera o nome do arquivo. Este método não deve ser chamado pelas aplicações.
     * @param fileName Nome do arquivo
     */
    void setFileName(String fileName);

    /**
     * Obtém a linha lida do arquivo. Sobrescreva o método caso queira persistir
     * esta informação no JPA.
     * @return número da linha.
     */
    String getLine();

    /**
     * Altera o nome do arquivo. Este método não deve ser chamado pelas aplicações.
     * @param line Linha lida.
     */
    void setLine(String line);
}
