package br.gov.dataprev.infra.batch.reader;

/**
 * Exceção ocorrida no componente LineNumberItemReader.
 * Permite que o nome do arquivo e a linha atual sejam capturados.
 * @author DATAPREV/DIT/DEAT
 */
public class LineNumberException extends Exception {

    private static final long serialVersionUID = 6342663323449686647L;

    private int lineNumber;
    private String fileName;
    private String line;

    public LineNumberException(final Exception e, final int lineNumber,
            final String line, final String fileName) {
        super(e);

        this.lineNumber = lineNumber;
        this.line = line;
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLine() {
        return line;
    }

}
