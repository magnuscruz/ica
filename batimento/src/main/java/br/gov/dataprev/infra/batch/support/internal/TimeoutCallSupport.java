package br.gov.dataprev.infra.batch.support.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

/**
 * <b>Atenção!</b> Classe interna do framework.
 * Uso não recomendado, pois pode sofrer modificação severa sem notificação prévia.
 * <p>
 * O componente TimeoutCallSupport é responsável criar uma thread avulsa para fazer a chamada.
 * <p>
 * Caso esta chamada demore muito, o componente mata a thread avulsa lançando 
 * uma {@link TimeoutException} para a Thread principal, evitando que a mesma fique bloqueada.
 * <p>
 * ATENÇÃO! Muita cautela ao usar o componente, uma vez que recursos podem não ser liberados 
 * no Sistema Operacional. Este componente NÃO deve ser usado dentro de containers/servidores JEE.
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
		
		// Aguarda a thread até o tempo especificado.
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
						"Situação inesperada. Foi capturada uma exceção que não possui herança com Exception ou Error");
				
				throw new Error(crun.theThrowable);
			}
		}
		
		// Recupera o valor que está na thread.
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
			// Ocorreu o timeout da invocação. Foi invocado o comando stop.	
			
			// TODO - Adicionar futuramente a possibilidade de colocar um código de cleanup aqui.
			
			throw td; // Relançando a exceção - cf. JavaDoc da ThreadDeath.
		} catch (Throwable e) {
			theThrowable = e;
		} finally {			
			cdLatch.countDown();
		}		
	}
}
