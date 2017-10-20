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
import java.awt.GradientPaint;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of a time series chart. For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 */
public class BarSeriesChart extends ApplicationFrame {

	public static String xLabel = "CLUSTER";
	public static String yLabel = "num";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5491668435037612732L;

	private ChartPanel chartPanel;

	Font font2 = new Font("times", Font.PLAIN, 12);

	/**
	 * A demonstration application showing how to create a simple time series
	 * chart. This example uses monthly data.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public BarSeriesChart(String title) {
		super(title);
		setSize(400, 400);
		chartPanel = (ChartPanel) createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
		chartPanel.setFont(font2);
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return A chart.
	 */
	private JFreeChart createChart(CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart("Number Wins",
				xLabel,
				yLabel,
				dataset, PlotOrientation.VERTICAL, false, true, false); 
 	    
 
        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
       // plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
 
        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
 
        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer(); 
        renderer.setItemLabelsVisible(true);
        chart.getCategoryPlot().setRenderer(renderer);
                
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0) );

		return chart;

	}

	/**
	 * Creates a dataset, consisting of two series of monthly data.
	 * 
	 * @return The dataset.
	 */
	private static CategoryDataset createDataset() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		return dataset;
	}

	/**
	 * To Add value in serie.
	 * 
	 * @param serie
	 * @param x
	 * @param y
	 */
	public void add(int serie, double y) {
        DefaultCategoryDataset seriesKey = 
            (DefaultCategoryDataset) chartPanel.getChart().getCategoryPlot().getDataset();
        seriesKey.addValue(y, ""+serie, ""+serie);
        this.repaint();	
	}

	/**
	 * Clear all series
	 */
	public void clear() {
        DefaultCategoryDataset seriesKey = 
            (DefaultCategoryDataset) chartPanel.getChart().getCategoryPlot().getDataset();
        seriesKey.clear();
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
			String path = "./images/" + algoritimo + "/";
			String pathFile = path +"NumWinsChart" + algoritimo + numPrototipos;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(pathFile
					+ ".png");
			file.createNewFile();
			ChartUtilities.saveChartAsPNG(file, chartPanel.getChart(),
					getWidth(), getHeight());
			DefaultCategoryDataset seriesKey = 
	            (DefaultCategoryDataset) chartPanel.getChart().getCategoryPlot().getDataset();
			List columnKeys = seriesKey.getColumnKeys();
			double[] xs = new double[columnKeys.size()];
			double[] ys = new double[columnKeys.size()];
			int c = 0;
			Gnuplot gnuplot = new Gnuplot();
			for (Object d : columnKeys) {
				String s = d.toString();
				double x = Double.parseDouble(s);
				xs[c] = x;
				ys[c] = seriesKey.getValue(s,s).doubleValue();
				c++;
			}
			LineSeriesGnuplot lineGnuplot = new LineSeriesGnuplot(
					this.getTitle(),
					pathFile+".dat",
					xs,
					ys);
			gnuplot.addSource(lineGnuplot);
			gnuplot.setOutputfile(pathFile);
			gnuplot.setStyle("boxes fs solid 0.50");
			//gnuplot.setXStyle("boxes fs solid 0.50");
			//gnuplot.setStyle("boxes fill pattern 1 lt 4");
			gnuplot.setLabels(yLabel,xLabel);
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

		BarSeriesChart demo = new BarSeriesChart("Time Series Chart Demo 1");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}