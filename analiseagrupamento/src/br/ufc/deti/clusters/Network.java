package br.ufc.deti.clusters;

public interface Network extends Clustering {

	/**
	 * update weight : within a fixed radius
	 */
	void updateWeights(Datum input);

	/**
	 * Verify activation.
	 * @param input
	 * @return
	 */
	Cluster feedForward(Datum input);
}