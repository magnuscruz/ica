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
package br.ufc.deti.clusters.neuralgas;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.AbstractNetwork;
import br.ufc.deti.clusters.Cluster;
import br.ufc.deti.clusters.Datum;
import br.ufc.deti.clusters.Network;
import br.ufc.deti.clusters.Reportable;


/**
 * NeuralGas class represents a Neural Gas network.
 *  
  * @author Laurent BOUGRAIN, Mohammed ATTIK, Eric DONDELINGER, Marie TONNELIER
 * @version $Revision: 1.4 $
 */

public class FuzzyCL extends AbstractNetwork implements Network {

	/** Update coefficient decreases modification importance according to the rank of the node */
	public float updateCoef            = 2;

	/* Methods */

	/**
	 * Default Constructor
	 * @param inputCount
	 * @param outputCount
	 * @param owner
	 * @param dataListModel
	 */
	public FuzzyCL(int inputCount, int outputCount, Reportable owner, DefaultListModel dataListModel) {
		super(inputCount, outputCount, owner, dataListModel);
		name = "FuzzyCL";
	}

	/**
	 * @see br.ufc.deti.clusters.AbstractNetwork#learn()
	 */
	public void learn() throws RuntimeException {
		for (k = getMinK(); k <= getMaxK(); k++) {
			runTrain();
		}	    
	}

	@Override
	public void updateWeights(Datum input) {
		// Update prototypes switch his rank
		int i = 0;
		for (Cluster element : outputClusters) {
			double coefToApply = calculateCoeficiente(i);
			element.euclidianRule(coefToApply, input.getAttributes());
			i++;
		}
	}

	/**
	 * @see br.ufc.deti.clusters.AbstractNetwork#calculateCoeficiente(int)
	 */
	public double calculateCoeficiente(int i) {
		double hLambda = (double)Math.exp(-i/lambda_atual);
		return rate_atual*hLambda;
	}

}
