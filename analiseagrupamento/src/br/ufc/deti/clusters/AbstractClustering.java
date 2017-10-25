package br.ufc.deti.clusters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;

import br.ufc.deti.clusters.charting.XYSeriesChart;
import br.ufc.deti.clusters.validation.Validation;

public abstract class AbstractClustering implements Clustering {

	/** Number of clusters */
	protected static final int MIN_K = 2;

	/** Number of clusters */
	private int maxK;

	/** Number of clusters */
	private int minK = MIN_K;

	protected int k = 2;

	protected double time = 0;

	protected boolean test;
	protected boolean testEpoc;

	/**
	 * Accumulate distance.
	 */
	protected double accErrorMin;

	/**
	 * Number of attributes input
	 */
	protected int attribsNumInput = 0;

	/**
	 * Number of output neurons
	 */
	protected int outputClusterNumber = 0;

	/**
	 * The owner object, to report to.
	 */
	protected Reportable ownerApp;

	/** Name of the input data */
	private DefaultListModel trainListModel;

	private Datum[] trainList;
	private Datum[] testList;

	// local variables
	protected int t_max;

	protected List<Cluster> outputClusters;

	protected Map<Integer, Cluster> outputClustersMap;

	protected XYSeriesChart chart;

	protected int epocas;

	protected String name;

	private int imageTam = 256;

	public AbstractClustering(int inputCount, int outputCount,
			Reportable owner, DefaultListModel dataListModel) {
		this.attribsNumInput = inputCount;
		this.outputClusterNumber = outputCount;
		this.trainListModel = dataListModel;
		this.trainList = new Datum[dataListModel.getSize()];
		this.testList = new Datum[dataListModel.getSize()];
		int i = 0;
		for (Object element : dataListModel.toArray()) {
			this.testList[i] = this.trainList[i] = (Datum) element;
			i++;
		}
		this.maxK= outputCount;
		this.ownerApp = owner;
		this.time = 0;
	}

	/**
	 * @see br.ufc.deti.clusters.AbstractNetwork#learn()
	 */
	public void learn() throws RuntimeException {
		for (k = getMinK(); k <= getMaxK(); k++) {
			runTrain();
		}	    
	}
	
	/**
	 * Initialize Clusters
	 */
	protected void initClusters() {
		// Randomize inputs
		sortRandomInputs();
		this.outputClusters = new ArrayList<Cluster>(this.k);
		this.outputClustersMap = new HashMap<Integer, Cluster>();
		for (int i = 0; i < this.k; i++) {
			Cluster randomInput = getRandomInput(i);
			outputClusters.add(randomInput);
			outputClustersMap.put(i, randomInput);
		}
	}

	/**
	 * To initialize variables
	 */
	protected void initializeVariables() {
		this.time = 0;
		this.accErrorMin = 0.0;
	}

	/**
	 * It runned train
	 */
	protected void runTrain() {
		initializeVariables();
		initClusters();
		clearPoints();
		for (int epoca = 0; epoca < epocas + (test?1:0); epoca++) {
			testEpoc = false;
			if (epoca == epocas) {
				testEpoc = true;
				DefaultListModel testListModel =
					((ClusteringApplication)ownerApp).load_actionPerformed(null);
				this.testList = new Datum[testListModel.getSize()];
				int i = 0;
				for (Object element : testListModel.toArray()) {
					this.testList[i] = (Datum) element;
					i++;
				}
			}
			clearPointsByEpoc();
			accErrorMin = 0.0;
			// loop through all training sets to determine correction
			for (Datum datum : getInputList()) {
				accErrorMin += train(datum);
			}
			updateForEpoc(getInputList().length, epoca, accErrorMin);

			/*try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}*/
		}
		calculateIndices(k);
		saveNumWinsForPrototipo();
	}

	/**
	 * Assigns a data point to one of the k clusters based on its distance from
	 * the means of the clusters
	 *
	 * @param dp
	 * @param winner
	 */
	protected void assignToCluster(Datum dp, Cluster winner) {
		dp.assignToCluster(winner.getClusterNumber());
		// Atribui ao prototipo vencedor a entrada
		winner.addPoint(dp);
		winner.addPointByEpoc(dp);
	}

	/**
	 * Return cluster value
	 * 
	 * @param key
	 * @return
	 */
	public Cluster getCluster(int key) {
		return outputClustersMap.get(key);
	}

	/**
	 * Return cluster value
	 * 
	 * @param key
	 * @return
	 */
	public Cluster getClusterByOrder(int key) {
		return outputClusters.get(key);
	}

	/**
	 * Calculate activity for the same input, using Euclidian Distance
	 * 
	 * @param input
	 */
	protected void calculateActivity(double[] input) {
		for (Cluster element : outputClusters) {
			element.euclidianDistance(input);
		}
	}

	/**
	 * Sort the node by value
	 */
	public void sortNodes() {
		Collections.sort(this.outputClusters, this.outputClusters
				.get(0));
	}

	/**
	 * To sort randomize inputs.
	 */
	protected void sortRandomInputs() {
		List<Datum> asList = Arrays.asList(trainList);
		Collections.sort(asList, RandomComparator.getInstance());
		trainList = (Datum[]) asList.toArray();
	}

	/**
	 * Show input elements
	 */
	protected void showInputData() {
		Enumeration<?> elementData = trainListModel.elements();
		for (; elementData.hasMoreElements();) {
			Datum element = (Datum) elementData.nextElement();
			double x = element.getAttributeX();
			double y = element.getAttributeY();
			chart.add(XYSeriesChart.DATA_CLUSTER + 1, x, y);
		}
	}

	/**
	 * Getter random value from inputs
	 * 
	 * @param element
	 * @return
	 */
	protected Cluster getRandomInput(int i) {
		int j = (int) (i + trainList.length * Math.random());
		if (j >= trainList.length) {
			j -= trainList.length;
		}
		Datum datum = (Datum) trainList[j];
		int sizeInput = datum.getAttributes().length;
		Cluster element = null;
		try {
			element = (Cluster) getProtoType().newInstance();
			element.setInputDimension(sizeInput);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Datum datumNew = new Datum(sizeInput);
		datumNew.setAttributes(datum.getAttributes());
		datumNew.setIndexX(datum.getIndexX());
		datumNew.setIndexY(datum.getIndexY());
		element.setClusterNumber(i);
		element.setCentroide(datumNew);
		return element;
	}

	/**
	 * To calculate and update indices graphs
	 * 
	 * @param k
	 *            number of clusters
	 */
	protected void calculateIndices(int k) {
		double indexDaviesBouldin = Validation
				.calculateDaviesBouldin(outputClusters.toArray());
		ownerApp.updateDaviesBouldin(k, indexDaviesBouldin);
		if (k == getMaxK())
			ownerApp.saveGraph(Reportable.DB_GRAPH, k);

		double indexDunn = Validation.calculateDunn(outputClusters.toArray());
		ownerApp.updateDunn(k, indexDunn);
		if (k == getMaxK())
			ownerApp.saveGraph(Reportable.DUNN_GRAPH, k);

		double indexError = calculateErrorQuant(getInputList().length, accErrorMin);
		ownerApp.updateErrorIndexGraph(k, indexError);
		if (k == getMaxK() && testEpoc)
			ownerApp.saveGraph(Reportable.ERROR_INDEX_GRAPH, k);

		ownerApp.saveGraph(Reportable.ERROR_GRAPH, k);
	}

	/**
	 * Update data in the graph
	 */
	protected void updateDataGraph() {
		chart.clear();
		// updateGraph
		for (Cluster cluster : outputClusters) {
			Datum wptr = cluster.getCentroide();
			ownerApp.updateData(XYSeriesChart.PROTOTIPOS, wptr.getAttributeX(),
					wptr.getAttributeY());
			String label = XYSeriesChart.DATA_CLUSTER
			+ (cluster.getClusterNumber() + 1);
			List<Datum> points = cluster.getPointsByEpoc();
			for (Datum datum : points) {
				ownerApp.updateData(label, datum
						.getAttributeX(), datum.getAttributeY());
			}
		}
		if ((time % (trainList.length * epocas)) == 0 || testEpoc)
			ownerApp.saveGraph(Reportable.DATA_GRAPH, outputClusters.size());
	}

	/**
	 * Update quantization error in the graph
	 * 
	 * @param sizeDataTrain
	 * @param epocaAtual
	 * @param accDistMin
	 */
	protected void updateErrorGraph(int sizeDataTrain, int epocaAtual,
			double accDistMin) {
		double erroQuant = calculateErrorQuant(sizeDataTrain, accDistMin);
		ownerApp.updateErrorGraph(epocaAtual, erroQuant);
	}

	/**
	 * @param sizeDataTrain
	 * @param accDistMin
	 * @return
	 */
	private double calculateErrorQuant(int sizeDataTrain, double accDistMin) {
		double erroQuant = 0.0;
		erroQuant = accDistMin / sizeDataTrain;
		return erroQuant;
	}

	/**
	 * Update graphics and inputs.
	 * 
	 * @param sizeDataTrain
	 * @param epocaAtual
	 * @param accDistMin
	 */
	protected void updateForEpoc(int sizeDataTrain, int epocaAtual,
			double accDistMin) {
		// Update quantization error in the graph
		if (!testEpoc) {
			//it don't executed in test
			updateErrorGraph(sizeDataTrain, epocaAtual, accDistMin);
		} else {
			try {
				String path = "./images/" + name + "/";

				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
				int numOutput = outputClusters.size();
				String fileName = "Image_"+name+numOutput+"K";
				String pathFile = path + fileName;
				file = new File(pathFile + ".dat");
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				for (Datum element : testList) {
					int clusterNumber = element.getClusterNumber();
					Cluster cluster = getCluster(clusterNumber);
					String toStringCsv = cluster.getCentroide().toStringCsv() + "\n";
					out.write(toStringCsv.getBytes());
				}
				
				out.close();

				// To calculate PSNR
				double psnr = toCalculatePSNR(sizeDataTrain, accDistMin);
				// To calculate Bit Rate
				double bitRate = toCalculateBitRate(numOutput);
				// To calculate AIK
				double aik = toCalculateAIK(sizeDataTrain, accDistMin, numOutput);

				file = new File(pathFile+ "PSNR_bitRate_aik" + ".dat");
				file.createNewFile();
				out = new FileOutputStream(file);
				out.write((psnr + " " + bitRate + " " + aik).getBytes());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// Update data in the graph
		updateDataGraph();

		// Randomize inputs
		sortRandomInputs();
	}

	private double toCalculateBitRate(int numOutput) {
		// Potencia de 2
		double T = 0;
		while (Math.pow(2, ++T) != numOutput) {}
		double bitRate = T/(trainList[0].getAttributes().length);
		return bitRate;
	}

	private double toCalculateAIK(int sizeDataTrain, double accDistMin, int numOutput) {
		double aik = trainList.length * Math.log(calculateErrorQuant(sizeDataTrain, accDistMin))
		+ numOutput * (trainList[0].getAttributes().length);
		return aik;
	}

	private double toCalculatePSNR(int sizeDataTrain, double accDistMin) {
		double erroQuant = calculateErrorQuant(sizeDataTrain, accDistMin);
		double psnr = 10 * Math.log10((Math.pow(imageTam - 1, 2))/erroQuant);
		return psnr;
	}

	/**
	 * To save number of wins for prototype
	 */
	protected void saveNumWinsForPrototipo() {
		for (Cluster cluster : outputClusters) {
			ownerApp.updateBarGraph(cluster.getClusterNumber() + 1,
					cluster.getPoints().size());
		}
		ownerApp.saveGraph(Reportable.BAR_GRAPH, outputClusters.size());
	}

	/**
	 * Present an input pattern and get the winning neuron.
	 * 
	 * @param input
	 *            input pattern
	 * @param normfac
	 *            the result
	 * @param synth
	 *            synthetic last input
	 * @return The winning neuron number.
	 */
	public Cluster winner(double input[]) {
		Cluster winner = null;
		double distance = 999999;
		for (Cluster c : outputClusters) {
			double d = Validation.euclidianDistance(c.getAttributes(), input);
			if (d < distance) {
				distance = d;
				winner = c;
			}
		}
		return winner;
	}

	/**
	 * To clear Points of Clusters
	 */
	protected void clearPoints() {
		for (Cluster cluster : outputClusters) {
			cluster.setPoints(new ArrayList<Datum>());
		}
		ownerApp.clearGraph(Reportable.ERROR_GRAPH);
		ownerApp.clearGraph(Reportable.DATA_GRAPH);
	}


    /**
	 * @see br.ufc.deti.clusters.Clustering#test()
	 */
	public void test() throws RuntimeException {
		test = true;
		learn();
	}

	/**
	 * To clear Points of Clusters by epoc
	 */
	protected void clearPointsByEpoc() {
		for (Cluster cluster : outputClusters) {
			cluster.setPointsByEpoc(new ArrayList<Datum>());
		}
	}

	/**
	 * @param chart
	 */
	public void setChart(XYSeriesChart chart) {
		this.chart = chart;
	}

	public int getT_max() {
		return t_max;
	}

	public void setEpocas(int epocas) {
		this.epocas = epocas;
		t_max = epocas * trainListModel.getSize();
	}

	/**
	 * @return the minK
	 */
	public int getMinK() {
		return minK;
	}

	/**
	 * @param minK
	 *            the minK to set
	 */
	public void setMinK(int minK) {
		this.minK = minK;
	}

	/**
	 * @return the maxK
	 */
	public int getMaxK() {
		return maxK;
	}

	/**
	 * @param maxK
	 *            the maxK to set
	 */
	public void setMaxK(int maxK) {
		this.maxK = maxK;
	}

	/**
	 * @return the trainListModel
	 */
	public DefaultListModel getTrainListModel() {
		return trainListModel;
	}

	/**
	 * @param trainListModel
	 *            the trainListModel to set
	 */
	public void setTrainListModel(DefaultListModel trainListModel) {
		this.trainListModel = trainListModel;
	}

	/**
	 * @return the trainList
	 */
	public Datum[] getInputList() {
		return (testEpoc ? testList : trainList);
	}

	protected Class getProtoType() {
		return Cluster.class;
	}

	public int getImageTam() {
		return imageTam;
	}

	public void setImageTam(int imageTam) {
		this.imageTam = imageTam;
	}
}
