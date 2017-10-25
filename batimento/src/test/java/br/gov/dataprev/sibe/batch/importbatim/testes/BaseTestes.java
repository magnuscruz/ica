package br.gov.dataprev.sibe.batch.importbatim.testes;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.regex.Pattern;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.mockito.Mockito;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.support.PropertiesConverter;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.dataprev.infra.batch.support.FileHelper;

/**
 * Classe base de testes.
 * @author DATAPREV/DIT/DEAT
 */
public class BaseTestes {

    private static final String ORIGEM_DIR = "./arquivos/batinv";
    private static final String SAIDA_DIR = "./arquivos/saida";
    private static final String BACKUP_DIR = "./arquivos/backup";
    private static final String TEST_DIR = "./arquivos/test";

    @Autowired
    private FileHelper fileHelper;

    private final Random rnd = new Random();

    public void initTestes(final String pattern) throws Exception {
        limpaDiretorio(BACKUP_DIR);
        limpaDiretorio(SAIDA_DIR);
        limpaDiretorio(ORIGEM_DIR);

        copy(pattern);
    }

    private void limpaDiretorio(final String dir) throws Exception {
        final File file = new File(dir);
        if (!file.exists()) {
        	file.mkdir();
        }
        for (final File f : file.listFiles()) {
            if (f.isFile()) {
                if (!f.delete()) {
                    throw new RuntimeException("Impossivel apagar o arquivo "
                            + f.getAbsolutePath());
                }
            }
        }
    }

    private void copy(final String pattern) throws Exception {
        final File file = new File(TEST_DIR);
        for (final File f : file.listFiles()) {
            if (Pattern.matches(pattern, f.getCanonicalPath())) {
                final String arquivoOrigem = f.getCanonicalPath();
                final String arquivoDestino = ORIGEM_DIR + "/" + f.getName();

                this.fileHelper.copiaArquivo(arquivoOrigem, arquivoDestino);
            }
        }
    }

    protected int gerarNumeroAleatorio() {
        return this.rnd.nextInt(Integer.MAX_VALUE);
    }

    public JobParameters getJobParameters(final String params) {
        return new DefaultJobParametersConverter()
                .getJobParameters(PropertiesConverter
                        .stringToProperties(params));
    }

    /**
     * Injeta um mock do Mockito na instancia cuja dependencia esta sendo testada.
     *
     * @param instance Objeto com dependencia.
     * @param clazz Classe cuja dependencia tem que ser tratada.
     * @param <T> Tipo do mock a ser criado.
     * @return Mock injetado.
     */
    public final <T> T injectMock(final Object instance, final Class<T> clazz) {
        final Class<?> classInstance = instance.getClass();

        for (final Field f : classInstance.getDeclaredFields()) {
            if (f.getType().equals(clazz)) {
                final boolean ac = f.isAccessible();
                f.setAccessible(true);

                T mock;
                try {
                    final Object actual = f.get(instance);
                    if (actual != null) {
                        throw new RuntimeException("Objeto ja foi injetado em " + f.getName());
                    }
                    mock = Mockito.mock(clazz);
                    f.set(instance, mock);
                } catch (final IllegalArgumentException e) {
                    throw new RuntimeException("Erro ao injetar mock", e);
                } catch (final IllegalAccessException e) {
                    throw new RuntimeException("Erro ao injetar mock", e);
                }

                f.setAccessible(ac);

                return mock;
            }
        }

        throw new RuntimeException("Nao ha dependencia para " + clazz.getSimpleName());
    }

    /**
     * Obtem um dataset para ser usado pelo DBUnit na geracao da massa de dados.
     * @param dataset Caminho no classpath para o dataset.
     * @return Dataset do DbUnit.
     * @throws DataSetException Se houver erro na criacao do dataset.
     */
    protected static FlatXmlDataSet obterDataset(final String dataset) throws DataSetException {
        final FlatXmlDataSetBuilder dataSetBuilder = new FlatXmlDataSetBuilder();
        final InputStream is =
                Thread.currentThread().getContextClassLoader().getResourceAsStream(dataset);
        return dataSetBuilder.build(is);
    }
}
