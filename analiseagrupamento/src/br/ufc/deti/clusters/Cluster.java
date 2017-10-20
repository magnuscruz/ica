/*
 * Represents an abstraction for a cluster of data points in two dimensional space
 *
 * Manas Somaiya
 * Computer and Information Science and Engineering
 * University of Florida
 *
 * Created: October 29, 2003
 * Last updated: October 30, 2003
 *
 */
package br.ufc.deti.clusters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.ufc.deti.clusters.validation.Validation;


/**
 * Represents an abstraction for a cluster of data points in two dimensional
 * space
 * 
 * @author Manas Somaiya mhs@cise.ufl.edu
 */
public class Cluster implements Comparator {

	private int inputDimension;

	private double distance2;

	/** Cluster Number */
	private int clusterNumber;

	/** Mean data point of this cluster */
	private Datum centroide;

	/** Mean data point of this cluster */
	private List<Datum> points = new ArrayList<Datum>();

	/** Mean data point of this cluster */
	private List<Datum> pointsByEpoc = new ArrayList<Datum>();

	/**
	 * Main method -- to test the cluster class
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		Cluster c1 = new Cluster(2);
		c1.setClusterNumber(1);
		c1.setCentroide(new Datum(3, 4));
		System.out.println(c1.getCentroide());

	} // end of main()

	/**
	 * Default constructor
	 */
	public Cluster() {
	}
	// constructor with argument
	/**
	 * @param n tamanho do vetor de pesos
	 */
	public Cluster(int n) {
		this.inputDimension = n;
		this.centroide = new Datum(n);
		this.centroide.setAttributes(new double[inputDimension]);
		distance2 = 0;
	}

	/**
	 * Allocate and initialize afferent weight vector.
	 * This function MUST be called at least once in the
	 * beginning to allocate and setup the afferent weights
	 * @param size
	 * @param x
	 * @param y
	 */
	public void initAttributes(int n, int size) {
		points = new ArrayList<Datum>();
		setInputDimension(n);
		double[] attributes = new double[n];
		getCentroide().setAttributes(attributes);
		for (int i = 0; i < getInputDimension(); i++) {
			attributes[i] = ((double)size) * Math.random();
		}
	}

	/**
	 * Sets the mean data point of this cluster
	 * 
	 * @param _centroide
	 *            the new mean data point for this cluster
	 */
	public void setCentroide(Datum _centroide) {

		this.centroide = _centroide;

	} // end of setMean()

	/**
	 * Returns the mean data point of this cluster
	 * 
	 * @return the mean data point of this cluster
	 */
	public Datum getCentroide() {

		return this.centroide;

	} // end of getMean()

	/**
	 * Returns the cluster number of this cluster
	 * 
	 * @return the cluster number of this cluster
	 */
	public int getClusterNumber() {

		return this.clusterNumber;

	} // end of getClusterNumber()


	/**
	 * @return the Attributes
	 */
	public double[] getAttributes() {
		return getCentroide().getAttributes();
	}

	/**
	 * @param Attributes the Attributes to set
	 */
	public void setAttributes(double[] attributes) {
		getCentroide().setAttributes(attributes);
	}

	/**
	 * Clear the weights.
	 */
	public void clearWeights() {
		double[] weights = getCentroide().getAttributes();
		for (int i = 0; i < weights.length; ++i) {
			weights[i] = 0.0;
		}
	}

	/**
	 * Calculate euclidian distance and set this.distance2.
	 * @return that value
	 */
	public double euclidianDistance(double input[]) {
		double[] attributes = getCentroide().getAttributes();
		this.distance2 = Validation.distance2(attributes, input);
		return Math.sqrt(distance2);
	}


	/**
	 * Modify Attributes (using delta rule)
	 * @param delta
	 * @return
	 */
	public void euclidianRule(double rate_atual, double input[]) {
		double[] weights = new double[getInputDimension()]; 
		double[] weightsOld = getCentroide().getAttributes();
		
		for (int i = 0; i < getInputDimension(); i++) {
			weights[i] = weightsOld[i] + rate_atual * (input[i] - weightsOld[i]);
		}
		getCentroide().setAttributes(weights);
	}

	public void addPoint(Datum dp) {
		points.add(dp);
	}

	public void addPointByEpoc(Datum dp) {
		pointsByEpoc.add(dp);
	}

	/**
	 * @return the points
	 */
	public List<Datum> getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(List<Datum> points) {
		this.points = points;
	}

	public double getErrorMedio() {
		double[] weights = centroide.getAttributes();
		int size = pointsByEpoc.size();
		double errors = 0.0;
		for (Datum element : pointsByEpoc) {
			double[] array = element.getAttributes();
			double distance = Validation.euclidianDistance(weights, array);
			errors += distance;
		}
		return errors/size;
	}

	public double getError() {
		return Math.sqrt(distance2);
	}

	/**
	 * @return the inputDimension
	 */
	public int getInputDimension() {
		return inputDimension;
	}

	/**
	 * @param inputDimension the inputDimension to set
	 */
	public void setInputDimension(int inputDimension) {
		this.inputDimension = inputDimension;
	}

	/**
	 * @return the distance2
	 */
	public double getDistance2() {
		return distance2;
	}

	/**
	 * @param distance2 the distance2 to set
	 */
	public void setDistance2(double distance2) {
		this.distance2 = distance2;
	}

	/**
	 * @param clusterNumber the clusterNumber to set
	 */
	public void setClusterNumber(int clusterNumber) {
		this.clusterNumber = clusterNumber;
	}

	/**
	 * Method for SORT (Interface Comparator)
	 * @param o1 first object
	 * @param o2 second object
	 * @return -1 or +1  as the first argument is less than or greater than the second.
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Object o1, Object o2){
		Cluster n1 = (Cluster)o1;
		Cluster n2 = (Cluster)o2;
		if (n1.getDistance2() < n2.getDistance2()) return -1;
		else return 1;
	}

	/**
	 * @return the pointsByEpoc
	 */
	public List<Datum> getPointsByEpoc() {
		return pointsByEpoc;
	}

	/**
	 * @param pointsByEpoc the pointsByEpoc to set
	 */
	public void setPointsByEpoc(List<Datum> pointsByEpoc) {
		this.pointsByEpoc = pointsByEpoc;
	}

	@Override
	public String toString() {
		return (this.getClusterNumber()+1)+" - "+this.getCentroide() + ", Num. wins = "+ points.size();
	}
} // end of class
