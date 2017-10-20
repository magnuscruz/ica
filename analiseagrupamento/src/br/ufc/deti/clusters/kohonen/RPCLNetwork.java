package br.ufc.deti.clusters.kohonen;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.Cluster;
import br.ufc.deti.clusters.Datum;
import br.ufc.deti.clusters.Neuron;
import br.ufc.deti.clusters.Reportable;

public class RPCLNetwork extends WTANetwork {
	//Coeficiente de potencia para calculo como elemento penalizador da distncia euclidiana
	//ajuda a minimizar a ocorrencia de unidades duplicadas de prototipos.
	private double gama = 0.05;
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
	public RPCLNetwork(int inputCount, int outputCount, Reportable owner, DefaultListModel dataListModel) {
		super(inputCount, outputCount, owner, dataListModel);
		name = "RPCL";
	}

	@Override
	public void updateWeights(Datum input) {
		Neuron winner = outputNeuronMap[min_i][min_j];
		Cluster rival = findSecond(winner);
		super.updateWeights(input);
		// update - Rival Penalized
		rival.euclidianRule(rate_atual*(-gama), input
				.getAttributes());
	}

	public double getGama() {
		return gama;
	}
	public void setGama(double gama) {
		this.gama = gama;
	}

}
