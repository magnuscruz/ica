
package br.ufc.deti.clusters.charting;

import java.io.File;

import de.unidu.is.gnuplot.ArrayDataSource;
import de.unidu.is.gnuplot.FileSource;
import de.unidu.is.gnuplot.Source;

/**
 * An example of a time series chart. For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 */
public class LineSeriesGnuplot extends ArrayDataSource {

	/**
	 * Constructs a new instance, sets the specified values, and sets
	 * the instance variable <code>count</code> to <code>-1</code>. 
	 *
	 * @param title title of this curve, or <code>null</code>
	 * @param file data output file
	 * @param x x values of this curve
	 * @param y y values of this curve
	 */
	public LineSeriesGnuplot(
			String title,
			File file,
			double[] x,
			double[] y) {
		super(title, file, x, y);
	}

	/**
	 * Constructs a new instance, sets the specified values, and sets
	 * the instance variable <code>count</code> to <code>-1</code>. 
	 *
	 * @param title title of this curve, or <code>null</code>
	 * @param filename name of the data output file
	 * @param x x values of this curve
	 * @param y y values of this curve
	 */
	public LineSeriesGnuplot(
		String title,
		String filename,
		double[] x,
		double[] y) {
		super(title, filename, x, y);
	}
}