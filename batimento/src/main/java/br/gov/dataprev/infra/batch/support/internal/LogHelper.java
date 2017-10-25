package br.gov.dataprev.infra.batch.support.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

/**
 * <b>Aten��o!</b> Classe interna do framework. Uso n�o recomendado, pois pode
 * sofrer modifica��o severa sem notifica��o pr�via.
 * <p>
 * O componente LogHelper � respons�vel por efetuar log no console,
 * usando arquivos de mensagens.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component
public class LogHelper {

    /*
     * Constantes de erro se n�o for possivel encontrar o arquivo de mensagens.
     */
    private static final String ERRO_INESPERADO =
            "Erro inesperado. Favor consultar o log de desenvolvimento";
    private static final String MSG_NAO_ENCONTRADA =
            "Mensagem n�o encontrada no arquivo de mensagens: {%s}";

    private static final Logger PRODUCAO_LOG = Logger.getLogger("producao");
    private static final Logger LOGGER = Logger.getLogger(LogHelper.class);

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Resource
    private MessageSource messageSource;

    /**
     * Gera mensagem de log para a produ��o.
     * <p>
     * O log � gerado para a category "producao" e deve preferencialmente ser
     * lan�ado ao console.
     *
     * @param msg Chave do arquivo de mensagens.
     * @param params Parametros para o texto de mensagem.
     */
    public void log(final String msg, final Object... params) {
        try {
            String msgForm =
                    this.messageSource.getMessage(msg, params, Locale.getDefault());

            // Verifica se � para concatenar o hor�rio
            if (msgForm.contains("$date$")) {
                final String data = this.sdf.format(new Date());
                msgForm = msgForm.replace("$date$", data);
            }

            PRODUCAO_LOG.info(msgForm);
        } catch (final NoSuchMessageException e) {
            final String msgForm = String.format(MSG_NAO_ENCONTRADA, msg);

            // Log sem stacktrace para a produ��o.
            PRODUCAO_LOG.error(msgForm);

            // Log com stacktrace para a equipe de desenvolvimento.
            LOGGER.error(msgForm, e);
        } catch (final Exception e) {
            // Log sem stacktrace para a produ��o.
            PRODUCAO_LOG.error(ERRO_INESPERADO);

            // Log com stacktrace para a equipe de desenvolvimento.
            LOGGER.error(ERRO_INESPERADO, e);
        }
    }

    /**
     * Gera mensagem de log para a produ��o sem consultar o arquivo de mensagems.
     *
     * @param texto Texto a ser imprimido no log de producao.
     */
    public void logDirect(final String texto) {
        PRODUCAO_LOG.info(texto);
    }

    /**
     * Gera mensagem de log para a produ��o para textos com v�rias linhas.
     *
     * @param texto Texto com varias linhas a ser imprimido no log de producao.
     */
    public void multiLog(final String texto) {
        String[] linhas = texto.split("\n");
        for (String linha: linhas) {
            PRODUCAO_LOG.info(linha);
        }
    }
}
