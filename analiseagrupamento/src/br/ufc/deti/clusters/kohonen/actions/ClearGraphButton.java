package br.ufc.deti.clusters.kohonen.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;


public class ClearGraphButton extends AbstractButtonGraph {
	/**
	 * 
	 */
	private static final long serialVersionUID = -130302709198907793L;
	public ClearGraphButton(Frame frame) {
		super(CLEAR,frame);
	}
	public void actionPerformed(ActionEvent e) {
		//((GraphGNG)getFrame()).getGraph().clear();
	}
}
