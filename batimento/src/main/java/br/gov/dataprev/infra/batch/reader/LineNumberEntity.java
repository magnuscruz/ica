package br.gov.dataprev.infra.batch.reader;

import javax.persistence.Transient;

/**
 * <p>
 * Entidade base que armazena o arquivo, o n�mero da linha e o texto original.
 * </p>
 * <p>
 * Por padr�o, os campos est�o marcados com a anota��o {@link Transient}.
 * Sobrescreva os m�todos get's caso necessite persistir estas informa��es com o JPA.
 * </p>
 * <p>
 * Outra alternativa, ao inv�s de usar esta classe como base, � implementar a
 * interface {@link LineNumberAware}
 * </p>
 * @see LineNumberItemReader
 * @author DATAPREV/DIT/DEAT
 */
public class LineNumberEntity implements LineNumberAware {

    private int lineNumber;

    private String fileName;

    private String line;

    /**
     * {@inheritDoc}
     * @see br.gov.dataprev.infra.batch.reader.LineNumberAware#getLineNumber()
     */
    @Transient
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * {@inheritDoc}
     * @see br.gov.dataprev.infra.batch.reader.LineNumberAware#setLineNumber(int)
     */
    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * {@inheritDoc}
     * @see br.gov.dataprev.infra.batch.reader.LineNumberAware#getFileName()
     */
    @Transient
    public String getFileName() {
        return this.fileName;
    }

    /**
     * {@inheritDoc}
     * @see br.gov.dataprev.infra.batch.reader.LineNumberAware#setFileName(java.lang.String)
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     * @see br.gov.dataprev.infra.batch.reader.LineNumberAware#getLine()
     */
    @Transient
    public String getLine() {
        return this.line;
    }

    /**
     * {@inheritDoc}
     * @see br.gov.dataprev.infra.batch.reader.LineNumberAware#setLine(java.lang.String)
     */
    public void setLine(final String line) {
        this.line = line;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        if (this.fileName != null) {
            result += this.fileName.hashCode();
        }
        result = prime * result + this.lineNumber;
        result = prime * result;
        if (this.line != null) {
            result += this.line.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final LineNumberEntity other = (LineNumberEntity) obj;
        if (this.fileName == null) {
            if (other.fileName != null) {
                return false;
            }
        } else if (!this.fileName.equals(other.fileName)) {
            return false;
        }
        if (this.lineNumber != other.lineNumber) {
            return false;
        }
        if (this.line == null) {
            if (other.line != null) {
                return false;
            }
        } else if (!this.line.equals(other.line)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LineNumberEntity [lineNumber=" + this.lineNumber
                + ", fileName=" + this.fileName + ", linha="
                + this.line + "]";
    }
}
