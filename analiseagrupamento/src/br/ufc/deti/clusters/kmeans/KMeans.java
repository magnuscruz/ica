/*
 * Implements the k-means algorithm
 *
 * Manas Somaiya
 * Computer and Information Science and Engineering
 * University of Florida
 *
 * Created: October 29, 2003
 * Last updated: October 30, 2003
 *
 */

package br.ufc.deti.clusters.kmeans;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.AbstractClustering;
import br.ufc.deti.clusters.Cluster;
import br.ufc.deti.clusters.Datum;
import br.ufc.deti.clusters.Reportable;
import br.ufc.deti.clusters.charting.XYSeriesChart;

/**
 * Implements the k-means algorithm
 *
 * @author Manas Somaiya mhs@cise.ufl.edu
 */
public class KMeans extends AbstractClustering {

	/**
	 * Returns a new instance of kMeans algorithm
	 * 
	 * @param k
	 *            number of clusters
	 * @param defaultListModel
	 *            name of the file containing input data
	 * @param reportable
	 *            Reportable
	 */
	public KMeans(int inputCount, int outputCount, Reportable owner,
			DefaultListModel dataListModel) {
		super(inputCount, outputCount, owner, dataListModel);
		name = "Kmeans";
	} // end of kMeans()

	/**
	 * Assigns a data point to one of the k clusters based on its distance from
	 * the means of the clusters
	 * 
	 * @param dp
	 *            data point to be assigned
	 * @return
	 */
	private Cluster findWinner(Datum dp) {
		calculateActivity(dp.getAttributes());
		// Order by distance2
		sortNodes();
		Cluster winner = outputClusters.get(0);
		assignToCluster(dp, winner);
		return winner;
	} // end of assignToCluster

	/**
	 * Updates the means of all k clusters
	 */
	private void updateMeans() {

		int sizeAttr = this.outputClusters.get(0).getAttributes().length;

		int sizeOutput = this.outputClusters.size();
		double[][] sum = new double[sizeOutput][sizeAttr];
		int[] size = new int[sizeOutput];
		Datum[] pastMeans = new Datum[sizeOutput];

		for (Cluster cluster : outputClusters) {
			int clusterNumber = cluster.getClusterNumber();
			for (int j = 0; j < sizeAttr; j++) {
				sum[clusterNumber][j]=0;
			}
			size[clusterNumber] = 0;
			pastMeans[clusterNumber] = cluster.getCentroide();

		}

		for (Datum dp : getInputList()) {
			int currentCluster = dp.getClusterNumber();

			if (currentCluster >= 0) {
				for (int j = 0; j < sum[currentCluster].length; j++) {
					sum[currentCluster][j] += dp.getAttributes()[j];
				}
				size[currentCluster]++;
			}
		}

		for (int i = 0; i < sizeOutput; i++) {
			if (size[i] != 0) {

				for (int j = 0; j < sum[i].length; j++) {
					sum[i][j] /= size[i];	
				}
				Datum newCentroide = new Datum(sum[i]);
				newCentroide.assignToCluster(i);
				this.getCluster(i).setCentroide(newCentroide);
			}
		}

	} // end of updateMeans()

	/**
	 * @param epocas
	 */
	public void setEpocas(int epocas) {
		this.epocas = epocas;
	}

	public void setChart(XYSeriesChart chart) {
		this.chart = chart;
	}

	public double train(Datum input) {
	    this.time++;
		Cluster errorMin = this.findWinner(input);
		if (!testEpoc)
			updateMeans();
		return errorMin.getError();
	}

	@Override
	protected void initializeVariables() {
		// TODO Auto-generated method stub
	}
}

