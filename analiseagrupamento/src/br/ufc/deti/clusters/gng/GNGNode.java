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

import br.ufc.deti.clusters.Cluster;
import br.ufc.deti.clusters.Neuron;

/**
 * GNG Node class is used in a competitivePopulation. A Kohonen node have coord (x,y,z).
 * 
  * @author Laurent BOUGRAIN, Mohammed ATTIK, Eric DONDELINGER
 * @version $Revision: 1.2 $
 */
public class GNGNode extends Neuron implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7562037221291612258L;
	/* Fields */
	
	/** Sum of all errors made by this node, used to determine where to add a new prototype */
	public double accumulator = 0.0;
	
	/* Methods */
	
	/**
	 * Creates a GNG node
	 */
	public GNGNode(){
		super();
	}
	
	/**
	 * Creates a GNG node with given size
	 * @param size initial number of nodes
	 */
	public GNGNode(int size) {
		super(size);
	}
	/**
	 * @param inputNeuronCount
	 */
	public GNGNode(Cluster cluster) {
		super(cluster);
	}
}
