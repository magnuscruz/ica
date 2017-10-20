package br.ufc.deti.clusters.charting;

public class UpdateData implements Runnable {
	String serie;

	double xAxis;

	double yAxis;

	private XYSeriesChart chart;

	public UpdateData(XYSeriesChart sChart) {
		this.chart = sChart;
	}

	public void run() {
		chart.add(serie, xAxis, yAxis);
	}
	/**
	 * @return Returns the serie.
	 */
	public String getSerie() {
		return serie;
	}
	/**
	 * @param serie The serie to set.
	 */
	public void setSerie(String serie) {
		this.serie = serie;
	}
	/**
	 * @return Returns the xAxis.
	 */
	public double getXAxis() {
		return xAxis;
	}
	/**
	 * @param axis The xAxis to set.
	 */
	public void setXAxis(double axis) {
		xAxis = axis;
	}
	/**
	 * @return Returns the yAxis.
	 */
	public double getYAxis() {
		return yAxis;
	}
	/**
	 * @param axis The yAxis to set.
	 */
	public void setYAxis(double axis) {
		yAxis = axis;
	}
}