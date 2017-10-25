package br.gov.dataprev.infra.batch.support.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

/**
 * <b>Aten��o!</b> Classe interna do framework.
 * Uso n�o recomendado, pois pode sofrer modifica��o severa sem notifica��o pr�via.
 * <p>
 * O componente TimeoutCallSupport � respons�vel criar uma thread avulsa para fazer a chamada.
 * <p>
 * Caso esta chamada demore muito, o componente mata a thread avulsa lan�ando 
 * uma {@link TimeoutException} para a Thread principal, evitando que a mesma fique bloqueada.
 * <p>
 * ATEN��O! Muita cautela ao usar o componente, uma vez que recursos podem n�o ser liberados 
 * no Sistema Operacional. Este componente N�O deve ser usado dentro de containers/servidores JEE.
 *
 * @author DATAPREV/DIT/DEAT
 * @deprecated Componente deve ser usado com muita cautela.
 */
@Deprecated
public class TimeoutCallSupport {
	
    static final Logger LOG = Logger.getLogger(TimeoutCallSupport.class);
	    
	/**
	 * Executa a chamada, usando 
	 * @param milisecounds
	 * @param callable
	 * @return
	 * @throws Throwable
	 */
	public static <T> T doWithTimeout(long milisecounds, 
			Callable<T> callable) throws Exception {
		CallableRunnable<T> crun = new CallableRunnable<T>(callable);

		Thread callThread = new Thread(crun);
		callThread.start();
		
		// Aguarda a thread at� o tempo especificado.
		boolean sucesso = crun.cdLatch.await(milisecounds, TimeUnit.MILLISECONDS);
		
		// Verifica se houve timeout
		if (!sucesso) {
			callThread.stop();
			LOG.error("Thread interrompida por timeout ["+milisecounds+"] ms");
			throw new TimeoutException(Long.toString(milisecounds));
		}
		
		// Verifica se houve erros
		if (crun.theThrowable != null) {
			
			if (crun.theThrowable instanceof Exception) {
				throw (Exception)crun.theThrowable;
			} else if (crun.theThrowable instanceof Error){
				throw (Error)crun.theThrowable;
			} else {
				LOG.fatal(crun.theThrowable+ 
						"Situa��o inesperada. Foi capturada uma exce��o que n�o possui heran�a com Exception ou Error");
				
				throw new Error(crun.theThrowable);
			}
		}
		
		// Recupera o valor que est� na thread.
		T thisReturn = crun.theReturn;		
		return thisReturn;
	}
}

class CallableRunnable<T> implements Runnable {
	private Callable<T> callable;
	public T theReturn;
	public Throwable theThrowable;
	
	CountDownLatch cdLatch = new CountDownLatch(1);

	public CallableRunnable(Callable<T> call) {
		this.callable = call;
	}

	public void run() {
		try {
			theReturn = callable.call();
		} catch (ThreadDeath td) {
			// Ocorreu o timeout da invoca��o. Foi invocado o comando stop.	
			
			// TODO - Adicionar futuramente a possibilidade de colocar um c�digo de cleanup aqui.
			
			throw td; // Relan�ando a exce��o - cf. JavaDoc da ThreadDeath.
		} catch (Throwable e) {
			theThrowable = e;
		} finally {			
			cdLatch.countDown();
		}		
	}
}
