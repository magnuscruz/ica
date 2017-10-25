package br.ufc.deti.clusters.validation;

import java.util.List;

import br.ufc.deti.clusters.Cluster;
import br.ufc.deti.clusters.Datum;


public final class Validation {

	/**
	 * To calculate Dunn Index
	 * @param clusters
	 * @return
	 */
	public static double calculateDunn(Object[] clusters) {
		double minDiss = 999999;
		double maxDist = 0.0;
		double dist = 0.0;
		for (int i = 0; i < clusters.length; i++) {
			List<Datum> points1 = ((Cluster)clusters[i]).getPointsByEpoc();
			Datum datum1 = null;
			//Max distance intra-cluster
			for (Datum datum : points1) {
				if (datum1 == null) {
					datum1 = datum;
					continue;
				}
				//System.out.println(""+datum1+" - "+datum);
				dist = distance2(datum1.getAttributes(),
						datum.getAttributes());
				if (dist > maxDist) {
					maxDist = dist;
				}
			}
			//Min distance inter-clusters
			for (int j = 0; j < clusters.length; j++) {
				if (j != i) {
					List<Datum> points2 = ((Cluster)clusters[j]).getPointsByEpoc();
					for (Datum datum : points1) {
						for (Datum datum2 : points2) {
							dist = distance2(datum2.getAttributes(),
									datum.getAttributes());
							if (dist < minDiss) {
								minDiss = dist;
							}							
						}
					}
				}
			}
		}
		return minDiss/maxDist;
	}

	/**
	 * To calculate Dunn Index
	 * @param clusters
	 * @return
	 */
	public static double calculateDunn(Cluster[][] clusters) {
		double minDiss = 999999;
		double maxDist = 0.0;
		double dist = 0.0;
		for (int i = 0; i < clusters.length; i++) {
			for (int j = 0; j < clusters[0].length; j++) {
				List<Datum> points1 = clusters[i][j].getPointsByEpoc();
				Datum datum1 = null;
				//Max distance intra-cluster
				for (Datum datum : points1) {
					if (datum1 == null) {
						datum1 = datum;
						continue;
					}
					dist = distance2(datum1.getAttributes(),
							datum.getAttributes());
					if (dist > maxDist) {
						maxDist = dist;
					}
				}
				//Min distance inter-clusters
				for (int m = 0; m < clusters.length; m++) {
					for (int n = 0; n < clusters[0].length; n++) {
						if (m != i && n != j) {
							List<Datum> points2 = clusters[m][n].getPointsByEpoc();
							for (Datum datum : points1) {
								for (Datum datum2 : points2) {
									dist = distance2(datum2.getAttributes(),
											datum.getAttributes());
									if (dist < minDiss) {
										minDiss = dist;
									}							
								}
							}
						}
					}
				}
			}
		}
		return minDiss/maxDist;
	}

	
	/**
	 * To calculate Davies Bouldin Index
	 * @param clusters
	 * @return
	 */
	public static double calculateDaviesBouldin(Object[] clusters) {
		double daviesBouldin = 0.0;
		for (int i = 0; i < clusters.length; i++) {
			double maxR = 0.0;
			for (int j = 0; j < clusters.length; j++) {
				if (j != i) {
					Cluster neuron1 = (Cluster)clusters[i];
					Cluster neuron2 = (Cluster)clusters[j];
					maxR = calculateMaxSimilar(maxR, neuron1, neuron2);
				}
			}
			daviesBouldin += maxR;
		}
		return daviesBouldin/clusters.length;
	}

	/**
	 * To calculate Davies Bouldin Index
	 * @param clusters
	 * @return
	 */
	public static double calculateDaviesBouldin(Cluster[][] clusters) {
		double daviesBouldin = 0.0;
		for (int i = 0; i < clusters.length; i++) {
			for (int j = 0; j < clusters[0].length; j++) {
				double maxR = 0.0;
				for (int m = 0; m < clusters.length; m++) {
					for (int n = 0; n < clusters[0].length; n++) {
						if (m != i && n != j) {
							Cluster neuron1 = clusters[i][j];
							Cluster neuron2 = clusters[m][n];
							maxR = calculateMaxSimilar(maxR, neuron1, neuron2);
						}
					}
				}
				daviesBouldin += maxR;
			}
		}
		return daviesBouldin/(clusters.length * clusters.length);
	}

	/**
	 * calculate euclidian distance
	 */
	public static double euclidianDistance(double array1[], double array2[]) {
		return Math.sqrt(distance2(array1, array2));
	}

	/**
	 * @param maxR
	 * @param neuron1
	 * @param neuron2
	 * @return
	 */
	private static double calculateMaxSimilar(double maxR, Cluster neuron1, Cluster neuron2) {
		double[] mean1 = neuron1.getAttributes();
		double[] mean2 = neuron2.getAttributes();
		double distance = euclidianDistance(
				mean1, mean2);
		double errorMedio1 = neuron1.getErrorMedio();
		double errorMedio2 = neuron2.getErrorMedio();
		double R = (errorMedio1 + errorMedio2)/distance;
		if (maxR < R) {
			maxR = R;
		}
		return maxR;
	}

	// calculate distance2
	public static double distance2(double array1[], double array2[]) {
		double distance2 = (double) 0.0;
		for (int i = 0; i < array1.length; ++i) {
			distance2 += (array1[i] - array2[i]) * (array1[i] - array2[i]);
		}
		return distance2;
	}

}
