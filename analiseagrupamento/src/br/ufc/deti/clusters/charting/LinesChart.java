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

import java.awt.BasicStroke;
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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;


/**
 * An example of a time series chart. For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 */
public class LinesChart extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1372440783901785908L;
	private ChartPanel chartPanel;
	private String xAxisLabel;
	private String yAxisLabel;
	Font font2 = new Font("times", Font.PLAIN, 12);

	/**
	 * A demonstration application showing how to create a simple time series
	 * chart. This example uses monthly data.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public LinesChart(String title, String name, String xAxisLabel, String yAxisLabel) {
		super(title);
		setName(name);
        setSize(400,300);
        setXAxisLabel(xAxisLabel);
        setYAxisLabel(yAxisLabel);
		chartPanel = (ChartPanel) createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
		chartPanel.setFont(font2);
	}

	private void setYAxisLabel(String axisLabel) {
		xAxisLabel = axisLabel;	
	}

	private void setXAxisLabel(String axisLabel) {
		yAxisLabel = axisLabel;
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

		JFreeChart chart = ChartFactory.createLineChart(
				getName(),
				yAxisLabel,
				xAxisLabel,
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
 		chart.setBackgroundPaint(Color.white);
 		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setDomainGridlinesVisible(true);
		plot.setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
 		NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
 		rangeAxis.setAutoRange(true);
 		rangeAxis.setAutoRangeIncludesZero(true);
 		rangeAxis.setUpperMargin(0.20000000000000001D);
 		rangeAxis.setLabelAngle(1.5707963267948966D);
 		rangeAxis.setLabel(xAxisLabel);
 		rangeAxis.setLabelFont(font2);
 		LineAndShapeRenderer renderer = (LineAndShapeRenderer)plot.getRenderer();
 		renderer.setDrawOutlines(true);
 		renderer.setSeriesStroke(0, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] {
 			10F, 6F
 		}, 0.0F));
 		renderer.setItemLabelsVisible(true);
 		renderer.setPositiveItemLabelPosition(new ItemLabelPosition());
 		renderer.setNegativeItemLabelPosition(new ItemLabelPosition());
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
	 * @param serie
	 * @param x
	 * @param y
	 */
	public void add(int x, double y) {
        DefaultCategoryDataset seriesKey = 
            (DefaultCategoryDataset) chartPanel.getChart().getCategoryPlot().getDataset();
        seriesKey.addValue(y, xAxisLabel, ""+x);
        this.repaint();		
	}

	public void doSave(String algoritimo, int numPrototipos) {
        try {
        	String path = "./images/" +
			        			algoritimo +
			        			"/";
			File file = new File(path);
        	if (!file.exists()) {
        		file.mkdirs();
        	}
        	String fileName = this.getTitle()+"-"+algoritimo+numPrototipos;
			String pathFile = path + fileName;
			file = new File(pathFile+".png");
			file.createNewFile();
			ChartUtilities.saveChartAsPNG(
			        file, chartPanel.getChart(), getWidth(), getHeight()
			);
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
				ys[c] = seriesKey.getValue(xAxisLabel,s).doubleValue();
				c++;
			}
			LineSeriesGnuplot lineGnuplot = new LineSeriesGnuplot(
					this.getTitle(),
					pathFile+".dat",
					xs,
					ys);
			gnuplot.addSource(lineGnuplot);
			gnuplot.setOutputfile(pathFile);
			if (getTitle().indexOf("Index") > -1)
				gnuplot.setStyle("linespoints pointtype 5");
			else
				gnuplot.setStyle("lines");
			
			gnuplot.setLabels(yAxisLabel,xAxisLabel);
			gnuplot.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {

		LinesChart demo = new LinesChart("Time Series Chart Demo 1", "X x Y", "X", "Y");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

	@Override
	public String toString() {
		return "[Title = " + getTitle()
		+ "]-[Name" + getName()
		+ "[XAxisLabel = " + getXAxisLabel()
		+ "]-[YAxisLabel" + getYAxisLabel()+ "]";
	}

	/**
	 * @return the xAxisLabel
	 */
	public String getXAxisLabel() {
		return xAxisLabel;
	}

	/**
	 * @return the yAxisLabel
	 */
	public String getYAxisLabel() {
		return yAxisLabel;
	}

}