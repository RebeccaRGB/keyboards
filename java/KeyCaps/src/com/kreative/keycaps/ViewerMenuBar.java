package com.kreative.keycaps;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class ViewerMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	private static final int[] KEY_CAP_SIZES = {
		20, // Size of keys in KCAP resource using KbitKeyCapMold
		28, // Size of large icon for Key Caps desk accessory using IconKeyCapMold
		36, // 3/4 in at 48 dpi
		48, // 3/4 in at 64 dpi
		54, // 3/4 in at 72 dpi
		72, // 3/4 in at 96 dpi
	};
	
	private final ViewerFrame frame;
	
	public ViewerMenuBar(ViewerFrame frame, File kbdDir) {
		this.frame = frame;
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new OpenMenuItem());
		fileMenu.add(new UIUtilities.CloseMenuItem(frame));
		fileMenu.addSeparator();
		fileMenu.add(new SaveMenuItem("Save as Text...", 0, 0, ".txt", "txt"));
		fileMenu.add(new SaveMenuItem("Save as XML...", 0, 0, ".kkcx", "kkcx"));
		fileMenu.addSeparator();
		fileMenu.add(new SaveMenuItem("Export to SVG...", 0, 0, ".svg", "svg"));
		fileMenu.add(new SaveMenuItem("Export to PNG...", 0, 0, ".png", "png"));
		if (!UIUtilities.IS_MAC_OS) {
			fileMenu.addSeparator();
			fileMenu.add(new UIUtilities.ExitMenuItem());
		}
		add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new CopyMenuItem("Copy as Text", 0, 0, "txt"));
		editMenu.add(new CopyMenuItem("Copy as XML", 0, 0, "kkcx"));
		editMenu.add(new CopyMenuItem("Copy as SVG", 0, 0, "svg"));
		editMenu.add(new CopyMenuItem("Copy as PNG", 0, 0, "png"));
		add(editMenu);
		
		if (kbdDir != null) {
			ActionListener kbdDirListener = new KeyboardDirectoryListener();
			JMenu kbdDirMenu = (JMenu)ViewerFileMenuItem.create(kbdDir, kbdDirListener);
			kbdDirMenu.setText("Layout");
			add(kbdDirMenu);
		}
		
		JMenu styleMenu = new JMenu("Style");
		styleMenu.add(new KeyCapMoldMenuItem(null, 0, 0, new IconKeyCapMold()));
		styleMenu.add(new KeyCapMoldMenuItem(null, 0, 0, new KbitKeyCapMold()));
		styleMenu.add(new KeyCapMoldMenuItem(null, 0, 0, new MaxKeyCapMold()));
		styleMenu.addSeparator();
		styleMenu.add(new KeyCapMoldScaleMenuItem(null, 0, 0, 0.5f));
		styleMenu.add(new KeyCapMoldScaleMenuItem(null, 0, 0, 1.0f));
		styleMenu.add(new KeyCapMoldScaleMenuItem(null, 0, 0, 2.0f));
		styleMenu.addSeparator();
		for (int keyCapSize : KEY_CAP_SIZES) {
			styleMenu.add(new KeyCapSizeMenuItem(null, 0, 0, keyCapSize));
		}
		add(styleMenu);
	}
	
	private class OpenMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public OpenMenuItem() {
			super("Open...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, UIUtilities.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File file = UIUtilities.getOpenFile(frame);
					if (file != null) frame.openFile(file);
				}
			});
		}
	}
	
	private class SaveMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveMenuItem(String text, int key, int mod, final String suffix, final String format) {
			super(text);
			if (key != 0) setAccelerator(KeyStroke.getKeyStroke(key, UIUtilities.SHORTCUT_KEY | mod));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AWTRenderer renderer = frame.getViewerPanel().getViewerComponent().getRenderer();
					KeyCapLayout layout = frame.getViewerPanel().getViewerComponent().getKeyCapLayout();
					if (renderer == null || layout == null || layout.isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
					} else {
						File file = UIUtilities.getSaveFile(frame, suffix);
						if (file != null) frame.saveFile(format, file);
					}
				}
			});
		}
	}
	
	private class CopyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyMenuItem(String text, int key, int mod, final String format) {
			super(text);
			if (key != 0) setAccelerator(KeyStroke.getKeyStroke(key, UIUtilities.SHORTCUT_KEY | mod));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.copy(format);
				}
			});
		}
	}
	
	private class KeyboardDirectoryListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = ((ViewerFileMenuItem)e.getSource()).getFile();
			try {
				KeyCapLayout layout = KeyCapReader.read(file);
				frame.getViewerPanel().getViewerComponent().setKeyCapLayout(layout);
				frame.pack();
			} catch (IOException ioe) {
				String msg = "Could not open " + file.getName() + ": " + e.toString();
				JOptionPane.showMessageDialog(frame, msg, "Open", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private class KeyCapMoldMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public KeyCapMoldMenuItem(String text, int key, int mod, final KeyCapMold mold) {
			super((text != null) ? text : mold.getClass().getSimpleName());
			if (key != 0) setAccelerator(KeyStroke.getKeyStroke(key, UIUtilities.SHORTCUT_KEY | mod));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AWTRenderer renderer = frame.getViewerPanel().getViewerComponent().getRenderer();
					if (renderer != null) {
						frame.getViewerPanel().getViewerComponent().setRenderer(new AWTRenderer(
							mold, renderer.getKeyCapMoldScale(),
							renderer.getKeyCapSize(), null
						));
						frame.hack();
					}
				}
			});
		}
	}
	
	private class KeyCapMoldScaleMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public KeyCapMoldScaleMenuItem(String text, int key, int mod, final float moldScale) {
			super((text != null) ? text : ("Scale " + moldScale + "x"));
			if (key != 0) setAccelerator(KeyStroke.getKeyStroke(key, UIUtilities.SHORTCUT_KEY | mod));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AWTRenderer renderer = frame.getViewerPanel().getViewerComponent().getRenderer();
					if (renderer != null) {
						frame.getViewerPanel().getViewerComponent().setRenderer(new AWTRenderer(
							renderer.getKeyCapMold(), moldScale,
							renderer.getKeyCapSize(), null
						));
						frame.hack();
					}
				}
			});
		}
	}
	
	private class KeyCapSizeMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public KeyCapSizeMenuItem(String text, int key, int mod, final int keyCapSize) {
			super((text != null) ? text : ("Size " + keyCapSize + "px"));
			if (key != 0) setAccelerator(KeyStroke.getKeyStroke(key, UIUtilities.SHORTCUT_KEY | mod));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AWTRenderer renderer = frame.getViewerPanel().getViewerComponent().getRenderer();
					if (renderer != null) {
						frame.getViewerPanel().getViewerComponent().setRenderer(new AWTRenderer(
							renderer.getKeyCapMold(),
							renderer.getKeyCapMoldScale(),
							keyCapSize, null
						));
						frame.hack();
					}
				}
			});
		}
	}
}
