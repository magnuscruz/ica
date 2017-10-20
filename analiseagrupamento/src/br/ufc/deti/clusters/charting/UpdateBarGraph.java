package br.ufc.deti.clusters.charting;

public class UpdateBarGraph implements Runnable {
	private int xAxisValue;

	private double indexValue;

	private BarSeriesChart graph;

	public UpdateBarGraph(BarSeriesChart barChart) {
		this.graph = barChart;
	}

	public void run() {
		graph.add(xAxisValue, indexValue);
	}

	public double getIndexValue() {
		return indexValue;
	}

	public void setIndexValue(double _indexValue) {
		this.indexValue = _indexValue;
	}

	public long getXAxisValue() {
		return xAxisValue;
	}

	public void setXAxisValue(int _xAxisValue) {
		this.xAxisValue = _xAxisValue;
	}

	@Override
	public String toString() {
		return graph.toString();
	}
}