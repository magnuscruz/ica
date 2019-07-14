package br.ufc.deti.clusters;

import java.util.List;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.gng.Topology;

public abstract class AbstractNetwork extends AbstractClustering implements
		Network {

	/** Bonds between Nodes */
	protected Topology topology = new Topology();

	// local variables
	protected double rate_atual = 0.5;

	protected double rate_ini = 0.5;

	protected double rate_fim = 0.0001;

	//SOM
	protected double radius_atual = 1;

	protected double radius_ini = 1;

	protected double radius_fim = 0.0001;

	//Neural gas
	protected double lambda_atual = 0.5;

	protected double lambda_ini = 0.5;

	protected double lambda_fim = 0.0001;

	//Kohonen
	protected double min;

	protected int min_i;

	protected int min_j;

	/**
	 * Calcula o coeficiente que sera aplicado na atualizao do pesos
	 * 
	 * @param i
	 * @return
	 */
	public abstract double calculateCoeficiente(int i);

	public AbstractNetwork(int inputCount, int outputCount, Reportable owner, List<Datum> dataListModel) {
		super(inputCount, outputCount, owner, dataListModel);
	}

	/**
	 * To initialize variables
	 */
	protected void initializeVariables() {
		super.initializeVariables();
		// inicializa todas as variveis de controle
		this.min = 0.0;
		this.min_i = this.min_j = 0;

		this.radius_atual = this.k;
		this.radius_ini = this.k;
		this.radius_fim = 0.0001;

		this.rate_ini = 0.5;
		this.rate_atual = 0.5;
		this.rate_fim = 0.0001;
		
		this.lambda_atual = k;
		this.lambda_ini = k;
		this.lambda_fim = 0.01;		
	}

	/**
	 * @see br.ufc.deti.clusters.Network#updateWeights(Datum)
	 */
	public void updateWeights(Datum input) {
		// Update prototypes switch his rank
		int i = 0;
		for (Cluster element : outputClusters) {
			double coefToApply = calculateCoeficiente(i++);
			element.euclidianRule(coefToApply, input.getAttributes());
		}

	}

	/**
	 * @see br.ufc.deti.clusters.Network#feedForward(Datum)
	 */
	public Cluster feedForward(Datum input) {
		//To find winner neuron
		Cluster winner = this.findWinner(input);

		// To change Topology Network
		changeTopology(winner);

		// Atribui ao prototipo vencedor a entrada
		assignToCluster(input, winner);

		return winner;
	}

	/**
	 * Change topology in the network
	 * @param winner
	 */
	protected void changeTopology(Cluster winner) {
		//TODO Only to implement if it's needed
	}

	/**
	 * Find the winner node. It's the Node with little value.
	 * @param input 
	 * 
	 * @return the winner Node
	 */
	protected Cluster findWinner(Datum input) {
		//To Calculate distances
		calculateActivity(input.getAttributes());
		// Order by distance2
		sortNodes();
		Cluster winner = this.outputClusters.get(0);
		return winner;
	}

	/**
	 * Find the second node. It's the Node with little value after the winner.
	 * 
	 * @param winner
	 *            winner Node
	 * @return the second Node or winner if there is only one node
	 */
	protected Cluster findSecond(Cluster winner) {
		if (outputClusters.size() == 1)
			return winner;
		Cluster second = this.outputClusters.get(0);
		if (second == winner)
			second = this.outputClusters.get(1);
		for (int i = 1; i < this.outputClusters.size(); i++) {
			Cluster candidate = this.outputClusters.get(i);
			if ((candidate.getDistance2() < winner.getDistance2())
					&& (candidate != winner)) {
				second = candidate;
			}
		}
		return second;
	}

	/**
	 * Train for number of steps
	 */
	public double train(Datum input) {
		time++;
		// train for number of steps
		Cluster errorMin = feedForward(input);
		if (!testEpoc)
			updateWeights(input);
		processRateAtual();
		processRadiusAtual();

		return errorMin.getError();
	}

	/**
	 * 
	 */
	protected void processRadiusAtual() {
		radius_atual = radius_ini
				* Math.pow((radius_fim / radius_ini), time / (double) t_max);
	}

	/**
	 * 
	 */
	protected void processRateAtual() {
		rate_atual = rate_ini
				* Math.pow((rate_fim / rate_ini), time / (double) t_max);
		lambda_atual = lambda_ini
		* Math.pow((lambda_fim / lambda_ini), time / (double) t_max);
	}
	@Override
	protected Class getProtoType() {
		return Neuron.class;
	}
}
