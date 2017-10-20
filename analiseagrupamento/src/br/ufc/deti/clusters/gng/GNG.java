/*
 Copyright Laurent BOUGRAIN CORTEX Team (LORIA) (2002 - 2006)

 Main contributors:
 Laurent BOUGRAIN (2002-2006), Christophe MOZZATI (2003),
 Eric DONDELINGER (2003), Marie TONNELIER (2004-2006)

 Thanks to:
 Cyprien NOEL (2002), Severine PIFFRE (2003),
 Jean-Etienne FIGARD (2003), Guillaume FERRIER (2003),
 Baptiste BUQLIER (2004), Bertrand BENOIT (2005),
 Pierre HENRY (2005), Julien MAIRE (2005),
 Cedric BORGESE (2005)

 Laurent.Bougrain@loria.fr

 This software is a computer program whose purpose is to provide a
 decision-making platform written in Java. It has been developped
 to favorize the developpement and use of neural networks.

 This software is governed by the CeCILL license under French law and
 abiding by the rules of distribution of free software. You can use, 
 modify and/ or redistribute the software under the terms of the CeCILL
 license as circulated by CEA, CNRS and INRIA at the following URL
 "http://www.cecill.info". 

 As a counterpart to the access to the source code and rights to copy,
 modify and redistribute granted by the license, users are provided only
 with a limited warranty and the software's author, the holder of the
 economic rights, and the successive licensors  have only limited
 liability. 

 In this respect, the user's attention is drawn to the risks associated
 with loading, using, modifying and/or developing or reproducing the
 software by the user in light of its specific status of free software,
 that may mean that it is complicated to manipulate, and that also
 therefore means that it is reserved for developers and experienced
 professionals having in-depth IT knowledge. Users are therefore encouraged
 to load and test the software's suitability as regards their requirements
 in conditions enabling the security of their systems and/or data to be
 ensured and, more generally, to use and operate it in the same conditions
 as regards security. 

 The fact that you are presently reading this means that you have had
 knowledge of the CeCILL license and that you accept its terms.
 */
package br.ufc.deti.clusters.gng;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.AbstractNetwork;
import br.ufc.deti.clusters.Cluster;
import br.ufc.deti.clusters.Datum;
import br.ufc.deti.clusters.Neuron;
import br.ufc.deti.clusters.Reportable;

/**
 * GNG class represents a Growing Neural Gas network.
 * 
 * @author Laurent BOUGRAIN, Mohammed ATTIK, Eric DONDELINGER, Marie TONNELIER
 * @version $Revision: 1.5 $
 */

public class GNG extends AbstractNetwork implements java.io.Serializable {

	/* Network Parameters */

	/**
	 * 
	 */
	private static final long serialVersionUID = -1245860969030582073L;

	/** Error coefficient for a new node inserted */
	public float errorCoefWhenNew = 0.5f;

	/**
	 * Error leak, used to decrease during time error accumulator of worst unit
	 * and its neighbor
	 */
	public float errorLeak = 0.0005f;

	/**
	 * Growing period (a new node is created if current step is a multiple of
	 * this period)
	 */
	public int stepBeforeGrowing = 200;

	private int epoca;

	/* Methods */

	/**
	 * Constructs a Growing Neural Gas with type "GNG".
	 * 
	 * @param inputCount
	 *            size of prototype
	 * @param outputCount
	 *            number of initials nodes - prototypes
	 * @param owner
	 *            Owner Application
	 * @param dataListModel
	 *            Data List Model
	 */
	public GNG(int inputCount, int outputCount, Reportable owner,
			DefaultListModel dataListModel) {
		super(inputCount, outputCount, owner, dataListModel);
	}

	/**
	 * Gets a short description of a concrete data-mining method.
	 * 
	 * @return a description of the method
	 * @see fr.loria.cortex.ginnet.dynnet.methods.DataMiningMethod#getDescription()
	 */
	public String getDescription() {
		return "Growing Neural Gas (Fritzke, 1994-1995)";
	}

	/**
	 * @see br.ufc.deti.clusters.AbstractNetwork#learn()
	 */
	public void learn() throws RuntimeException {
		GNGNode winner;
		int sizeDataTrain = getTrainListModel().getSize();
		// for (int k = getMinK(); k <= getMaxK(); k++) {
		k = getMinK();
		initializeVariables();
		initClusters();
		clearPoints();
		for (epoca = 0; epoca < epocas + (test ? 1 : 0); epoca++) {
			testEpoc = false;
			if (epoca == epocas) {
				testEpoc = true;
			}
			clearPointsByEpoc();
			accErrorMin = 0.0;
			double distMin = 0.0;
			int count = 0;
			// loop through all training sets to determine correction
			for (Object element : getInputList()) {
				Datum datum = (Datum) element;
				if (datum != null) {
					distMin = train(datum);
					accErrorMin += distMin;

					winner = (GNGNode) outputClusters.get(0);
					// update accumulator
					winner.accumulator += winner.getError();

					// test if it's time to grow
					if (this.timeToGrow(count)) {
						// Create a new node between the two with maximum error
						// sums
						GNGNode maxError1 = findMaxAccumulator();
						GNGNode maxError2 = this.topology
								.findMaxAccumulatorNeighbor(maxError1);

						this.createNewNodeMean(maxError1, maxError2);
						// Update accumulators
						maxError1.accumulator *= 1 - this.errorCoefWhenNew;
						maxError2.accumulator *= 1 - this.errorCoefWhenNew;
					}
					// Updates all accumulators
					this.updateAccumulators();

					count++;
				}
			}
			updateForEpoc(sizeDataTrain, epoca, accErrorMin);

			// Execution pause
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}
		}
		/*
		 * calculateIndices(outputClusters.size()); saveNumWinsForPrototipo(); }
		 */
	}

	/**
	 * @see br.ufc.deti.clusters.AbstractNetwork#initClusters()
	 */
	protected void initClusters() {
		// Randomize inputs
		sortRandomInputs();
		this.outputClusters = new ArrayList<Cluster>(this.k);
		this.outputClustersMap = new HashMap<Integer, Cluster>();
		for (int i = 0; i < this.k; i++) {
			GNGNode neuron = new GNGNode(getRandomInput(i));
			outputClusters.add(neuron);
			outputClustersMap.put(i, neuron);
		}
	}

	/**
	 * @see br.ufc.deti.clusters.AbstractNetwork#calculateCoeficiente(int)
	 */
	public double calculateCoeficiente(int i) {
		Cluster winner = this.outputClusters.get(0);
		Cluster node = this.outputClusters.get(i);
		// Is Winner or Neighbors
		if (this.topology.getBond(winner, node) != null || i == 0) {
			return this.rate_atual;
		}
		return 0.0;
	}

	/**
	 * Updates all accumulator
	 */
	public void updateAccumulators() {
		GNGNode node;
		for (int i = 0; i < this.outputClusters.size(); i++) {
			node = (GNGNode) this.outputClusters.get(i);
			node.accumulator *= 1 - this.errorLeak;
		}
	}

	/**
	 * Finds the maximum accumulator of the population of nodes
	 * 
	 * @return Node with max accumulator
	 */
	public GNGNode findMaxAccumulator() {
		GNGNode max = (GNGNode) this.outputClusters.get(0);
		for (int i = 1; i < this.outputClusters.size(); i++) {
			GNGNode node = (GNGNode) this.outputClusters.get(i);
			if (node.accumulator > max.accumulator)
				max = node;
		}
		return max;
	}

	/**
	 * Creates a new Node between 2 other Nodes.
	 * 
	 * @param node1
	 *            first node
	 * @param node2
	 *            second node
	 */
	public void createNewNodeMean(GNGNode node1, GNGNode node2) {
		int length = node1.getAttributes().length;
		GNGNode newNode = new GNGNode(length);
		newNode.setDistance2(Math.pow(
				(node1.getError() + node2.getError()) / 2, 2.0));
		newNode.accumulator = (node1.accumulator + node2.accumulator) / 2;
		for (int i = 0; i < length; i++) {
			newNode.getAttributes()[i] = (node1.getAttributes()[i] + node2
					.getAttributes()[i]) / 2;
		}
		// Add the new Node to the PopulationCompetitive
		this.outputClusters.add(newNode);
		// Delete bond and create new Bonds
		this.topology.free(node1, node2);
		this.topology.bind(node1, newNode);
		this.topology.bind(newNode, node2);
	}

	/**
	 * Tests if the Network can growing
	 * 
	 * @param patternRank
	 *            number of the current pattern in current cycle
	 * @return boolean set at true if a new node has to be inserted
	 */
	public boolean timeToGrow(int patternRank) {
		int currentPattern = (getInputList().length * this.epoca) + patternRank;
		// Not first step and not maximum size reached
		// and number of pattern presented is a multiple of growing period =>
		// growing
		return (((currentPattern % this.stepBeforeGrowing) == 0)
				&& (this.outputClusters.size() < this.getMaxK()) && (this.epoca != 0));
	}

	@Override
	protected void changeTopology(Cluster winner) {
		super.changeTopology(winner);
		this.deleteUnBounded();
	}

	/**
	 * Deletes all unbounded Nodes
	 */
	public void deleteUnBounded() {
		Neuron node;
		Object[] objects = outputClusters.toArray();
		for (int i = objects.length - 1; i >= 0; i--) {
			node = (Neuron) objects[i];
			// if no bonds, delete the node
			if (!(this.topology.isBounded(node))) {
				this.outputClusters.remove(node);
				node = null; // for garbage collector
			}
		}
	}

	@Override
	protected Class getProtoType() {
		return GNGNode.class;
	}

}
