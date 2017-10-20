package br.ufc.deti.clusters;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import br.ufc.deti.clusters.charting.BarSeriesChart;
import br.ufc.deti.clusters.charting.LinesChart;
import br.ufc.deti.clusters.charting.UpdateBarGraph;
import br.ufc.deti.clusters.charting.UpdateData;
import br.ufc.deti.clusters.charting.UpdateIndex;
import br.ufc.deti.clusters.charting.XYSeriesChart;
import br.ufc.deti.clusters.fuzzy.FuzzyCLNetwork;
import br.ufc.deti.clusters.gng.GNG;
import br.ufc.deti.clusters.kmeans.KMeans;
import br.ufc.deti.clusters.kohonen.FSCLNetwork;
import br.ufc.deti.clusters.kohonen.RPCLNetwork;
import br.ufc.deti.clusters.kohonen.SOMNetwork;
import br.ufc.deti.clusters.kohonen.WTANetwork;
import br.ufc.deti.clusters.neuralgas.NGNetwork;

/**
 * ClusteringApplication
 *
 * @author Magnus Alencar da Cruz
 * @version 1.1
 */

public class ClusteringApplication extends JFrame implements Runnable,
		Reportable, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7684460559035679369L;

	private static final String SOM = "SOM";

	private static final String KMEANS = "K-Means";

	private static final String NEURALGAS = "NeuralGas";

	private static final String GNEURALGAS = "GrowingNeuralGas";

	private static final String WTA = "WTA";

	private static final String FSCL = "FSCL";

	private static final String RPCL = "RPCL";

	private static final String FUZZYCL = "FuzzyCL";

	/**
	 * The letters that have been defined.
	 */
	private DefaultListModel letterListModel = new DefaultListModel();

	/**
	 * The Clustering Algoritm.
	 */
	private AbstractClustering clustering;

	/**
	 * The background thread used for training.
	 */
	private Thread trainThread = null;
	
	private Thread testThread = null;

	private JTextField tf;

	private JTextField tfAtributo1;

	private JTextField tfAtributo2;

	private JTextField tfNumKMin;

	private JTextField tfNumKMax;

	private JTextField tfEpoca;

	// {{DECLARE_CONTROLS
	private JLabel JLabel1 = new JLabel();

	/**
	 * The add button.
	 */
	private JButton add = new JButton();

	/**
	 * The clear button
	 */
	private JButton clear = new JButton();

	/**
	 * The clear button
	 */
	private JButton buttonGraph = new JButton();

	/**
	 * The recognize button
	 */
	private JButton recognize = new JButton();

	private JScrollPane jsData = new JScrollPane();

	/**
	 * The letters list box
	 */
	private JList dados = new JList();

	/**
	 * SOUTH The delete button
	 */
	private JButton del = new JButton();

	/**
	 * The load button
	 */
	private JButton load = new JButton();

	/**
	 * The save button
	 */
	private JButton save = new JButton();

	/**
	 * The train button
	 */
	private JButton train = new JButton();
	
	private JButton test = new JButton();

	private JButton trainFim = new JButton();

	private JLabel jlSample = new JLabel();

	private XYSeriesChart dataChart;
	
	private BarSeriesChart barChart;

	private LinesChart errorGraph;

	private LinesChart errorIndexGraph;

	private LinesChart indexDB;

	private LinesChart indexDunn;

	private JRadioButton radionSom = new JRadioButton(SOM);

	private JRadioButton radionKmeans = new JRadioButton(KMEANS);

	private JRadioButton radionNeuralGas = new JRadioButton(NEURALGAS);

	//private JRadioButton radionGNeuralGas = new JRadioButton(GNEURALGAS);

	private JRadioButton radionWTA = new JRadioButton(WTA);

	private JRadioButton radionFSCL = new JRadioButton(FSCL);
	
	private JRadioButton radionRPCL = new JRadioButton(RPCL);
	private JRadioButton radionFUZZYCL = new JRadioButton(FUZZYCL);
	

	private ButtonGroup grupo = new ButtonGroup();

	private JTextField tfOutro = new JTextField();

	private JTextField tamImage = new JTextField();

	private JComboBox comboSeparator = new JComboBox();

	private JTextField ignoredColumn = new JTextField("0");
	private JTextField fator = new JTextField("1.0");

	private String algoritimo;

	private File selectedFile;

	private int atrib1 = 1;

	private int atrib2 = 2;

	/**
	 * The constructor.
	 */
	public ClusteringApplication() {
		setTitle("Java Neural Network");
		setSize(500, 270);
		setVisible(false);

		tf = new JTextField();
		tf.setSize(50, 30);
		tfAtributo1 = new JTextField("1");
		tfAtributo1.setSize(50, 30);
		tfAtributo2 = new JTextField("2");
		tfAtributo2.setSize(50, 30);
		tfNumKMin = new JTextField("2");
		tfNumKMin.setSize(50, 30);
		tfNumKMax = new JTextField("4");
		tfNumKMax.setSize(50, 30);
		tfEpoca = new JTextField("50");
		tfEpoca.setSize(50, 30);

		this.addWindowListener(this);
		jsData
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jsData
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsData.setOpaque(false);
		jsData.getViewport().add(dados);
		jsData.setSize(50, 50);
		jsData.setAutoscrolls(true);
		JPanel panelLeft = new JPanel();
		panelLeft.setLayout(new BorderLayout());
		JPanel panelLeft1 = new JPanel();
		panelLeft1.setLayout(new BorderLayout());
		panelLeft1.add(new Label("Data Loaded"), BorderLayout.NORTH);
		panelLeft1.add(jsData, BorderLayout.CENTER);
		panelLeft.add(panelLeft1, BorderLayout.NORTH);

		JPanel panelButton = new JPanel();
		panelLeft.add(panelButton, BorderLayout.SOUTH);
		panelButton.setLayout(new BorderLayout());

		panelButton.add(load, BorderLayout.NORTH);
		load.setText("Load");
		load.setActionCommand("Load");

		panelButton.add(train, BorderLayout.WEST);
		train.setText("Train");
		train.setActionCommand("Train");

		panelButton.add(test, BorderLayout.CENTER);
		test.setText("Test");
		test.setActionCommand("Test");

		panelButton.add(trainFim, BorderLayout.EAST);
		trainFim.setText("Stop");
		trainFim.setActionCommand("Stop");

		panelButton.add(save, BorderLayout.SOUTH);
		save.setText("Save");
		save.setActionCommand("Save");

		JPanel panelRight = new JPanel();
		panelRight.setLayout(new BorderLayout());

		JPanel panelText = new JPanel();
		panelText.setLayout(new BorderLayout());
		JPanel panelSubText = new JPanel();
		panelSubText.setLayout(new BorderLayout());
		panelText.add(panelSubText, BorderLayout.CENTER);

		JPanel panelTextTest = new JPanel();
		panelTextTest.setLayout(new BorderLayout());
		panelTextTest.add(new Label("Test datum:"), BorderLayout.WEST);
		panelTextTest.add(tf, BorderLayout.CENTER);

		panelText.add(panelTextTest, BorderLayout.NORTH);

		JPanel panelText1 = new JPanel();
		panelText1.setLayout(new BorderLayout());
		panelText1.add(new Label("Atrib 1"), BorderLayout.NORTH);
		panelText1.add(tfAtributo1, BorderLayout.SOUTH);
		panelText.add(panelText1, BorderLayout.WEST);

		JPanel panelText2 = new JPanel();
		panelText2.setLayout(new BorderLayout());
		panelText2.add(new Label("Atrib 2"), BorderLayout.NORTH);
		panelText2.add(tfAtributo2, BorderLayout.SOUTH);
		panelSubText.add(panelText2, BorderLayout.WEST);

		JPanel panelText3 = new JPanel();
		panelText3.setLayout(new BorderLayout());
		panelText3.add(new Label("Ignore Col"), BorderLayout.NORTH);
		panelText3.add(ignoredColumn, BorderLayout.SOUTH);
		panelSubText.add(panelText3, BorderLayout.CENTER);

		JPanel panelText4 = new JPanel();
		panelText4.setLayout(new BorderLayout());
		panelText4.add(new Label("K min"), BorderLayout.NORTH);
		panelText4.add(tfNumKMin, BorderLayout.SOUTH);
		panelSubText.add(panelText4, BorderLayout.EAST);

		JPanel panelText5 = new JPanel();
		panelText5.setLayout(new BorderLayout());
		panelText5.add(new Label("K max"), BorderLayout.NORTH);
		panelText5.add(tfNumKMax, BorderLayout.SOUTH);
		panelText.add(panelText5, BorderLayout.EAST);

		JPanel panelText6 = new JPanel();
		panelText6.setLayout(new BorderLayout());
		panelText6.add(new Label("Num de Epocas:"), BorderLayout.WEST);
		panelText6.add(tfEpoca, BorderLayout.CENTER);
		panelText.add(panelText6, BorderLayout.SOUTH);

		panelRight.add(panelText, BorderLayout.NORTH);

		JPanel panelSeparator = new JPanel();
		panelSeparator.setLayout(new BorderLayout());

		comboSeparator.addItem(";");
		comboSeparator.addItem(",");
		comboSeparator.addItem("Tab");
		comboSeparator.addItem("Outro");

		
		JPanel panelSeparator1 = new JPanel();
		panelSeparator1.setLayout(new BorderLayout());
		panelSeparator1.add(new Label("Separator:"), BorderLayout.WEST);
		panelSeparator1.add(comboSeparator, BorderLayout.CENTER);
		JPanel panelSeparator2 = new JPanel();
		panelSeparator2.setLayout(new BorderLayout());
		panelSeparator2.add(new Label("Outro:"), BorderLayout.WEST);
		panelSeparator2.add(tfOutro, BorderLayout.CENTER);
		panelSeparator.add(panelSeparator1, BorderLayout.NORTH);
		panelSeparator.add(panelSeparator2, BorderLayout.WEST);
		panelSeparator.add(new Label("Tam. Image:"), BorderLayout.WEST);
		panelSeparator.add(tamImage, BorderLayout.CENTER);
		tamImage.setText("1");

		JPanel panelButtonRigth = new JPanel();

		recognize.setText("Recognize");
		recognize.setActionCommand("Recognize");
		panelButtonRigth.add(recognize, BorderLayout.WEST);

		buttonGraph.setText("Graph");
		buttonGraph.setActionCommand("Graph");
		panelButtonRigth.add(buttonGraph, BorderLayout.EAST);

		panelSeparator.add(panelButtonRigth, BorderLayout.SOUTH);

		panelRight.add(panelSeparator, BorderLayout.CENTER);

		JPanel panelAction = new JPanel();
		panelAction.setLayout(new BorderLayout());

		radionSom.setActionCommand(SOM);
		grupo.add(radionSom);
		radionKmeans.setActionCommand(KMEANS);
		grupo.add(radionKmeans);
		radionNeuralGas.setActionCommand(NEURALGAS);
		grupo.add(radionNeuralGas);
		//radionGNeuralGas.setActionCommand(GNEURALGAS);
		//grupo.add(radionGNeuralGas);
		radionWTA.setActionCommand(WTA);
		grupo.add(radionWTA);
		radionFSCL.setActionCommand(FSCL);
		grupo.add(radionFSCL);
		radionRPCL.setActionCommand(RPCL);
		grupo.add(radionRPCL);
		radionFUZZYCL.setActionCommand(FUZZYCL);
		grupo.add(radionFUZZYCL);
		radionSom.setSelected(true);

		JPanel panelActionNorth = new JPanel();
		panelActionNorth.setLayout(new BorderLayout());
		panelActionNorth.add(radionWTA, BorderLayout.WEST);
		panelActionNorth.add(radionSom, BorderLayout.EAST);
		panelAction.add(panelActionNorth, BorderLayout.NORTH);

		panelAction.add(radionFSCL, BorderLayout.WEST);
		panelAction.add(fator, BorderLayout.CENTER);
		panelAction.add(radionRPCL, BorderLayout.EAST);

		JPanel panelActionSouth = new JPanel();
		panelActionSouth.setLayout(new BorderLayout());
		panelActionSouth.add(radionKmeans, BorderLayout.WEST);
		panelActionSouth.add(radionFUZZYCL, BorderLayout.CENTER);
		panelActionSouth.add(radionNeuralGas, BorderLayout.EAST);
		//panelActionSouth.add(radionGNeuralGas, BorderLayout.EAST);
		panelAction.add(panelActionSouth, BorderLayout.SOUTH);

		panelRight.add(panelAction, BorderLayout.SOUTH);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panelLeft, BorderLayout.WEST);
		getContentPane().add(panelRight, BorderLayout.EAST);

		// {{INIT_CONTROLS
		JLabel1.setText("Data");

		add.setText("Add");
		add.setActionCommand("Add");

		clear.setText("Clear");
		clear.setActionCommand("Clear");

		del.setText("Delete");
		del.setActionCommand("Delete");

		jlSample.setText("Write Sample Data");

		// }}

		// {{REGISTER_LISTENERS
		SymAction lSymAction = new SymAction();
		clear.addActionListener(lSymAction);
		buttonGraph.addActionListener(lSymAction);
		add.addActionListener(lSymAction);
		del.addActionListener(lSymAction);
		load.addActionListener(lSymAction);
		save.addActionListener(lSymAction);
		train.addActionListener(lSymAction);
		test.addActionListener(lSymAction);
		trainFim.addActionListener(lSymAction);
		recognize.addActionListener(lSymAction);
		// }}
		dados.setModel(letterListModel);
		dataChart = new XYSeriesChart("Distribuicao dos Dados", atrib1, atrib2);
		barChart = new BarSeriesChart("Distribuicao das Vitorias");
		errorGraph = new LinesChart("Quantization Error", "Epoca X Error",
				"Epoca", "Error");
		indexDB = new LinesChart("Davies-Bouldin Index", "DB Index", "K",
				"Index");
		errorIndexGraph = new LinesChart("Quantization Error Index", "Error Index", "K",
		"Index");
		indexDunn = new LinesChart("Dunn Index Family", "Dunn Index", "K",
				"Index");
		this.repaint();
		// {{INIT_MENUS
		// }}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            Args not really used.
	 */
	public static void main(String args[]) {
		(new ClusteringApplication()).setVisible(true);
	}

	// }}
	// {{DECLARE_MENUS
	// }}

	class SymAction implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent event) {
			Object object = event.getSource();
			if (object == clear)
				clear_actionPerformed(event);
			else if (object == buttonGraph)
				graph_actionPerformed(event);
			else if (object == del)
				del_actionPerformed(event);
			else if (object == load)
				load_actionPerformed(event);
			else if (object == train)
				train_actionPerformed(event);
			else if (object == test)
				test_actionPerformed(event);
			else if (object == trainFim)
				trainFim_actionPerformed(event);
			else if (object == recognize)
				recognize_actionPerformed(event);
		}
	}

	/**
	 * Called to clear the image.
	 * 
	 * @param event
	 *            The event
	 */
	void clear_actionPerformed(java.awt.event.ActionEvent event) {
		tf.setText("");
	}

	/**
	 * Called to clear the image.
	 * 
	 * @param event
	 *            The event
	 */
	void graph_actionPerformed(java.awt.event.ActionEvent event) {
		updateDataGraph();
		if (errorGraph.isVisible()) {
			errorGraph.setVisible(false);
		} else {
			errorGraph.setVisible(true);
		}
		if (errorIndexGraph.isVisible()) {
			errorIndexGraph.setVisible(false);
		} else {
			errorIndexGraph.setVisible(true);
		}
		if (indexDB.isVisible()) {
			indexDB.setVisible(false);
		} else {
			indexDB.setVisible(true);
		}
		if (indexDunn.isVisible()) {
			indexDunn.setVisible(false);
		} else {
			indexDunn.setVisible(true);
		}
		if (dataChart.isVisible()) {
			dataChart.setVisible(false);
		} else {
			dataChart.setVisible(true);
		}
		if (barChart.isVisible()) {
			barChart.setVisible(false);
		} else {
			barChart.setVisible(true);
		}
	}

	/**
	 * Called when the del button is pressed.
	 * 
	 * @param event
	 *            The event.
	 */
	void del_actionPerformed(java.awt.event.ActionEvent event) {
		int i = dados.getSelectedIndex();

		if (i == -1) {
			JOptionPane.showMessageDialog(this,
					"Please select a letter to delete.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		letterListModel.remove(i);
	}

	/**
	 * Called when the load button is pressed.
	 * 
	 * @param event
	 *            The event
	 * @return 
	 */
	public DefaultListModel load_actionPerformed(java.awt.event.ActionEvent event) {

		try {
			JFileChooser fc = null;
			if (selectedFile != null) {
				fc = new JFileChooser(selectedFile);
			} else {
				fc = new JFileChooser();
			}
			FileReader f = null;// the actual file stream
			BufferedReader r = null;// used to read the file line by line

			int j = fc.showDialog(this, "Select Data File");
			if (j == 0) {
				selectedFile = fc.getSelectedFile();
				if (fc != null) {
					f = new FileReader(selectedFile);
					r = new BufferedReader(f);
					String line;
					int i = 0;

					letterListModel.clear();

					updateDataGraph();
					String separador = getSeparator();
					int colIgnore = getColIgnored();
					while ((line = r.readLine()) != null) {
						String[] strings = line.split(separador);

						int sizeInput = strings.length;
						if (colIgnore > -1 && colIgnore < sizeInput) {
							sizeInput--;
						}
						double d[] = new double[sizeInput];
						Datum datum = new Datum(sizeInput);
						letterListModel.add(i++, datum);
						int index = 0;
						int c = 0;
						for (String string : strings) {
							if (index != colIgnore) {
								d[c++] = Double.parseDouble(string.trim());
							}
							index++;
						}
						datum.setIndexX(atrib1 - 1);
						datum.setIndexY(atrib2 - 1);
						datum.setAttributes(d);
					}

					r.close();
					f.close();
					clear_actionPerformed(null);
					JOptionPane.showMessageDialog(this, "Loaded from '"
							+ selectedFile.getName() + "'.", "Training",
							JOptionPane.PLAIN_MESSAGE);
					printInputs();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
					JOptionPane.ERROR_MESSAGE);
		}
		return letterListModel;
	}

	/**
	 * @return
	 */
	private int getColIgnored() {
		String colStr = ignoredColumn.getText();
		int colIgnore = -1;
		if (!"".equals(colStr)) {
			colIgnore = Integer.parseInt(colStr);
		}
		return colIgnore;
	}

	/**
	 * @return
	 */
	private String getSeparator() {
		String separador = (String) comboSeparator.getSelectedItem();
		if (separador.equals("SELECIONE SEPARADOR")
				|| separador.equals("Outro")) {
			separador = tfOutro.getText();
		}
		if (separador.equals("")) {
			separador = ";";
		} else if (separador.equals("Tab")) {
			separador = "\t";
		}
		return separador;
	}

	/**
	 * Update data graph
	 * @return 
	 */
	private boolean updateDataGraph() {
		String at1 = tfAtributo1.getText();
		String at2 = tfAtributo2.getText();
		boolean mudou = false;
		if (!at1.equals("") && !at1.equals("" + atrib1)) {
			mudou = true;
			atrib1 = Integer.parseInt(at1);
		}
		if (!at2.equals("") && !at2.equals("" + atrib2)) {
			mudou = true;
			atrib2 = Integer.parseInt(at2);
		}
		if (mudou) {
			dataChart.recreateDemoPanel(atrib1, atrib2);
			dataChart.repaint();
		}
		return mudou;
	}

	/**
	 * Run method for the background training thread.
	 */
	public void run() {
		if (letterListModel.size() > 0) {
			int inputNeuron = ((Datum) letterListModel.get(0)).getAttributes().length;
			int outputNeuron = (int) Math.ceil(Math
					.sqrt(letterListModel.size() / 10));

			try {
				// Create Clusterings algoritm
				createClustering(inputNeuron, outputNeuron);
				// Execute learn
				runLearn();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Load data set!", "Error",
					JOptionPane.ERROR_MESSAGE);
			train_actionPerformed(null);
		}
	}

	/**
	 * Execute learn
	 */
	private void runLearn() {
		clustering.setEpocas(Integer.parseInt(tfEpoca.getText()));
		clustering.setChart(dataChart);

		//If running test
		if (testThread!=null) {
			clustering.test();
			test_actionPerformed(null);
		} else if (testThread!=null) {
			clustering.learn();
			train_actionPerformed(null);
		}
		JOptionPane.showMessageDialog(this, "It finished '"
				+ algoritimo + "'.", "Training",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Create Clusterings algoritm
	 * 
	 * @param inputNeuron
	 * @param outputNeuron
	 */
	private void createClustering(int inputNeuron, int outputNeuron) {
		ButtonModel buttonModel = grupo.getSelection();

		String actionCommand = buttonModel.getActionCommand();
		boolean mudou = updateDataGraph();
		for (int i = 0; i < letterListModel.size(); i++) {
			Datum datum = ((Datum) letterListModel.get(i));
			datum.assignToCluster(0);
			if (mudou) {
				datum.setIndexX(atrib1 - 1);
				datum.setIndexY(atrib2 - 1);
			}
		}
		if (actionCommand.equals(SOM)) {
			algoritimo = SOM;
			clustering = new SOMNetwork(inputNeuron, outputNeuron
					* outputNeuron, this, letterListModel);
		} else if (actionCommand.equals(WTA)) {
			algoritimo = WTA;
			clustering = new WTANetwork(inputNeuron, outputNeuron
					* outputNeuron, this, letterListModel);
		} else if (actionCommand.equals(FSCL)) {
			algoritimo = FSCL;
			clustering = new FSCLNetwork(inputNeuron, outputNeuron
					* outputNeuron, this, letterListModel);
			double z = Double.valueOf(
					fator.getText()).doubleValue();
			((FSCLNetwork)clustering).setZ(z);
		} else if (actionCommand.equals(RPCL)) {
			algoritimo = RPCL;
			clustering = new RPCLNetwork(inputNeuron, outputNeuron
					* outputNeuron, this, letterListModel);
			double gamma = Double.valueOf(
					fator.getText()).doubleValue();
			((RPCLNetwork)clustering).setGama(gamma);
		} else if (actionCommand.equals(FUZZYCL)) {
			algoritimo = FUZZYCL;
			clustering = new FuzzyCLNetwork(inputNeuron, outputNeuron
					* outputNeuron, this, letterListModel);
			double z = Double.valueOf(
					fator.getText()).doubleValue();
			((FuzzyCLNetwork)clustering).setZ(z);
		} else if (actionCommand.equals(KMEANS)) {
			algoritimo = KMEANS;
			clustering = new KMeans(inputNeuron, outputNeuron, this,
					letterListModel);
		} else if (actionCommand.equals(NEURALGAS)) {
			algoritimo = NEURALGAS;
			clustering = new NGNetwork(inputNeuron,
					outputNeuron * outputNeuron, this, letterListModel);
		} else if (actionCommand.equals(GNEURALGAS)) {
			algoritimo = GNEURALGAS;
			clustering = new GNG(inputNeuron, outputNeuron * outputNeuron,
					this, letterListModel);
		}
		String numKMin = tfNumKMin.getText();
		String numKMax = tfNumKMax.getText();
		errorGraph.clear();
		errorIndexGraph.clear();
		indexDB.clear();
		indexDunn.clear();
		dataChart.clear();
		barChart.clear();
		try {
			clustering.setMinK(Integer.parseInt(numKMin));
			clustering.setMaxK(Integer.parseInt(numKMax));
			int lenImage = Integer.parseInt(tamImage.getText());
			clustering.setImageTam(lenImage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print input data in graph.
	 */
	private void printInputs() {
		for (int t = 0; t < letterListModel.size(); t++) {
			Datum ds = (Datum) letterListModel.getElementAt(t);
			dataChart.add(XYSeriesChart.DATA_CLUSTER + 1, ds.getAttributeX(),
					ds.getAttributeY());
		}
	}

	/**
	 * Called to update the stats, from the neural network.
	 * 
	 * @param epoca
	 *            How many tries.
	 * @param erroQuant
	 *            The current error.
	 */
	public void updateErrorGraph(int epoca, double erroQuant) {
		UpdateIndex stats = new UpdateIndex(errorGraph);
		stats.setXAxisValue(epoca + 1);
		stats.setIndexValue(erroQuant);

		try {
			SwingUtilities.invokeAndWait(stats);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called to update the stats, from the neural network.
	 * 
	 * @param kNumber
	 *            How many K.
	 * @param index
	 *            The current index.
	 */
	public void updateErrorIndexGraph(int kNumber, double valor) {
		UpdateIndex stats = new UpdateIndex(errorIndexGraph);
		stats.setXAxisValue(kNumber);
		stats.setIndexValue(valor);

		try {
			SwingUtilities.invokeAndWait(stats);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called to update the stats, from the neural network.
	 * 
	 * @param kNumber
	 *            How many K.
	 * @param index
	 *            The current index.
	 */
	public void updateBarGraph(int kNumber, double valor) {
		UpdateBarGraph stats = new UpdateBarGraph(barChart);
		stats.setXAxisValue(kNumber);
		stats.setIndexValue(valor);

		try {
			SwingUtilities.invokeAndWait(stats);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called to update the stats, from the neural network.
	 * 
	 * @param kNumber
	 *            How many K.
	 * @param index
	 *            The current index.
	 */
	public void updateDaviesBouldin(int kNumber, double index) {
		UpdateIndex stats = new UpdateIndex(indexDB);
		stats.setXAxisValue(kNumber);
		stats.setIndexValue(index);

		try {
			SwingUtilities.invokeAndWait(stats);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called to update the stats, from the neural network.
	 * 
	 * @param kNumber
	 *            How many K.
	 * @param index
	 *            The current index.
	 */
	public void updateDunn(int kNumber, double index) {
		UpdateIndex stats = new UpdateIndex(indexDunn);
		stats.setXAxisValue(kNumber);
		stats.setIndexValue(index);

		try {
			SwingUtilities.invokeAndWait(stats);
			// saveGraph(DUNN_GRAPH);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called to update the stats, from the neural network.
	 * 
	 * @param classe
	 *            How many tries.
	 * @param axisX
	 *            The current error.
	 * @param axisY
	 *            The best error.
	 */
	public void updateData(String classe, double axisX, double axisY) {
		UpdateData data = new UpdateData(dataChart);
		data.setSerie(classe);
		data.setYAxis(axisY);
		data.setXAxis(axisX);
		try {
			SwingUtilities.invokeAndWait(data);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Called when the train button is pressed.
	 * 
	 * @param event
	 *            The event.
	 */
	void train_actionPerformed(java.awt.event.ActionEvent event) {
		if (trainThread == null) {
			dataChart.clear();
			train.setText("Stop Training");
			train.repaint();
			trainThread = new Thread(this);
			trainThread.start();
		} else {
			train.setText("Train");
			train.repaint();
			trainThread = null;
		}
	}

	/**
	 * Called when the train button is pressed.
	 * 
	 * @param event
	 *            The event.
	 */
	void test_actionPerformed(java.awt.event.ActionEvent event) {
		if (testThread == null) {
			dataChart.clear();
			test.setText("Stop Testing");
			test.repaint();
			testThread = new Thread(this);
			testThread.start();
		} else {
			test.setText("Test");
			test.repaint();
			testThread = null;
		}
	}

	/**
	 * Called when the stop train button is pressed.
	 * 
	 * @param event
	 *            The event.
	 */
	void trainFim_actionPerformed(java.awt.event.ActionEvent event) {
		errorGraph.clear();
		indexDB.clear();
		indexDunn.clear();
		dataChart.clear();
		train.setText("Begin");
		train.repaint();
		trainThread = null;
	}

	public void start() {
		trainThread = new Thread(this);
		trainThread.start();

		if (errorGraph != null)
			errorGraph.setVisible(true);
		if (indexDB != null)
			indexDB.setVisible(true);
		if (indexDunn != null)
			indexDunn.setVisible(true);
	}

	public void stop() {
		trainThread.interrupt();
		if (errorGraph != null)
			errorGraph.setVisible(false);
		if (indexDB != null)
			indexDB.setVisible(false);
		if (indexDunn != null)
			indexDunn.setVisible(false);
	}

	/**
	 * Called when the recognize button is pressed.
	 * 
	 * @param event
	 *            The event.
	 */
	void recognize_actionPerformed(java.awt.event.ActionEvent event) {
		if (clustering == null) {
			JOptionPane.showMessageDialog(this, "I need to be trained first!",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		double input[] = new double[((Datum) letterListModel.get(0))
				.getAttributes().length];
		int idx = 0;
		String ds = tf.getText();
		String[] strings = ds.split(getSeparator());
		for (int x = 0; x < strings.length; x++) {
			input[idx++] = new Double(strings[x]).doubleValue();
		}
		Cluster best = clustering.winner(input);
		JOptionPane.showMessageDialog(this, " (Cluster #" + best + " fired)",
				"The Datum is", JOptionPane.PLAIN_MESSAGE);
		clear_actionPerformed(null);

	}

	/**
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {
	}

	/**
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
		if (buttonGraph != null)
			buttonGraph.setVisible(false);
		if (dataChart != null)
			dataChart.setVisible(false);
		this.dispose();
	}

	/**
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
	}

	/**
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/**
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
	}

	/**
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	public void saveGraph(String graph, int numPrototipos) {
		if (graph.equals(DATA_GRAPH)) {
			dataChart.repaint();
			dataChart.doSave(algoritimo, numPrototipos);
		} else if (graph.equals(ERROR_GRAPH)) {
			errorGraph.doSave(algoritimo, numPrototipos);
		} else if (graph.equals(ERROR_INDEX_GRAPH)) {
			errorIndexGraph.doSave(algoritimo, numPrototipos);
		} else if (graph.equals(DB_GRAPH)) {
			indexDB.doSave(algoritimo, numPrototipos);
		} else if (graph.equals(DUNN_GRAPH)) {
			indexDunn.doSave(algoritimo, numPrototipos);
		} else if (graph.equals(BAR_GRAPH)) {
			barChart.doSave(algoritimo, numPrototipos);
		}
	}

	public void clearGraph(String graph) {
		if (graph.equals(DATA_GRAPH)) {
			dataChart.clear();
		} else if (graph.equals(ERROR_GRAPH)) {
			errorGraph.clear();
		} else if (graph.equals(DB_GRAPH)) {
			indexDB.clear();
		} else if (graph.equals(DUNN_GRAPH)) {
			indexDunn.clear();
		} else if (graph.equals(BAR_GRAPH)) {
			barChart.clear();
		}
	}
}