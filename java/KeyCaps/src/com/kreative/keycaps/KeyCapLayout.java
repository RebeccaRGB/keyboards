package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class KeyCapLayout extends ArrayList<KeyCap> {
	private static final long serialVersionUID = 1L;
	private static final float DECIMAL_PLACES = 1000;
	
	private final PropertyMap props = new PropertyMap();
	public PropertyMap getPropertyMap() { return this.props; }
	
	public void parse(KeyCapParser p) {
		Point2D.Float loc = new Point2D.Float();
		float keyCapSize = KeyCapUnits.U;
		while (p.hasNext()) {
			if (p.hasNextChar('@')) p.next();
			if (p.hasNextPoint()) {
				loc = p.nextPoint(loc);
				if (p.hasNextUnit()) {
					float u = p.nextUnit(keyCapSize);
					loc.x *= u / keyCapSize;
					loc.y *= u / keyCapSize;
					keyCapSize = u;
				}
			}
			if (p.hasNextChar(':')) p.next();
			while (p.hasNext()) {
				if (p.hasNextChar(';')) { p.next(); break; }
				KeyCap k = KeyCap.parse(p, loc.x, loc.y, keyCapSize);
				loc.x += k.getShape().getAdvanceWidth(keyCapSize);
				add(k);
				if (p.hasNextChar(',')) { p.next(); continue; }
				if (p.hasNextChar(';')) { p.next(); break; }
				throw p.expected(", or ;");
			}
		}
	}
	
	public void parse(String s) {
		KeyCapParser p = new KeyCapParser(s);
		parse(p);
		p.expectEnd();
	}
	
	public Rectangle2D.Float getBounds(float keyCapSize) {
		boolean first = true;
		float minx = 0;
		float miny = 0;
		float maxx = 0;
		float maxy = 0;
		for (KeyCap k : this) {
			Rectangle2D.Float kb = k.getShape().getBounds(keyCapSize);
			float kminx = kb.x + k.getX(keyCapSize);
			float kminy = kb.y - k.getY(keyCapSize);
			float kmaxx = kminx + kb.width;
			float kmaxy = kminy + kb.height;
			if (first) {
				first = false;
				minx = kminx;
				miny = kminy;
				maxx = kmaxx;
				maxy = kmaxy;
			} else {
				if (kminx < minx) minx = kminx;
				if (kminy < miny) miny = kminy;
				if (kmaxx > maxx) maxx = kmaxx;
				if (kmaxy > maxy) maxy = kmaxy;
			}
		}
		return new Rectangle2D.Float(minx, miny, maxx-minx, maxy-miny);
	}
	
	public String toSVG(KeyCapMold cs, float csScale, KeyCapEngraver ce, float keyCapSize) {
		Color tc = cs.getDefaultLegendColor(cs.getDefaultKeyCapColor());
		String tcs = "#" + Integer.toHexString(0xFF000000 | tc.getRGB()).substring(2);
		String vbox = viewBox(getBounds(keyCapSize), 0, DECIMAL_PLACES);
		StringBuffer shapeDefs = new StringBuffer();
		StringBuffer pathDefs = new StringBuffer();
		StringBuffer keyboard = new StringBuffer();
		HashMap<KeyCapShape,Integer> shapes = new HashMap<KeyCapShape,Integer>();
		HashMap<String,Integer> paths = new HashMap<String,Integer>();
		Collections.sort(this);
		for (KeyCap k : this) {
			Integer shapeId = shapes.get(k.getShape());
			if (shapeId == null) {
				shapes.put(k.getShape(), (shapeId = shapes.size()));
				Shape shape = k.getShape().toAWTShape(keyCapSize / csScale);
				shapeDefs.append("<g id=\"shape" + shapeId + "\">\n");
				shapeDefs.append(cs.createLayeredObject(shape, null, null).toSVG("  ", "  "));
				shapeDefs.append("</g>\n");
			}
			keyboard.append("<g class=\"key\">\n");
			String tx = "translate(" + ftoa(+k.getX(keyCapSize), DECIMAL_PLACES) + " " + ftoa(-k.getY(keyCapSize), DECIMAL_PLACES) + ")";
			if (csScale != 1) tx += " scale(" + ftoa(csScale, DECIMAL_PLACES) + " " + ftoa(csScale, DECIMAL_PLACES) + ")";
			keyboard.append("<use xlink:href=\"#shape" + shapeId + "\" transform=\"" + tx + "\"/>\n");
			for (KeyCapEngraver.TextBox tb : ce.makeBoxes(cs, csScale, k.getShape(), keyCapSize, k.getLegend())) {
				if (tb.path != null && tb.path.length() > 0) {
					Integer pathId = paths.get(tb.path);
					if (pathId == null) {
						paths.put(tb.path, (pathId = paths.size()));
						pathDefs.append("<path id=\"path" + pathId + "\" d=\"" + tb.path + "\"/>\n");
					}
					float x = tb.anchor.getX(tb.x, tb.lineHeight) + k.getX(keyCapSize);
					float y = tb.anchor.getY(tb.y, tb.lineHeight) - k.getY(keyCapSize);
					tx = (
						"translate(" + ftoa(x, DECIMAL_PLACES) + " " +
						ftoa(y, DECIMAL_PLACES) + ") scale(" +
						ftoa(tb.lineHeight, DECIMAL_PLACES) + " " +
						ftoa(tb.lineHeight, DECIMAL_PLACES) + ")"
					);
					keyboard.append(
						"<use xlink:href=\"#path" + pathId + "\" transform=\"" +
						tx + "\" fill=\"" + tcs + "\"/>\n"
					);
				} else if (tb.text != null && tb.text.length() > 0) {
					float x = tb.x + k.getX(keyCapSize);
					String a = tb.anchor.getTextAnchor();
					String[] lines = tb.text.split("\r\n|\r|\n");
					for (int i = 0, n = lines.length; i < n; i++) {
						float y = tb.anchor.getY(tb.y, tb.lineHeight*n) + tb.lineHeight*i + tb.lineHeight*0.8f - k.getY(keyCapSize);
						keyboard.append("<text");
						keyboard.append(" x=\"" + ftoa(x, DECIMAL_PLACES) + "\"");
						keyboard.append(" y=\"" + ftoa(y, DECIMAL_PLACES) + "\"");
						keyboard.append(" text-anchor=\"" + a + "\"");
						keyboard.append(" font-family=\"Arial\"");
						keyboard.append(" font-size=\"" + ftoa(tb.lineHeight, DECIMAL_PLACES) + "\"");
						if (stringWidth(lines[i], "Arial", Font.PLAIN, tb.lineHeight) > tb.maxWidth) {
							keyboard.append(" textLength=\"" + ftoa(tb.maxWidth, DECIMAL_PLACES) + "\"");
							keyboard.append(" lengthAdjust=\"spacingAndGlyphs\"");
						}
						keyboard.append(" fill=\"" + tcs + "\"");
						keyboard.append(">" + xmlSpecialChars(lines[i]) + "</text>\n");
					}
				}
			}
			keyboard.append("</g>\n");
		}
		StringBuffer svg = new StringBuffer();
		svg.append("<?xml version=\"1.0\"?>\n");
		svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\"" + vbox + ">\n");
		svg.append("<defs>\n");
		svg.append(shapeDefs.toString());
		svg.append(pathDefs.toString());
		svg.append("</defs>\n");
		svg.append("<g class=\"keyboard\">\n");
		svg.append(keyboard.toString());
		svg.append("</g>\n");
		svg.append("</svg>\n");
		return svg.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(this);
		for (KeyCap k : this) {
			if (sb.length() == 0 || !k.isAt(x, y, keyCapSize)) {
				if (sb.length() > 0) sb.append(";\n");
				keyCapSize = k.getKeyCapSize();
				sb.append('@');
				sb.append(ftoa((x = k.getX(keyCapSize)), DECIMAL_PLACES));
				sb.append(',');
				sb.append(ftoa((y = k.getY(keyCapSize)), DECIMAL_PLACES));
				sb.append(KeyCapUnits.unitToString(keyCapSize, DECIMAL_PLACES));
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		return sb.toString();
	}
	
	public String toNormalizedString() {
		StringBuffer sb = new StringBuffer();
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(this);
		for (KeyCap k : this) {
			if (sb.length() == 0 || !k.isAt(x, y, keyCapSize)) {
				if (sb.length() > 0) sb.append(";\n");
				// keyCapSize = k.getKeyCapSize();
				sb.append('@');
				sb.append(ftoa((x = k.getX(keyCapSize)), DECIMAL_PLACES));
				sb.append(',');
				sb.append(ftoa((y = k.getY(keyCapSize)), DECIMAL_PLACES));
				sb.append(KeyCapUnits.unitToString(keyCapSize, DECIMAL_PLACES));
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toNormalizedString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		return sb.toString();
	}
	
	public String toMinimizedString() {
		StringBuffer sb = new StringBuffer();
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(this);
		for (KeyCap k : this) {
			if (sb.length() == 0 || !k.isAt(x, y, keyCapSize)) {
				if (sb.length() > 0) sb.append(";");
				keyCapSize = k.getMinimalKeyCapSize();
				sb.append('@');
				sb.append(ftoa((x = k.getX(keyCapSize)), DECIMAL_PLACES));
				sb.append(',');
				sb.append(ftoa((y = k.getY(keyCapSize)), DECIMAL_PLACES));
				sb.append(KeyCapUnits.unitToString(keyCapSize, DECIMAL_PLACES));
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toMinimizedString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		return sb.toString();
	}
	
	private static String ftoa(float v, float r) {
		v = Math.round(v * r) / r;
		if (v == (int)v) return Integer.toString((int)v);
		return Float.toString(v);
	}
	
	private static String viewBox(Rectangle2D.Float bounds, float pad, float r) {
		return (
			" width=\"" + ftoa(bounds.width+pad+pad, r) + "\"" +
			" height=\"" + ftoa(bounds.height+pad+pad, r) + "\"" +
			" viewBox=\"" + ftoa(bounds.x-pad, r) + " " + ftoa(bounds.y-pad, r) +
			" " + ftoa(bounds.width+pad+pad, r) + " " + ftoa(bounds.height+pad+pad, r) + "\""
		);
	}
	
	private static String xmlSpecialChars(String s) {
		StringBuffer sb = new StringBuffer();
		int i = 0, n = s.length();
		while (i < n) {
			int ch = s.codePointAt(i);
			switch (ch) {
				case '&': sb.append("&amp;"); break;
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '\"': sb.append("&#34;"); break;
				case '\'': sb.append("&#39;"); break;
				default:
					if (ch >= 0x20 && ch < 0x7F) sb.append((char)ch);
					else sb.append("&#" + ch + ";");
					break;
			}
			i += Character.charCount(ch);
		}
		return sb.toString();
	}
	
	private static float stringWidth(String s, String family, int style, float size) {
		FontRenderContext ctx = new FontRenderContext(null, true, true);
		Font font = new Font(family, style, 1);
		size *= font.getStringBounds(s, ctx).getWidth();
		return size;
	}
}
