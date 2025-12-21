package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class AWTRenderer {
	private final KeyCapMold mold;
	private final float moldScale;
	private final float keyCapSize;
	private final KeyCapEngraver engraver;
	private final int backgroundColor;
	private final boolean showUSBCodes;
	private final Font baseFont;
	private final Font monoFont;
	private final FontRenderContext frc;
	private final AWTShapeDefs shapeDefs;
	private final AWTPathDefs pathDefs;
	
	public AWTRenderer(
		KeyCapMold mold, float moldScale,
		float keyCapSize, KeyCapEngraver e,
		int backgroundColor, boolean showUSBCodes
	) {
		this.mold = mold;
		this.moldScale = moldScale;
		this.keyCapSize = keyCapSize;
		this.engraver = (e != null) ? e : new KeyCapEngraver(mold, moldScale, keyCapSize);
		this.backgroundColor = backgroundColor;
		this.showUSBCodes = showUSBCodes;
		this.baseFont = new Font("SansSerif", Font.PLAIN, 1);
		this.monoFont = new Font("Monospaced", Font.PLAIN, 12);
		this.frc = new FontRenderContext(null, true, true);
		this.shapeDefs = new AWTShapeDefs(mold, moldScale, keyCapSize);
		this.pathDefs = new AWTPathDefs();
	}
	
	public KeyCapMold getKeyCapMold() { return mold; }
	public float getKeyCapMoldScale() { return moldScale; }
	public float getKeyCapSize() { return keyCapSize; }
	public KeyCapEngraver getKeyCapEngraver() { return engraver; }
	public int getBackgroundColor() { return backgroundColor; }
	public boolean getShowUSBCodes() { return showUSBCodes; }
	
	public Rectangle2D.Float getBounds(KeyCapLayout layout) {
		Rectangle2D.Float bounds = layout.getBounds(keyCapSize);
		Padding padding = mold.getPadding();
		bounds.x -= padding.left * moldScale;
		bounds.y -= padding.top * moldScale;
		bounds.width += (padding.left + padding.right) * moldScale;
		bounds.height += (padding.top + padding.bottom) * moldScale;
		return bounds;
	}
	
	public void paint(Graphics2D g, KeyCapLayout layout, Rectangle2D bounds, boolean aspect) {
		Rectangle2D.Float layoutBounds = this.getBounds(layout);
		AffineTransform baseTX = getViewTransform(bounds, layoutBounds, aspect);
		PropertyMap lp = layout.getPropertyMap();
		String lvs = lp.containsAny("vs") ? lp.getString("vs") : null;
		Color lcc = lp.containsAny("cc") ? lp.getColor("cc") : null;
		Float lco = lp.containsAny("co", "cc") ? lp.getOpacity("co", "cc") : null;
		Color llc = lp.containsAny("lc") ? lp.getColor("lc") : null;
		Float llo = lp.containsAny("lo", "lc") ? lp.getOpacity("lo", "lc") : null;
		Float llh = lp.containsAny("lh") ? lp.getFloat("lh") : null;
		Anchor lha = lp.containsAny("ha", "a") ? lp.getAnchor("ha", "a") : null;
		Anchor lva = lp.containsAny("va", "a") ? lp.getAnchor("va", "a") : null;
		
		if (backgroundColor != 0) {
			Color base = g.getColor();
			Color bc = ColorUtilities.getPaletteColor(backgroundColor);
			Float bo = ColorUtilities.getPaletteOpacity(backgroundColor);
			g.setColor(ColorUtilities.overrideColor(base, bc, bo));
			g.fill(bounds);
			g.setColor(base);
		}
		
		for (KeyCap k : layout) {
			PropertyMap kp = k.getPropertyMap();
			String kvs = kp.containsAny("vs") ? kp.getString("vs") : lvs;
			Color kcc = kp.containsAny("cc") ? kp.getColor("cc") : lcc;
			Float kco = kp.containsAny("co", "cc") ? kp.getOpacity("co", "cc") : lco;
			Color klc = kp.containsAny("lc") ? kp.getColor("lc") : llc;
			Float klo = kp.containsAny("lo", "lc") ? kp.getOpacity("lo", "lc") : llo;
			Float klh = kp.containsAny("lh") ? kp.getFloat("lh") : llh;
			Anchor kha = kp.containsAny("ha", "a") ? kp.getAnchor("ha", "a") : lha;
			Anchor kva = kp.containsAny("va", "a") ? kp.getAnchor("va", "a") : lva;
			
			KeyCapShape shape = k.getShape();
			LayeredObject lo = shapeDefs.getLayeredObject(shape, kvs, kcc, kco);
			Point2D.Float pos = k.getPosition().getLocation(keyCapSize);
			AffineTransform loTX = new AffineTransform(baseTX);
			loTX.concatenate(new AffineTransform(moldScale, 0, 0, moldScale, pos.getX(), -pos.getY()));
			lo.paint(g, loTX);
			
			KeyCapLegend legend = k.getLegend();
			PropertyMap jp = legend.getPropertyMap();
			Color jlc = jp.containsAny("lc") ? jp.getColor("lc") : klc;
			Float jlo = jp.containsAny("lo", "lc") ? jp.getOpacity("lo", "lc") : klo;
			Float jlh = jp.containsAny("lh") ? jp.getFloat("lh") : klh;
			Anchor jha = jp.containsAny("ha", "a") ? jp.getAnchor("ha", "a") : kha;
			Anchor jva = jp.containsAny("va", "a") ? jp.getAnchor("va", "a") : kva;
			
			for (KeyCapEngraver.TextBox tb : engraver.makeBoxes(shape, kvs, legend)) {
				if (tb == null || tb.item == null) continue;
				PropertyMap ip = tb.item.getPropertyMap();
				Color ilc = ip.containsAny("lc") ? ip.getColor("lc") : jlc;
				Float ilo = ip.containsAny("lo", "lc") ? ip.getOpacity("lo", "lc") : jlo;
				Float ilh = ip.containsAny("lh") ? ip.getFloat("lh") : jlh;
				Anchor iha = ip.containsAny("ha", "a") ? ip.getAnchor("ha", "a") : jha;
				Anchor iva = ip.containsAny("va", "a") ? ip.getAnchor("va", "a") : jva;
				
				Color base = g.getColor();
				Color tc = textColor(ilc, kcc);
				Float to = (ilo != null && ilo < 1) ? ilo : null;
				g.setColor(ColorUtilities.overrideColor(base, tc, to));
				
				if (tb.path != null && tb.path.length() > 0) {
					Shape path = pathDefs.getPathShape(tb.path);
					float lh = (ilh != null) ? (tb.height * ilh) : tb.lineHeight;
					float x = ((iha != null) ? iha : tb.anchor).getX(tb.x, tb.width, lh) + pos.x;
					float y = ((iva != null) ? iva : tb.anchor).getY(tb.y, tb.height, lh) - pos.y;
					AffineTransform pathTX = new AffineTransform(baseTX);
					pathTX.concatenate(new AffineTransform(lh, 0, 0, lh, x, y));
					g.fill(pathTX.createTransformedShape(path));
				}
				
				if (tb.text != null && tb.text.length() > 0) {
					String[] lines = tb.text.split("\r\n|\r|\n");
					float lh = (ilh != null) ? (tb.height * ilh) : tb.lineHeight;
					float th = lh * lines.length;
					float ta = lh * 0.8f - pos.y;
					float ty = ((iva != null) ? iva : tb.anchor).getY(tb.y, tb.height, th) + ta;
					for (int i = 0; i < lines.length; i++) {
						if (lines[i] == null || lines[i].length() == 0) continue;
						TextLayout tl = new TextLayout(lines[i], baseFont.deriveFont(lh), frc);
						float sw = tl.getAdvance();
						if (sw > tb.width) {
							float sx = tb.width / sw;
							float x = tb.x + pos.x;
							float y = ty + lh * i;
							AffineTransform textTX = new AffineTransform(baseTX);
							textTX.concatenate(new AffineTransform(sx, 0, 0, 1, x, y));
							g.fill(tl.getOutline(textTX));
						} else {
							Anchor a = ((iha != null) ? iha : tb.anchor);
							float x = a.getX(tb.x, tb.width, sw) + pos.x;
							float y = ty + lh * i;
							AffineTransform textTX = new AffineTransform(baseTX);
							textTX.concatenate(new AffineTransform(1, 0, 0, 1, x, y));
							g.fill(tl.getOutline(textTX));
						}
					}
				}
				
				g.setColor(base);
			}
			
			if (showUSBCodes) {
				Integer usb = k.getPropertyMap().getInteger("usb");
				if (usb != null) {
					Color base = g.getColor();
					Color tc = textColor(jlc, kcc);
					Float to = (jlo != null && jlo < 1) ? jlo : null;
					g.setColor(ColorUtilities.overrideColor(base, tc, to));
					
					String h = Integer.toHexString(usb).toUpperCase();
					h = ((h.length() < 2) ? "0x0" : "0x") + h;
					TextLayout tl = new TextLayout(h, monoFont, frc);
					AffineTransform textTX = new AffineTransform(baseTX);
					textTX.concatenate(new AffineTransform(1, 0, 0, 1, pos.x + 4, -pos.y - 4));
					g.fill(tl.getOutline(textTX));
					
					g.setColor(base);
				}
			}
		}
	}
	
	private AffineTransform getViewTransform(Rectangle2D view, Rectangle2D content, boolean aspect) {
		double sx = view.getWidth() / content.getWidth();
		double sy = view.getHeight() / content.getHeight();
		if (aspect) sx = sy = Math.min(sx, sy);
		double cx = content.getX() * sx;
		double cy = content.getY() * sy;
		double cw = content.getWidth() * sx;
		double ch = content.getHeight() * sy;
		// cx, cy, cw, ch is in the view's coordinate system
		double tx = view.getX() + (view.getWidth() - cw) / 2 - cx;
		double ty = view.getY() + (view.getHeight() - ch) / 2 - cy;
		return new AffineTransform(sx, 0, 0, sy, tx, ty);
	}
	
	private Color textColor(Color itemColor, Color capColor) {
		if (itemColor != null) return itemColor;
		if (capColor != null) return mold.getDefaultLegendColor(capColor);
		return mold.getDefaultLegendColor(mold.getDefaultKeyCapColor());
	}
}
