package br.ufc.deti.clusters.kohonen;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.AbstractNetwork;
import br.ufc.deti.clusters.Cluster;
import br.ufc.deti.clusters.Datum;
import br.ufc.deti.clusters.Neuron;
import br.ufc.deti.clusters.Reportable;
import br.ufc.deti.clusters.TwoDNetwork;

/**
 * KohonenNeuralNetwork Copyright 2005 by Jeff Heaton(jeff@jeffheaton.com)
 * 
 * Example program from Chapter 7 Programming Neural Networks in Java
 * http://www.heatonresearch.com/articles/series/1/
 * 
 * This software is copyrighted. You may use it in programs of your own, without
 * restriction, but you may not publish the source code without the author's
 * permission. For more information on distributing this code, please visit:
 * http://www.heatonresearch.com/hr_legal.php
 * 
 * @author Jeff Heaton
 * @version 1.1
 */

public class WTANetwork extends AbstractNetwork implements TwoDNetwork {
	/**
	 * The weights of the output neurons base on the input from the input
	 * neurons.
	 */
	protected Neuron outputNeuronMap[][];

	/**
	 * The learning method.
	 */
	protected int learnMethod = 1;

	/**
	 * The learning rate.
	 */
	protected double learnRate = 0.5;

	/**
	 * Abort if error is beyond this
	 */
	protected double quitError = 0.05;

	/**
	 * Set to true to abort learning.
	 */
	public boolean halt = false;

	protected int size;
	protected int size2 = 1;

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
	public WTANetwork(int inputCount, int outputCount, Reportable owner,
			DefaultListModel dataListModel) {
		super(inputCount, outputCount, owner, dataListModel);
		name = "WTA";
	}

	/**
	 * feed forward
	 */
	public Cluster feedForward(Datum input) {
		this.min = 999999;
		double distance = 0.0;
		Neuron winner = null;
		// calculate activity for the same input
		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size2; j++) {
				distance = distanceFindWinner(input, i, j);
				// Guarda os indices do vencedor
				if (distance < min) {
					min_i = i;
					min_j = j;
					min = distance;
					winner = outputNeuronMap[i][j];
				}
			}
		}
		// Order by distance2
		sortNodes();
		// Atribui ao prototipo vencedor a entrada
		assignToCluster(input, winner);
		return winner;
	}

	/*
	 * @see br.ufc.deti.clusters.AbstractNetwork#updateWeights(br.ufc.deti.clusters.Datum)
	 */
	public void updateWeights(Datum input) {
		outputNeuronMap[min_i][min_j].euclidianRule(rate_atual, input
				.getAttributes());
	}

	/**
	 * This method is called to train the network. It can run for a very long
	 * time and will report progress back to the owner object.
	 * 
	 * @exception java.lang.RuntimeException
	 */
	public void learn() throws RuntimeException {
		for (k = getMinK(); k <= getMaxK(); k++) {
			int sqrtInt = (int) Math.sqrt(k);
			if (sqrtInt * sqrtInt == k) {
				this.size = sqrtInt;
				this.size2 = sqrtInt;
			} else {
				this.size = k;
				this.size2 = 1;
			}
			this.outputNeuronMap = new Neuron[this.size][this.size2];
			runTrain();
		}

	}

	@Override
	protected void initClusters() {
		// Randomize inputs
		sortRandomInputs();
		this.outputClusters = new ArrayList<Cluster>(size * size2);
		this.outputClustersMap = new HashMap<Integer, Cluster>();
		int c = 0;
		for (int i = 0; i < outputNeuronMap.length; i++) {
			for (int j = 0; j < outputNeuronMap[i].length; j++) {
				outputNeuronMap[i][j] = (Neuron) getRandomInput(c);
				outputClusters.add(outputNeuronMap[i][j]);
				outputClustersMap.put(c, outputNeuronMap[i][j]);
				c++;
			}
		}
	}

	/**
	 * Distance function to find winner
	 * 
	 * @param input
	 *            input
	 * @param i
	 *            index i
	 * @param j
	 *            index j
	 * @return
	 */
	protected double distanceFindWinner(Datum input, int i, int j) {
		double distance = outputNeuronMap[i][j].euclidianDistance(input
				.getAttributes());
		return distance;
	}

	@Override
	public double calculateCoeficiente(int i) {
		// TODO Auto-generated method stub
		return 0;
	}
}