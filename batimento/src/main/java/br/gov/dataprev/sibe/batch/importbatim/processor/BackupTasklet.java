package br.gov.dataprev.sibe.batch.importbatim.processor;

import java.io.File;
import java.io.IOException;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.support.FileHelper;
import br.gov.dataprev.infra.batch.support.ProductionHelper;
import br.gov.dataprev.infra.batch.support.StepPhase;

/**
 * Executa backup dos arquivos que est�o na pasta 'refappbatch.arquivo.eleitor.dir'.
 *
 * @author DATAPREV/DIT/DEAT
 */
@Component("backupTasklet")
@Scope("step")
public class BackupTasklet implements Tasklet {

    @Autowired
    private FileHelper fileHelper;

    @Value("${refappbatch.arquivo.eleitor.dir}")
    private String eleitorDir;

    @Value("${refappbatch.arquivo.backup.dir}")
    private String backupDir;

    @Autowired
    private ProductionHelper pHelper;

    public RepeatStatus execute(final StepContribution contribution,
            final ChunkContext chunkContext) throws Exception {
        final File file = new File(this.eleitorDir);
        final File fileBackup = new File(this.backupDir);

        this.pHelper.notifyIO(StepPhase.READ,
                "Arquivos na pasta [" + this.fileHelper.getFullPath(file) + "]");
        this.pHelper.notifyIO(StepPhase.WRITE,
                "Pasta de backup em [" + this.fileHelper.getFullPath(fileBackup) + "]");

        final String[] arquivos = file.list();
        if (arquivos != null) {
            copiaArquivos(contribution, arquivos);
        }

        pHelper.publicarNoSumario("Espaco em disco",
                fileHelper.getFreeSpaceKb(backupDir));

        return RepeatStatus.FINISHED;
    }

    private void copiaArquivos(final StepContribution contribution,
            final String[] arquivos) throws IOException {
        for (final String arq : arquivos) {
            if (arq.endsWith(".CSV") || arq.endsWith(".TXT")) {
                final String arquivoDestino = this.backupDir + "/" + arq;
                final String arquivoOrigem = this.eleitorDir + "/" + arq;

                this.fileHelper.copiaArquivo(arquivoOrigem, arquivoDestino);

                // Atualiza os contadores do spring.
                contribution.incrementReadCount();
                contribution.incrementWriteCount(1);
            }
        }
    }

}
