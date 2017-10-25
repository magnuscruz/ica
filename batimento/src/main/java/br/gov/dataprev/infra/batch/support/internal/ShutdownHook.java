package br.gov.dataprev.infra.batch.support.internal;

import br.gov.dataprev.infra.batch.support.MainHelper;

/**
 * <b>Atenção!</b> Classe interna do framework. Uso não recomendado, pois pode
 * sofrer modificação severa sem notificação prévia.
 * <p>
 * O componente ShutdownHook serve para detectar problemas em gerais que não foram
 * avaliados pela aplicacao.
 * <p>
 * Como pode haver estouros de memória e outros problemas, este hook verifica
 * se o MainHelper.safeSystemExit() foi chamado e se foi verificado se existe
 * espaco em disco antes de iniciar o Job.
 *
 * @see MainHelper
 * @author DATAPREV/DIT/DEAT
 */
public class ShutdownHook extends Thread {

    private static final int UNSAFE = -1;
    private static final String MSG_ERRO_SEM_RETURN_CODE =
            "ATENCAO! A fase nao informou o return code corretamente."
                    + " [Use o MainHelper.safeSystemExit()]";
    private static final String MSG_ERRO_ESPACO =
            "ATENCAO! A fase nao verificou pelo espaco em disco."
                    + " [Use o DiskSpaceInitializer]";

    private static int code = UNSAFE;
    private static boolean spaceChecked = false;

    public static void setExitCode(final int code) {
        ShutdownHook.code = code;
    }

    @Override
    public void run() {
        if (code == UNSAFE) {
            System.out.println(MSG_ERRO_SEM_RETURN_CODE);
        } else if (!spaceChecked) {
            System.out.println(MSG_ERRO_ESPACO);
        }

        System.out.println("RETURN CODE = " + code);
    }

    public static void setSpaceChecked(final boolean b) {
        spaceChecked = b;
    }

    public static boolean getSpaceChecked() {
        return spaceChecked;
    }

}
