package br.ufc.deti.clusters;

public interface Clustering {

	/**
	 * Called to learn from training sets.
	 * 
	 * @exception java.lang.RuntimeException
	 */
	void learn() throws RuntimeException;

	/**
	 * Called to test from training sets.
	 * 
	 * @exception java.lang.RuntimeException
	 */
	void test() throws RuntimeException;

	/**
	 * Called to present an input pattern.
	 * 
	 * @param input
	 *            The input pattern
	 */
	double train(Datum input);
}