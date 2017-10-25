package br.gov.dataprev.infra.batch.reader.internal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.reader.LineNumberItemReader;

/**
 * <b>Atenção!</b> Classe interna do framework. Uso não recomendado, pois pode
 * sofrer modificação severa sem notificação prévia.
 * <p>
 * Bean com informações acerca da linha sendo processada no momento para o step.
 * </p>
 * @author DATAPREV/DIT/DEAT
 * @see LineNumberItemReader
 */
@Component
@Scope("step")
public class LineStepInformationBean {

    private int lineNumber;
    private String line;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLine() {
        return line;
    }

    public void setLine(final String line) {
        this.line = line;
    }

}
