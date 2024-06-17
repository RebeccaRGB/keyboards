package com.kreative.keycaps;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ViewerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final ViewerPanel panel;
	
	public ViewerFrame(ViewerComponent vc) {
		this.panel = new ViewerPanel(vc);
		
		setTitle("Key Caps");
		setJMenuBar(new ViewerMenuBar(this));
		setContentPane(this.panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public ViewerPanel getViewerPanel() {
		return this.panel;
	}
	
	public void openFile(File file) {
		try {
			KeyCapLayout layout = KeyCapReader.read(file);
			this.panel.getViewerComponent().setKeyCapLayout(layout);
			this.pack();
		} catch (IOException e) {
			String msg = "Could not open " + file.getName() + ": " + e.toString();
			JOptionPane.showMessageDialog(this, msg, "Open", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void saveFile(String format, File file) {
		try {
			AWTRenderer renderer = this.panel.getViewerComponent().getRenderer();
			KeyCapLayout layout = this.panel.getViewerComponent().getKeyCapLayout();
			Object obj = UIUtilities.createTransferData(renderer, layout, format);
			UIUtilities.writeTransferData(obj, format, file);
		} catch (IOException e) {
			String msg = "Could not save " + file.getName() + ": " + e.toString();
			JOptionPane.showMessageDialog(this, msg, "Save", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void copy(String format) {
		AWTRenderer renderer = this.panel.getViewerComponent().getRenderer();
		KeyCapLayout layout = this.panel.getViewerComponent().getKeyCapLayout();
		Object obj = UIUtilities.createTransferData(renderer, layout, format);
		UIUtilities.copyTransferData(obj);
	}
	
	public void hack() {
		int fw = this.getWidth();
		int fh = this.getHeight();
		int vw = this.panel.getViewerComponent().getWidth();
		int vh = this.panel.getViewerComponent().getHeight();
		Dimension ps = this.panel.getViewerComponent().getPreferredSize();
		int nw = ps.width + fw - vw;
		int nh = ps.height + fh - vh;
		this.setSize(nw, nh);
	}
}
