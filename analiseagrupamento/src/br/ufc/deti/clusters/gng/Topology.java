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

import br.ufc.deti.clusters.Cluster;
import br.ufc.deti.clusters.Neuron;

/**
 * Bond class manages Bonds between Nodes (ex : GNG Network).
 *
 * @author Eric DONDELINGER
 * @version $Revision: 1.1 $
 */
public class Topology implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2088141351789518397L;
	/* Fields */
	
	/** List of bonds */
	public ArrayList<Bond> bonds = new ArrayList<Bond>();
	
	/* Methods */
	
	/**
	 * Constructs an empty topology.
	 */
	public Topology(){
	}
	
	/**
	 * Overriding this method allows you to choose the type of bond
	 * you want to use. We use here the base class Bond.
	 * @return bond created
	 */
	public Bond createBond() {
		return new Bond();
	}
	
	/**
	 * Returns the link between two units. Can be used to check
	 * if units are bound since it returns null otherwise.
	 * @param a origin of the bond
	 * @param b destination of the bond
	 * @return bond between those 2 units or null if it doesn't exist
	 */
	public final Bond getBond(Cluster a, Cluster b) {
		Bond existing = null;
		for(int i=0; i < this.bonds.size(); i++) {
			Bond bond = (Bond) this.bonds.get(i);
			if(bond.a == a || bond.b == a){
				if (bond.a == a || bond.b == b){
					existing = bond;
				}
			}
		}
		return existing;
	}
	
	/**
	 * Binds two units using the function createBond()
	 * @param a origin of the bond (generally the winner node)
	 * @param b destination of the bond (generally the second node)
	 * @return bond between those 2 units or a new one if it doesn't exist
	 */
	public final Bond bind(Cluster a, Cluster b) {
		Bond bond = getBond(a, b);
		if(bond == null) {
			bond = createBond();
			bond.a = a;
			bond.b = b;
			bonds.add(bond);
		}
		bond.age = 0;
		return bond;
	}
	
	/**
	 * Add 1 to age of each Bonds
	 */
	public void increaseAgeOfBonds(){
		for (int i = 0; i < this.bonds.size(); i++){
			Bond bond = (Bond)this.bonds.get(i);
			bond.age += 1;
		}
	}
	
	/**
	 * Deletes bonds too old.
	 * @param maxAge maximum age for a bond
	 */
	public void deleteOldBonds(int maxAge){
		for (int i = this.bonds.size() - 1; i >= 0 ; i--){
			Bond bond = (Bond)this.bonds.get(i);
			if (bond.age > maxAge){
				this.remove(bond);
			}
		}
	}
	
	/**
	 * Test if a Node has bounds
	 * @param node Node
	 * @return true if has Bounds, else false
	 */
	public boolean isBounded(Neuron node){
		boolean response = false;
		for (int i = 0; i < this.bonds.size(); i++){
			Bond bond = (Bond)this.bonds.get(i);
			if ((bond.a == node)||(bond.b == node)){
				response = true;
				break;
			}
		}
		return response;
	}
	
	/**
	 * Find the max accumulator Neighbor (For GNG algorithm)
	 * @param node with max accuulator
	 * @return MaxAccumulatorNeighbor
	 */
	public GNGNode findMaxAccumulatorNeighbor(GNGNode node){
		GNGNode max = null;
		double maxValue = 0;
		for (int i = 0; i < this.bonds.size(); i++){
			Bond bond = (Bond)this.bonds.get(i);
			if ((GNGNode)bond.a == node){
				if (((GNGNode)bond.b).accumulator > maxValue){
					max = (GNGNode)bond.b;
					maxValue = max.accumulator;
				}
			}
			if (bond.b == node){
				if (((GNGNode)bond.a).accumulator > maxValue){
					max = (GNGNode)bond.a;
					maxValue = max.accumulator;
				}
			}
		}
		return max;
	}
	
	/**
	 * Removes the bond between a and b and destroys it.
	 * @param a origin of the bond to remove
	 * @param b destination of the bond to remove
	 */
	public final void free(Neuron a, Neuron b) {
		Bond bond = getBond(a, b);
		if(bond != null) {
			remove(bond);
			bond = null; // for garbage collector
		}
	}
	
	/**
	 * Removes given bond and destroys it.
	 * @param bond bond to remove
	 */
	public final void remove(Bond bond) {
		bonds.remove(bonds.indexOf(bond));
		bond = null; // for garbage collector
	}
}
