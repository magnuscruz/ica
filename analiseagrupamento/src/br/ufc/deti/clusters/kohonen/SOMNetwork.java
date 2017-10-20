package br.ufc.deti.clusters.kohonen;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.Datum;
import br.ufc.deti.clusters.Reportable;


/**
 * SOM network
 * @author Magnus Alencar da Cruz
 * @version 1.1
 */

public class SOMNetwork extends WTANetwork {

	/**
	 * The constructor.
	 * 
	 * @param inputCount
	 *            Number of attributes input
	 * @param outputCount
	 *            Number of output neurons
	 * @param owner
	 *            The owner object, for updates.
	 */
	public SOMNetwork(int inputCount,
			int outputCount,
			Reportable owner,
			DefaultListModel dataListModel) {
		super(inputCount, outputCount, owner, dataListModel);
		name = "SOM";
	}

	/**
	 * Update weight : within a fixed radius
	 * @see br.ufc.deti.clusters.kohonen.WTANetwork#updateWeights(br.ufc.deti.clusters.Datum)
	 */
	public void updateWeights(Datum input) {
		// find boundary of weight update
		int diff_i = min_i - (int)radius_atual;
		int i1 = (diff_i < 0) ? 0 : diff_i;
		diff_i = min_i + (int)radius_atual;
		int i2 = (diff_i > size - 1) ? size - 1 : diff_i;
		int diff_j = min_j - (int)radius_atual;
		int j1 = (diff_j < 0) ? 0 : diff_j;
		diff_j = min_j + (int)radius_atual;
		int j2 = (diff_j > size2 - 1) ? size2 - 1 : diff_j;

		// update weights within the boundary
		for (int i = i1; i <= i2; i++) {
			for (int j = j1; j <= j2; j++) {
				outputNeuronMap[i][j].euclidianRule(rate_atual, input.getAttributes());
			}
		}
	}
}