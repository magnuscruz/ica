/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------------
 * TimeSeriesChartDemo1.java
 * -------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   ;
 *
 * $Id: TimeSeriesChartDemo1.java,v 1.2.2.2 2005/10/25 20:41:32 mungady Exp $
 *
 * Changes
 * -------
 * 09-Mar-2005 : Version 1, copied from the demo collection that ships with
 *               the JFreeChart Developer Guide (DG);
 *
 */

package br.ufc.deti.clusters.charting;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of a time series chart. For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 */
public class XYSeriesChart extends ApplicationFrame {

	public static final String ATTRIBUTE = "ATRIBUTO";

	public static final String ATTRIBUTE1 = "ATRIBUTO 1";

	public static final String ATTRIBUTE2 = "ATRIBUTO 2";

	public static final String ATTRIBUTE3 = "ATRIBUTO 3";

	public static final String ATTRIBUTE4 = "ATRIBUTO 4";

	public static final String PROTOTIPOS = "PROTOTIPOS";

	public static final String DATA_CLUSTER = "CLUSTER";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5491668435037612732L;

	private ChartPanel chartPanel;

	private int idXAxisLabel;

	private int idYAxisLabel;

	Font font2 = new Font("times", Font.PLAIN, 12);

	/**
	 * A demonstration application showing how to create a simple time series
	 * chart. This example uses monthly data.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public XYSeriesChart(String title, int xAxisLabel, int yAxisLabel) {
		super(title);
		setSize(400, 400);
		this.idXAxisLabel = xAxisLabel;
		this.idYAxisLabel = yAxisLabel;
		chartPanel = (ChartPanel) createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
		chartPanel.setFont(font2);
	}

	public void recreateDemoPanel(int xAxisLabel, int yAxisLabel) {
		this.idYAxisLabel = yAxisLabel;
		this.idXAxisLabel = xAxisLabel;
		chartPanel.setChart(createChart(createDataset()));
		chartPanel.repaint();
		// chartPanel.c = (ChartPanel) createDemoPanel();
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return A chart.
	 */
	private JFreeChart createChart(XYDataset dataset) {

		String xAxisLabel = "";
		String yAxisLabel = "";
		if (idXAxisLabel == 1) {
			xAxisLabel = ATTRIBUTE1;
		} else if (idXAxisLabel == 2) {
			xAxisLabel = ATTRIBUTE2;
		} else if (idXAxisLabel == 3) {
			xAxisLabel = ATTRIBUTE3;
		} else if (idXAxisLabel == 4) {
			xAxisLabel = ATTRIBUTE4;
		}

		if (idYAxisLabel == 1) {
			yAxisLabel = ATTRIBUTE1;
		} else if (idYAxisLabel == 2) {
			yAxisLabel = ATTRIBUTE2;
		} else if (idYAxisLabel == 3) {
			yAxisLabel = ATTRIBUTE3;
		} else if (idYAxisLabel == 4) {
			yAxisLabel = ATTRIBUTE4;
		}
		JFreeChart chart = ChartFactory.createScatterPlot("Data PLOT", // title
				xAxisLabel, // x-axis label
				yAxisLabel, // y-axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		r.setBaseItemLabelFont(font2);
		// r.setSeriesItemLabelFont(0, font2);
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}

		return chart;

	}

	/**
	 * Creates a dataset, consisting of two series of monthly data.
	 * 
	 * @return The dataset.
	 */
	private static XYDataset createDataset() {
		XYSeries s1 = new XYSeries(PROTOTIPOS);

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s1);

		return dataset;
	}

	/**
	 * To Add value in serie.
	 * 
	 * @param serie
	 * @param x
	 * @param y
	 */
	public void add(String serie, double x, double y) {
		XYSeriesCollection seriesKey = (XYSeriesCollection) chartPanel
				.getChart().getXYPlot().getDataset();
		XYSeries series = null;
		int parseInt = 0;
		if (PROTOTIPOS.equals(serie)) {
			series = seriesKey.getSeries(0);
		} else if (serie.startsWith(DATA_CLUSTER)) {
			parseInt = Integer.parseInt(serie.replaceAll(DATA_CLUSTER,""));
			if (seriesKey.getSeriesCount() > parseInt) {
				series = seriesKey.getSeries(parseInt);
			}
		}

		if (series == null) {
			series = new XYSeries(DATA_CLUSTER + parseInt);
			series.setDescription(DATA_CLUSTER + parseInt);
			seriesKey.addSeries(series);
		}
		series.add(x, y);
	}

	/**
	 * Clear all series
	 */
	public void clear() {
		XYSeriesCollection seriesKey = (XYSeriesCollection) chartPanel
				.getChart().getXYPlot().getDataset();
		int count = seriesKey.getSeriesCount();
		seriesKey.getSeries(0).clear();
		for (int i = count - 1; i > 0; i--) {
			seriesKey.removeSeries(i);
		}
		repaint();
	}

	/**
	 * Clear serie
	 */
	public void clear(String serie) {
		XYSeriesCollection seriesKey = (XYSeriesCollection) chartPanel
				.getChart().getXYPlot().getDataset();
		XYSeries series = null;
		int parseInt = 0;
		if (PROTOTIPOS.equals(serie)) {
			series = seriesKey.getSeries(0);
		} else if (serie.startsWith(DATA_CLUSTER)) {
			parseInt = Integer.parseInt(serie.replaceAll(DATA_CLUSTER,""));
			if (seriesKey.getSeriesCount() > parseInt) {
				series = seriesKey.getSeries(parseInt);
			}
		}

		series.clear();
		repaint();
	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 * 
	 * @return A panel.
	 */
	public JPanel createDemoPanel() {
		JFreeChart chart = createChart(createDataset());
		return new ChartPanel(chart);
	}

	public void doSave(String algoritimo, int numPrototipos) {
		try {
			repaint();
			String path = "./images/" + algoritimo + "/";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			String fileName = this.getTitle()+"-"+algoritimo+numPrototipos;
			String pathFile = path + fileName;
			file = new File(pathFile + ".png");
			file.createNewFile();
			ChartUtilities.saveChartAsPNG(file, chartPanel.getChart(),
					getWidth(), getHeight());
			XYSeriesCollection seriesKey = (XYSeriesCollection) chartPanel
					.getChart().getXYPlot().getDataset();
			double[] xs = null;
			double[] ys = null;
			Gnuplot gnuplot = new Gnuplot();
			List<XYSeries> columnKeys = seriesKey.getSeries();
			int i = 0;
			for (XYSeries xy : columnKeys) {
				List<XYDataItem> items = xy.getItems();
				xs = new double[items.size()];
				ys = new double[items.size()];
				int c = 0;
				for (XYDataItem item : items) {
					ys[c] = item.getX().doubleValue();
					xs[c] = item.getY().doubleValue();
					c++;
				}
				LineSeriesGnuplot lineGnuplot = new LineSeriesGnuplot(
						(i==0?PROTOTIPOS:DATA_CLUSTER+i), pathFile+ i + ".dat", xs, ys);
				gnuplot.addSource(lineGnuplot);
				i++;
			}
			gnuplot.setOutputfile(pathFile);
			gnuplot.setBW();
			gnuplot.setStyle("points");
			gnuplot.setLabels(ATTRIBUTE +" "+ idYAxisLabel,
					ATTRIBUTE +" "+ idXAxisLabel);
			gnuplot.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {

		XYSeriesChart demo = new XYSeriesChart("Time Series Chart Demo 1", 1, 2);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}