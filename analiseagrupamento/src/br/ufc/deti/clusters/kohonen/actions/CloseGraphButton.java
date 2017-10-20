package br.ufc.deti.clusters.kohonen.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

public class CloseGraphButton extends AbstractButtonGraph {
	/**
	 * 
	 */
	private static final long serialVersionUID = -130302709198907793L;
	public CloseGraphButton(Frame frame) {
		super(CLOSE,frame);
	}
	public void actionPerformed(ActionEvent e) {
		//((GraphGNG)getFrame()).dispose();
	}
}
