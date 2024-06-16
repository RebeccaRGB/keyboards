package com.kreative.keycaps;

import javax.swing.JFrame;

public class ViewerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final ViewerPanel panel;
	
	public ViewerFrame(ViewerComponent vc) {
		this.panel = new ViewerPanel(vc);
		
		setTitle("Key Caps");
		setContentPane(this.panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public ViewerPanel getViewerPanel() {
		return this.panel;
	}
}
