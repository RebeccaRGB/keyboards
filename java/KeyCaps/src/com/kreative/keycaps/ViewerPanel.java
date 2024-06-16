package com.kreative.keycaps;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ViewerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public final ViewerComponent vc;
	
	public ViewerPanel(ViewerComponent vc) {
		this.vc = vc;
		
		JPanel viewPanel = new JPanel(new BorderLayout());
		viewPanel.add(this.vc, BorderLayout.CENTER);
		viewPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setLayout(new BorderLayout());
		add(viewPanel, BorderLayout.CENTER);
	}
	
	public ViewerComponent getViewerComponent() {
		return this.vc;
	}
}
