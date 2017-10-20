package br.ufc.deti.clusters;


// Neuron class : a very simple neuron model
// by Yoonsuck Choe <yschoe@cs.utexas.edu>
// Fri Apr 4 05:01:50 CST 1997

public class Neuron extends Cluster {

	// constructor
	public Neuron() {
		super(0);
	}

	/**
	 * @param inputNeuronCount
	 */
	public Neuron(int inputNeuronCount) {
		super(inputNeuronCount);
	}

	/**
	 * @param inputNeuronCount
	 */
	public Neuron(Cluster cluster) {
		super(cluster.getAttributes().length);
		this.setCentroide(cluster.getCentroide());
		this.setClusterNumber(cluster.getClusterNumber());
		
	}

	@Override
	public boolean equals(Object obj) {
		double[] weights = getCentroide().getAttributes();
		double[] weights2 = ((Neuron)obj).getCentroide().getAttributes();
		boolean equals = true;
		for (int i = 0; i < getInputDimension(); ++i) {
			if (weights[i] != weights2[i]) {
				return false;
			}
		}		
		return equals;
	}
}