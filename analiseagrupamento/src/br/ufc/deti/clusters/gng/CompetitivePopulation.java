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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import br.ufc.deti.clusters.Neuron;

/**
 * CompetitivePopulation class represents a Population for competitive learning.
 * 
 * @author Laurent BOUGRAIN, Mohammed ATTIK, Eric DONDELINGER
 * @version $Revision: 1.1 $
 */

public class CompetitivePopulation implements java.io.Serializable {
	
	/* Fields */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7123370705053655460L;
	/** List of nodes of the population */
	private ArrayList<Neuron> nodes;
    
	/* Methods */
	
	/**
	 * Class Constructor
	 * @param populationSize size of the population
	 * @param inputSize size of patterns (prototypes)
	 * @param classOfNode name of class used for Nodes
     * @param distanceFunction function used to compute distance between desired and computed prototypes
     * @param aggregatingFunction aggregating function
	 */
	public CompetitivePopulation(int populationSize, int inputSize) {
        this.nodes = new ArrayList<Neuron>();
        Neuron node;
		for(int i=0; i < populationSize; i++) {
			try{
				node = new Neuron();
				node.setAttributes(new double[inputSize]);
				this.addNode(node);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

    /**
	 * Add a node to the population
	 * @param node node to add
	 */
	public void addNode(Neuron node) {
		nodes.add(node);
	}
    
    /**
     * Gets the number of nodes in the population.
     * @return number of nodes
     */
    public int getNbNodes() {
        return this.nodes.size();
    }
    
    /**
     * Gets the node at given position in the population.
     * @param index position of node in the population
     * @return node at given index
     * @throws IllegalArgumentException if given index isn't a valid node index
     */
    public Neuron getNode(int index) {
        if((index < 0) || (index > getNbNodes()))
            throw new IllegalArgumentException("There is no node at index "+index);
        return this.nodes.get(index);
    }
    
    /**
     * Removes given node from population.
     * @param node node to remove
     */
    public void removeNode(Neuron node) {
        this.nodes.remove(this.nodes.indexOf(node));
    }
	/**
	 * Initialize each prototype value to a random value (min..max)
	 * @param min minimum value for initialization 
	 * @param max maximum value for initialization
	 */
	public void  initializePrototypes(double min, double max){
		for (int i = 0; i < this.nodes.size(); i++){
			Neuron n = this.nodes.get(i);
			double[] weights = n.getAttributes();
			for (int j = 0; j < weights.length; j++){
				double value = (max - min) * ((double) Math.random()) + min;
				weights[j] = value;
			}
		}
	}
	
	/**
	 * Print value of each Node
	 */
	public void showNodeValues(){
		for (int i = 0; i < this.nodes.size(); i++){
			System.out.println(this.nodes.get(i).getDistance2() + " ");
		}
	}
	
	/**
	 * Saves prototype of Node into a file.
	 * @param fileName file to save prototypes
	 */
	public void writePrototypes(String fileName){
		File file = new File(fileName);
		try{
			BufferedWriter bout = new BufferedWriter(new FileWriter(file));
			PrintWriter wout;
			wout = new PrintWriter(bout);
			
			for (int i = 0; i < this.nodes.size(); i++){
				Neuron n = this.nodes.get(i);
				double[] weights = n.getAttributes();
				for (int j=0; j<weights.length;j++){
					wout.print(weights[j] + " ");
				}
				wout.println("");
			}
			wout.close();
		}catch(Exception e){
			System.out.println("*** ERROR SAVING PROTOTYPES ***");
		}
	}
	
	/**
	 * Prints the Vector of all the Node of the Map.
	 */
	public void showPrototypes(){
	    System.out.println("Prototypes: ");
		for (int i = 0; i < this.nodes.size(); i++){
			Neuron n = this.nodes.get(i);
			double[] weights = n.getAttributes();
			for (int j=0; j<weights.length;j++){
				System.out.print(weights[j] + " ");
			}
			System.out.println("");
		}
	}
}
