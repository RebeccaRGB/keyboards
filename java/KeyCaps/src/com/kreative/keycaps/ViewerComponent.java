package com.kreative.keycaps;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class ViewerComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final RenderingHints.Key AA_KEY = RenderingHints.KEY_ANTIALIASING;
	private static final Object AA_OFF = RenderingHints.VALUE_ANTIALIAS_OFF;
	private static final Object AA_ON = RenderingHints.VALUE_ANTIALIAS_ON;
	
	private AWTRenderer renderer;
	private KeyCapLayout layout;
	private boolean aspect;
	private boolean aa;
	private int buffered;
	private Dimension prefSize;
	
	public ViewerComponent(AWTRenderer renderer, KeyCapLayout layout) {
		this.renderer = renderer;
		this.layout = layout;
		this.aspect = true;
		this.aa = true;
		Toolkit tk = Toolkit.getDefaultToolkit();
		Object csf = tk.getDesktopProperty("apple.awt.contentScaleFactor");
		if (csf instanceof Number) {
			this.buffered = (int)Math.ceil(((Number)csf).doubleValue());
		} else {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
			AffineTransform tx = gc.getDefaultTransform();
			this.buffered = (int)Math.ceil(Math.max(tx.getScaleX(), tx.getScaleY()));
		}
		this.prefSize = null;
	}
	
	public AWTRenderer getRenderer() { return this.renderer; }
	public void setRenderer(AWTRenderer renderer) { this.renderer = renderer; this.repaint(); }
	public KeyCapLayout getKeyCapLayout() { return this.layout; }
	public void setKeyCapLayout(KeyCapLayout layout) { this.layout = layout; this.repaint(); }
	public boolean getKeepAspectRatio() { return this.aspect; }
	public void setKeepAspectRatio(boolean aspect) { this.aspect = aspect; this.repaint(); }
	public boolean getAntiAlias() { return this.aa; }
	public void setAntiAlias(boolean aa) { this.aa = aa; this.repaint(); }
	public int getDoubleBuffered() { return this.buffered; }
	public void setDoubleBuffered(int buffered) { this.buffered = buffered; this.repaint(); }
	
	public Dimension getPreferredSize() {
		if (this.prefSize != null) return this.prefSize;
		if (renderer != null && layout != null && layout.size() > 0) {
			Rectangle2D bounds = renderer.getBounds(layout);
			int w = (int)Math.round(bounds.getWidth());
			int h = (int)Math.round(bounds.getHeight());
			Insets insets = getInsets();
			int width = w + insets.left + insets.right;
			int height = h + insets.top + insets.bottom;
			return new Dimension(width, height);
		} else {
			Insets insets = getInsets();
			int width = insets.left + insets.right;
			int height = insets.top + insets.bottom;
			return new Dimension(width, height);
		}
	}
	
	public void setPreferredSize(Dimension prefSize) {
		this.prefSize = prefSize;
	}
	
	protected void paintComponent(Graphics g) {
		if (renderer != null && layout != null && layout.size() > 0) {
			Insets insets = getInsets();
			int w = getWidth() - insets.left - insets.right;
			int h = getHeight() - insets.top - insets.bottom;
			if (w > 0 && h > 0) {
				if (buffered > 0) {
					int bw = w * buffered, bh = h * buffered;
					BufferedImage image = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = image.createGraphics();
					g2.setRenderingHint(AA_KEY, aa ? AA_ON : AA_OFF);
					Rectangle bounds = new Rectangle(0, 0, bw, bh);
					renderer.paint(g2, layout, bounds, aspect);
					g2.dispose();
					g.drawImage(image, insets.left, insets.top, w, h, null);
				} else if (g instanceof Graphics2D) {
					Graphics2D g2 = (Graphics2D)g;
					Object saveAA = g2.getRenderingHint(AA_KEY);
					g2.setRenderingHint(AA_KEY, aa ? AA_ON : AA_OFF);
					Rectangle bounds = new Rectangle(insets.left, insets.top, w, h);
					renderer.paint(g2, layout, bounds, aspect);
					g2.setRenderingHint(AA_KEY, saveAA);
				}
			}
		}
	}
}
