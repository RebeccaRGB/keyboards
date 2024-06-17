package com.kreative.keycaps;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class UIUtilities {
	public static final int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	public static final boolean IS_MAC_OS;
	static {
		boolean isMacOS;
		try { isMacOS = System.getProperty("os.name").toUpperCase().contains("MAC OS"); }
		catch (Exception e) { isMacOS = false; }
		IS_MAC_OS = isMacOS;
	}
	
	private static final String[] KBD_DIR_NAMES = {
		"keyboardskkcx", "keyboardsxml",
		"keyboardskkc", "keyboardstxt",
		"keyboards"
	};
	
	public static File getKeyboardDirectory() {
		File parent = new File(".").getAbsoluteFile();
		while (parent != null) {
			File[] children = parent.listFiles();
			for (String kbdDirName : KBD_DIR_NAMES) {
				for (File child : children) {
					if (child.isDirectory()) {
						String nn = child.getName().replaceAll("[^\\p{L}\\p{M}\\p{N}]", "");
						if (nn.equalsIgnoreCase(kbdDirName)) return child;
					}
				}
			}
			parent = parent.getParentFile();
		}
		return null;
	}
	
	private static String lastOpenDirectory = null;
	static {
		File kbdDir = getKeyboardDirectory();
		if (kbdDir != null) lastOpenDirectory = kbdDir.getAbsolutePath();
	}
	
	public static File getOpenFile(Frame frame) {
		boolean disposableFrame = (frame == null);
		if (disposableFrame) frame = new Frame();
		FileDialog fd = new FileDialog(frame, "Open", FileDialog.LOAD);
		if (lastOpenDirectory != null) fd.setDirectory(lastOpenDirectory);
		fd.setVisible(true);
		String ds = fd.getDirectory(), fs = fd.getFile();
		fd.dispose();
		if (disposableFrame) frame.dispose();
		if (ds == null || fs == null) return null;
		return new File((lastOpenDirectory = ds), fs);
	}
	
	private static String lastSaveDirectory = null;
	
	public static File getSaveFile(Frame frame, String suffix) {
		boolean disposableFrame = (frame == null);
		if (disposableFrame) frame = new Frame();
		FileDialog fd = new FileDialog(frame, "Save", FileDialog.SAVE);
		if (lastSaveDirectory != null) fd.setDirectory(lastSaveDirectory);
		fd.setVisible(true);
		String ds = fd.getDirectory(), fs = fd.getFile();
		fd.dispose();
		if (disposableFrame) frame.dispose();
		if (ds == null || fs == null) return null;
		if (!fs.toLowerCase().endsWith(suffix.toLowerCase())) fs += suffix;
		return new File((lastSaveDirectory = ds), fs);
	}
	
	public static class CloseMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CloseMenuItem(final Window window) {
			super("Close Window");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				}
			});
		}
	}
	
	public static class ExitMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ExitMenuItem() {
			super("Exit");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.gc();
					for (Window window : Window.getWindows()) {
						if (window.isVisible()) {
							window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
							if (window.isVisible()) return;
						}
					}
					System.exit(0);
				}
			});
		}
	}
	
	public static Object createTransferData(AWTRenderer renderer, KeyCapLayout layout, String format) {
		if (layout == null || layout.isEmpty()) {
			return null;
		} else if ("txt".equalsIgnoreCase(format) || "kkc".equalsIgnoreCase(format)) {
			return layout.toString();
		} else if ("norm.txt".equalsIgnoreCase(format) || "norm.kkc".equalsIgnoreCase(format)) {
			return layout.toNormalizedString();
		} else if ("min.txt".equalsIgnoreCase(format) || "min.kkc".equalsIgnoreCase(format)) {
			return layout.toMinimizedString();
		} else if ("xml".equalsIgnoreCase(format) || "kkcx".equalsIgnoreCase(format)) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			KKCXWriter.write(pw, layout);
			pw.close();
			return sw.toString();
		} else if ("svg".equalsIgnoreCase(format)) {
			if (renderer == null) return null;
			SVGRenderer svg = new SVGRenderer(
				renderer.getKeyCapMold(), renderer.getKeyCapMoldScale(),
				renderer.getKeyCapSize(), null
			);
			return svg.render(layout);
		} else {
			if (renderer == null) return null;
			Rectangle2D bounds = renderer.getBounds(layout);
			int w = (int)Math.round(bounds.getWidth());
			int h = (int)Math.round(bounds.getHeight());
			bounds = new Rectangle(0, 0, w, h);
			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			renderer.paint(g, layout, bounds, false);
			g.dispose();
			return image;
		}
	}
	
	public static void writeTransferData(Object obj, String format, File file) throws IOException {
		if (obj instanceof String) {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);
			pw.println(obj.toString());
			pw.flush();
			pw.close();
		} else if (obj instanceof RenderedImage) {
			ImageIO.write((RenderedImage)obj, format, file);
		} else {
			throw new IOException("Nothing to write");
		}
	}
	
	public static void copyTransferData(Object obj) {
		if (obj instanceof String) {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection sel = new StringSelection(obj.toString());
			cb.setContents(sel, sel);
		} else if (obj instanceof Image) {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			ImageSelection sel = new ImageSelection((Image)obj);
			cb.setContents(sel, sel);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	public static KeyCapLayout paste() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			try {
				Object data = cb.getData(DataFlavor.stringFlavor);
				byte[] bytes = data.toString().getBytes("UTF-8");
				ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				KeyCapLayout layout = KeyCapReader.read("clipboard", in);
				in.close();
				return layout;
			}
			catch (UnsupportedFlavorException e) {}
			catch (IOException e) {}
		}
		Toolkit.getDefaultToolkit().beep();
		return null;
	}
	
	private UIUtilities() {}
}
