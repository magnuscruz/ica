package br.ufc.deti.clusters;

/**
 *
 * @author Magnus Alencar da Cruz (magnuscruz@gmail.com)
 * @version 1.1
 */

public interface Reportable {
	public static String DATA_GRAPH = "DATA_GRAPH";
	public static String ERROR_GRAPH = "ERROR_GRAPH";
	public static String ERROR_INDEX_GRAPH = "ERROR_INDEX_GRAPH";
	public static String DUNN_GRAPH = "DUNN_GRAPH";
	public static String DB_GRAPH = "DB_GRAPH";
	public static String BAR_GRAPH = "BAR_GRAPH";
	void updateErrorGraph(int epoca, double errorQuant);
	void updateErrorIndexGraph(int kNumber, double index);

	void updateData(String classe, double axisX, double axisY);

	void updateDaviesBouldin(int kNumber, double index);
	void updateBarGraph(int kNumber, double index);

	void updateDunn(int kNumber, double index);

	void saveGraph(String graph, int numPrototipos);
	void clearGraph(String graph);
}
