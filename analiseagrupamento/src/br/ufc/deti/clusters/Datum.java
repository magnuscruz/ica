package br.ufc.deti.clusters;

import java.util.ArrayList;

import br.ufc.deti.clusters.validation.Validation;


/*
 * Represents an abstraction for a data point in two dimensional space
 *
 * Manas Somaiya
 * Computer and Information Science and Engineering
 * University of Florida
 *
 * Created: October 29, 2003
 * Last updated: October 30, 2003
 *
 */

/**
 * Represents an abstraction for a data point in two dimensional space
 * 
 * @author Manas Somaiya mhs@cise.ufl.edu
 */
public class Datum {

	/** Attributes */
	private double attributes[];

	/** Assigned cluster */
	private int clusterNumber = -1;

	private int indexX = 0;
	private int indexY = 1;

	/**
	 * Main method -- to test the kMeansPoint class
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		Datum dp1 = new Datum(-3, -4);
		Datum dp2 = new Datum(0, 4);
		System.out.println(Validation.euclidianDistance(
				dp1.getAttributes(), dp2.getAttributes()));
		System.out.println(dp1.getAttributeX());
		System.out.println(dp2.getAttributeY());
		dp1.assignToCluster(7);
		System.out.println(dp1.getClusterNumber());
		dp1.assignToCluster(17);
		System.out.println(dp1.getClusterNumber());
		System.out.println(dp2.getClusterNumber());
		System.out.println(dp1);

	} // end of main()

	/**
	 * Creates a new instance of data point
	 * 
	 * @param _x
	 *            value in dimension x
	 * @param _y
	 *            value in dimension y
	 */
	public Datum(double _x, double _y) {
		this.attributes = new double[2];
		this.attributes[0] = _x;
		this.attributes[1] = _y;
		this.clusterNumber = -1;
	} // end of kMeansPoint()

    /**
	 * Contrutor default
	 */
	public Datum() {
		this.attributes = new double[2];
		this.clusterNumber = -1;
	}

    /**
     * @param tam tamanho do array de atributos
     */
    public Datum(int tam) {
    	this.attributes = new double[tam];
		this.clusterNumber = -1;
    }

	public Datum(double[] input) {
		this.attributes = input;
		this.clusterNumber = -1;
	}

	/**
	 * Assigns the data point to a cluster
	 * 
	 * @param _clusterNumber
	 *            the cluster to which this data point is to be assigned
	 */
	public void assignToCluster(int _clusterNumber) {

		this.clusterNumber = _clusterNumber;

	} // end of assignToCluster()

	/**
	 * Returns the cluster to which the data point belongs
	 * 
	 * @return the cluster number to which the data point belongs
	 */
	public int getClusterNumber() {

		return this.clusterNumber;

	} // end of getClusterNumber()

	/**
	 * @return the attributes
	 */
	public double[] getAttributes() {
		return attributes;
	}

	/**
	 * @return the attribute X
	 */
	public double getAttributeX() {
		return attributes[indexX];
	}

	/**
	 * @return the attribute Y
	 */
	public double getAttributeY() {
		return attributes[indexY];
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(double[] attributes) {
		this.attributes = attributes;
	}

	//To add attribute
	public void add(int index, double value) {
        attributes[index] = value;        
    }

    @Override
    public String toString() {
    	ArrayList<Double> array = new ArrayList<Double>();
    	for (int i = 0; i < attributes.length; i++) {
    		array.add(new Double(attributes[i]));
		}
    	
    	return array.toString();
    }
    public String toStringCsv() {
    	StringBuffer bf = new StringBuffer();
    	for (int i = 0; i < attributes.length; i++) {
    		bf.append(attributes[i] + (i != attributes.length - 1 ? " " : ""));
		}
    	
    	return bf.toString();
    }

	/**
	 * @return the indexX
	 */
	public int getIndexX() {
		return indexX;
	}

	/**
	 * @param indexX the indexX to set
	 */
	public void setIndexX(int indexX) {
		this.indexX = indexX;
	}

	/**
	 * @return the indexY
	 */
	public int getIndexY() {
		return indexY;
	}

	/**
	 * @param indexY the indexY to set
	 */
	public void setIndexY(int indexY) {
		this.indexY = indexY;
	}


} // end of class
