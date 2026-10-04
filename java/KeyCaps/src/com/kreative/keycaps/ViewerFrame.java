package com.kreative.keycaps;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ViewerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final ViewerPanel panel;
	private long kbdLastMod;
	private File kbdFile;
	
	public ViewerFrame(ViewerComponent vc, File kbdFile, File kbdDir) {
		this.panel = new ViewerPanel(vc);
		this.kbdLastMod = getLastModifiedTime(kbdFile);
		this.kbdFile = kbdFile;
		setTitle(myWindowTitle());
		setJMenuBar(new ViewerMenuBar(this, kbdDir));
		setContentPane(this.panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new CheckListener());
	}
	
	public ViewerPanel getViewerPanel() {
		return this.panel;
	}
	
	public File getKeyboardFile() {
		return this.kbdFile;
	}
	
	private synchronized void checkFile() {
		try {
			if (kbdFile == null) return;
			long lmt = getLastModifiedTime(kbdFile);
			if (kbdLastMod == lmt) return;
			KeyCapLayout layout = KeyCapReader.read(kbdFile);
			this.panel.getViewerComponent().setKeyCapLayout(layout);
			this.kbdLastMod = lmt;
			this.setTitle(myWindowTitle());
			this.hack();
		} catch (IOException e) {
			return;
		}
	}
	
	public synchronized void openFile(File file) {
		try {
			KeyCapLayout layout = KeyCapReader.read(file);
			this.panel.getViewerComponent().setKeyCapLayout(layout);
			this.kbdLastMod = getLastModifiedTime(file);
			this.kbdFile = file;
			this.setTitle(myWindowTitle());
			this.hack();
		} catch (IOException e) {
			String msg = "Could not open " + file.getName() + ": " + e.toString();
			JOptionPane.showMessageDialog(this, msg, "Open", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public synchronized void saveFile(String format, File file) {
		try {
			AWTRenderer renderer = this.panel.getViewerComponent().getRenderer();
			KeyCapLayout layout = this.panel.getViewerComponent().getKeyCapLayout();
			Object obj = UIUtilities.createTransferData(renderer, layout, format);
			UIUtilities.writeTransferData(obj, format, file);
			this.kbdLastMod = getLastModifiedTime(file);
			this.kbdFile = file;
			this.setTitle(myWindowTitle());
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
	
	private String myWindowTitle() {
		KeyCapLayout layout = this.panel.getViewerComponent().getKeyCapLayout();
		String name = layout.getPropertyMap().getString("name");
		if (name != null && name.length() > 0) return name;
		if (kbdFile != null) {
			name = kbdFile.getName();
			int o = name.lastIndexOf(".");
			if (o > 0) name = name.substring(0, o);
			if (name.length() > 0) return name;
		}
		return "Key Caps";
	}
	
	private class CheckThread extends Thread {
		public void run() {
			while (!Thread.interrupted()) {
				checkFile();
				try { Thread.sleep(100); }
				catch (InterruptedException e) { return; }
			}
		}
	}
	
	private class CheckListener extends WindowAdapter {
		private final CheckThread thread = new CheckThread();
		public void windowOpened(WindowEvent e) { thread.start(); }
		public void windowClosed(WindowEvent e) { thread.interrupt(); }
	}
	
	private static long getLastModifiedTime(File file) {
		if (file == null) {
			return 0;
		} else try {
			Object fp = File.class.getMethod("toPath").invoke(file);
			Class<?> files = Class.forName("java.nio.file.Files");
			Class<?> path = Class.forName("java.nio.file.Path");
			Class<?> linkOption = Class.forName("java.nio.file.LinkOption");
			Class<?> linkOptionA = Class.forName("[Ljava.nio.file.LinkOption;");
			Method getLMT = files.getMethod("getLastModifiedTime", path, linkOptionA);
			Object lmt = getLMT.invoke(null, fp, Array.newInstance(linkOption, 0));
			Class<?> fileTime = Class.forName("java.nio.file.attribute.FileTime");
			Object millis = fileTime.getMethod("toMillis").invoke(lmt);
			return ((Number)millis).longValue();
		} catch (Exception e) {
			return 0;
		}
	}
}
