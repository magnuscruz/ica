package br.gov.dataprev.infra.batch.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileSystemUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.exception.BatchRuntimeException;
import br.gov.dataprev.infra.batch.support.internal.LogHelper;

/**
 * Classe criada pelo projeto GRU da UDRJ e adaptada para o framework batch.
 * <p/>
 *
 * @author DATAPREV/DIT/DEAT + UDRJ
 */
@Component("fileHelper")
public class FileHelper {

    private static final Logger LOG = Logger.getLogger(FileHelper.class);

    private static final String MSG_REMOVENDO_ARQUIVO = "dtp.infra.batch.REMOVENDO_ARQUIVO";
    private static final String MSG_COPIANDO_ARQUIVO = "dtp.infra.batch.COPIANDO_ARQUIVO";
    private static final int BUFFER_SIZE = 1024;

    @Autowired
    private LogHelper logHelper;

    /**
     * Este método faz a cópia de um arquivo para outro.
     *
     * @param arquivoOrigem Pathname do arquivo a ser copiado.
     * @param arquivoDestino Pathname do arquivo de destino
     * @throws IOException Se houver erros durante a copia.
     */
    public void copiaArquivo(final String arquivoOrigem, final String arquivoDestino)
            throws IOException {
        final File f1 = new File(arquivoOrigem);
        final File f2 = new File(arquivoDestino);

        this.logHelper.log(MSG_COPIANDO_ARQUIVO, getFullPath(f1),
                getFullPath(f2));

        final InputStream in = new FileInputStream(f1);
        try {
            final OutputStream out = new FileOutputStream(f2);
            try {
                final byte[] buf = new byte[BUFFER_SIZE];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }

        LOG.debug("Arquivo Copiado");
    }

    /**
     * Move o arquivo Origem para o arquivo Destino.
     *
     * @param arquivoOrigem Pathname do arquivo a ser movido.
     * @param arquivoDestino Pathname do arquivo de destino
     * @throws IOException Se houver erros durante a copia.
     */
    public void moveArquivo(final String arquivoOrigem, final String arquivoDestino)
            throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Parametros de entrada: [" + arquivoOrigem + "], ["
                    + arquivoDestino + "]");
        }
        copiaArquivo(arquivoOrigem, arquivoDestino);
        removeArquivo(arquivoOrigem);

        LOG.debug("Arquivo Movido");
    }

    /**
     * Remove um arquivo do sistema operacional.
     *
     * @param arquivo Pathname do arquivo a ser removido.
     */
    public void removeArquivo(final String arquivo) {
        final File f = new File(arquivo);

        if (f.isFile()) {
            this.logHelper.log(MSG_REMOVENDO_ARQUIVO, getFullPath(f));

            if (!f.exists()) {
                throw new IllegalArgumentException(
                        "Delete: nao existe este arquivo: " + arquivo);
            }

            final boolean success = f.delete();

            if (!success) {
                throw new IllegalArgumentException(
                        "Delete: o arquivo nao pode ser removido: " + arquivo);
            }
            LOG.debug("Arquivo Removido");
        } else {
            LOG.warn("Metodo não foi projetado para apagar pastas");
        }

    }

    /**
     * Obtém a extensão do arquivo.
     *
     * @param fileName Pathname do arquivo a ser obtida a extensão.
     * @return Extensão do arquivo.
     */
    public String getFileExtension(final String fileName) {
        // exemplo bla.bla.bla.bla.txt
        /*
         * tem as barras invertidas porque com expressões
         * regulares o ponto é um caracter especial... Tem
         * que "escapar" este caractere!
         */
        final String[] ext = fileName.split("\\.");
        final int i = ext.length;

        if (i > 1) {
            return ext[i - 1];
        } else {
            return ext[1];
        }
    }

    /**
     * Obtem o caminho completo do arquivo. Útil para saber em tempo de produção o
     * local exato que o arquivo está sendo manipulado. Por exemplo:
     * <br/>
     * <u>Parametro</u>: <code>./arquivo/ELEITOR.TXT</code>
     * <br/>
     * <u>Retorno</u>: <code>/home/user/refappbatch/arquivo/ELEITOR.TXT</code>
     *
     * @param file Arquivo no qual deve-se obter o caminho completo.
     * @return Caminho completo do arquivo.
     */
    public String getFullPath(final File file) {
        String reference;
        try {
            reference = file.getCanonicalPath();
        } catch (final IOException e) {
            reference = file.getAbsolutePath();
        }

        return reference;
    }

    /**
     * Obtém o espaço em disco, medido em KB.
     * @param path Caminho no qual o espaço em disco será mensurado.
     * @return Espaco livre em KB
     */
    public long getFreeSpaceKb(final String path) {
        final String fullPath = getFullPath(new File(path));
        try {
            return FileSystemUtils.freeSpaceKb(fullPath);
        } catch (final IOException e) {
            throw new BatchRuntimeException("Erro obtendo espaço em disco para ["
                    + path + "]", e);
        }
    }
}
