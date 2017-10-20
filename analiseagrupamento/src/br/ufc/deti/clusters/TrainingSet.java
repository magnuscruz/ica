package br.ufc.deti.clusters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TrainingSet Copyright 2005 by Jeff Heaton(jeff@jeffheaton.com)
 * 
 * Example program from Chapter 7 Programming Neural Networks in Java
 * http://www.heatonresearch.com/articles/series/1/
 * 
 * This software is copyrighted. You may use it in programs of your own, without
 * restriction, but you may not publish the source code without the author's
 * permission. For more information on distributing this code, please visit:
 * http://www.heatonresearch.com/hr_legal.php
 * 
 * @author Jeff Heaton
 * @version 1.1
 */

public class TrainingSet {

	protected int inputCount;

	public Datum input[];

	protected int trainingSetCount;

	/**
	 * The constructor.
	 * 
	 * @param inputCount
	 *            Number of input neurons
	 * @param outputCount
	 *            Number of output neurons
	 */
	public TrainingSet(int inputCount) {
		this.inputCount = inputCount;
		trainingSetCount = 0;
	}

	/**
	 * Get the input neuron count
	 * 
	 * @return The input neuron count
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * Set the number of entries in the training set. This method also allocates
	 * space for them.
	 * 
	 * @param trainingSetCount
	 *            How many entries in the training set.
	 */
	public void setTrainingSetCount(int trainingSetCount) {
		this.trainingSetCount = trainingSetCount;
		input = new Datum[trainingSetCount];
		for (Datum element : input) {
			element.setAttributes(new double[inputCount]);
		}
	}

	/**
	 * Get the training set data.
	 * 
	 * @return Training set data.
	 */
	public int getTrainingSetCount() {
		return trainingSetCount;
	}

	/**
	 * Set one of the training set's inputs.
	 * 
	 * @param set
	 *            The entry number
	 * @param index
	 *            The index(which item in that set)
	 * @param value
	 *            The value
	 * @exception java.lang.RuntimeException
	 */
	public void setInput(int set, int index, double value)
			throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		if ((index < 0) || (index >= inputCount))
			throw (new RuntimeException("Training input index out of range:"
					+ index));
		input[set].add(index, value);
	}

	/**
	 * Get an input set.
	 * 
	 * @param set
	 *            The entry requested.
	 * @return The complete input set as an array.
	 * @exception java.lang.RuntimeException
	 */
	public double[] getInputSet(int set) throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		return input[set].getAttributes();
	}

	public void sortRandomInputs() {
		List<Datum> inputList = Arrays.asList(input);
		Collections.sort(inputList, RandomComparator.getInstance());
		input = (Datum[]) inputList.toArray();
	}

	
}
