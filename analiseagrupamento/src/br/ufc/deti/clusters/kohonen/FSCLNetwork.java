package br.ufc.deti.clusters.kohonen;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.Datum;
import br.ufc.deti.clusters.Reportable;

public class FSCLNetwork extends WTANetwork {
	//Coeficiente de potencia para calculo como elemento ponderador da distncia euclidiana
	//ajuda a minimizar a ocorrncia de unidades mortas.
	private double z = 1.0;
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
	public FSCLNetwork(int inputCount, int outputCount, Reportable owner, DefaultListModel dataListModel) {
		super(inputCount, outputCount, owner, dataListModel);
		name = "FSCL";
	}
	/**
	 * Distance function to find winner
	 * Coeficiente de potencia para calculo como elemento ponderador da distncia euclidiana
	 * ajuda a minimizar a ocorrncia de unidades mortas.
	 * @param input input
	 * @param i index i
	 * @param j index j
	 * @return 
	 */
	protected double distanceFindWinner(Datum input, int i, int j) {
		double distance;
		int numWins = outputNeuronMap[i][j].getPoints().size();
		double f = Math.pow(((double)numWins/(double)time), z);
		if (testEpoc) {
			f = 1;
		}
		distance =
			f * super.distanceFindWinner(input, i, j);
		return distance;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}

}
