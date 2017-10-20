package br.ufc.deti.clusters.kohonen.actions;

import java.awt.Button;
import java.awt.Frame;
import java.awt.event.ActionListener;

public abstract class AbstractButtonGraph extends Button implements ActionListener {
	/**
	 * The name of the close button.
	 */
	protected final static String CLOSE = "Close";
	/**
	 * The name of the clear button.
	 */
	protected final static String CLEAR = "Clear";
	private Frame frame;
	public AbstractButtonGraph(String name, Frame frame) {
		super(name);
		addActionListener(this);
		this.frame = frame;
		
	}
	public Frame getFrame() {
		return frame;
	}
}
