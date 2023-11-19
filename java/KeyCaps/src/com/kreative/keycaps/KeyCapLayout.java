package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.colorToString;
import static com.kreative.keycaps.KeyCapUnits.ROUNDING;
import static com.kreative.keycaps.KeyCapUnits.valueToString;
import static com.kreative.keycaps.ShapeUtilities.toSVGViewBox;
import static com.kreative.keycaps.StringUtilities.stringWidth;
import static com.kreative.keycaps.StringUtilities.xmlSpecialChars;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

public class KeyCapLayout extends ArrayList<KeyCap> {
	private static final long serialVersionUID = 1L;
	
	private final PropertyMap props = new PropertyMap();
	public PropertyMap getPropertyMap() { return this.props; }
	
	public void parse(KeyCapParser p) {
		Point2D.Float loc = new Point2D.Float();
		float keyCapSize = KeyCapUnits.U;
		this.props.parse(p);
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
				this.add(k);
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
			float kminx = kb.x + k.getPosition().getX(keyCapSize);
			float kminy = kb.y - k.getPosition().getY(keyCapSize);
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
		String tcs = colorToString(cs.getDefaultLegendColor(cs.getDefaultKeyCapColor()), "black");
		String vbox = toSVGViewBox(getBounds(keyCapSize), 0, ROUNDING);
		SVGShapeDefs shapeDefs = new SVGShapeDefs(cs, csScale, keyCapSize);
		SVGPathDefs pathDefs = new SVGPathDefs();
		StringBuffer keyboard = new StringBuffer();
		Collections.sort(this);
		for (KeyCap k : this) {
			String shapeID = shapeDefs.getShapeID(k.getShape(), null, null);
			keyboard.append("<g class=\"key\">\n");
			String tx = "translate(" + valueToString(+k.getPosition().getX(keyCapSize)) + " " + valueToString(-k.getPosition().getY(keyCapSize)) + ")";
			if (csScale != 1) tx += " scale(" + valueToString(csScale) + " " + valueToString(csScale) + ")";
			keyboard.append("<use xlink:href=\"#" + shapeID + "\" transform=\"" + tx + "\"/>\n");
			for (KeyCapEngraver.TextBox tb : ce.makeBoxes(cs, csScale, k.getShape(), keyCapSize, k.getLegend())) {
				if (tb.path != null && tb.path.length() > 0) {
					String pathID = pathDefs.getPathID(tb.path);
					float x = tb.anchor.getX(tb.x, tb.lineHeight) + k.getPosition().getX(keyCapSize);
					float y = tb.anchor.getY(tb.y, tb.lineHeight) - k.getPosition().getY(keyCapSize);
					tx = (
						"translate(" + valueToString(x) + " " +
						valueToString(y) + ") scale(" +
						valueToString(tb.lineHeight) + " " +
						valueToString(tb.lineHeight) + ")"
					);
					keyboard.append(
						"<use xlink:href=\"#" + pathID + "\" transform=\"" +
						tx + "\" fill=\"" + tcs + "\"/>\n"
					);
				} else if (tb.text != null && tb.text.length() > 0) {
					float x = tb.x + k.getPosition().getX(keyCapSize);
					String a = tb.anchor.getTextAnchor();
					String[] lines = tb.text.split("\r\n|\r|\n");
					for (int i = 0, n = lines.length; i < n; i++) {
						float y = tb.anchor.getY(tb.y, tb.lineHeight*n) + tb.lineHeight*i + tb.lineHeight*0.8f - k.getPosition().getY(keyCapSize);
						keyboard.append("<text");
						keyboard.append(" x=\"" + valueToString(x) + "\"");
						keyboard.append(" y=\"" + valueToString(y) + "\"");
						keyboard.append(" text-anchor=\"" + a + "\"");
						keyboard.append(" font-family=\"Arial\"");
						keyboard.append(" font-size=\"" + valueToString(tb.lineHeight) + "\"");
						if (stringWidth(lines[i], "Arial", Font.PLAIN, tb.lineHeight) > tb.maxWidth) {
							keyboard.append(" textLength=\"" + valueToString(tb.maxWidth) + "\"");
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
				KeyCapPosition p = k.getPosition();
				keyCapSize = p.getKeyCapSize();
				x = p.getX(keyCapSize);
				y = p.getY(keyCapSize);
				sb.append('@');
				sb.append(p.toString());
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		else return props.toString();
		if (props.isEmpty()) return sb.toString();
		return props.toString() + "\n" + sb.toString();
	}
	
	public String toNormalizedString() {
		StringBuffer sb = new StringBuffer();
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(this);
		for (KeyCap k : this) {
			if (sb.length() == 0 || !k.isAt(x, y, keyCapSize)) {
				if (sb.length() > 0) sb.append(";\n");
				KeyCapPosition p = k.getPosition();
				x = p.getX(keyCapSize);
				y = p.getY(keyCapSize);
				sb.append('@');
				sb.append(p.toNormalizedString());
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toNormalizedString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		else return props.toString();
		if (props.isEmpty()) return sb.toString();
		return props.toString() + "\n" + sb.toString();
	}
	
	public String toMinimizedString() {
		StringBuffer sb = new StringBuffer();
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(this);
		for (KeyCap k : this) {
			if (sb.length() == 0 || !k.isAt(x, y, keyCapSize)) {
				if (sb.length() > 0) sb.append(";");
				KeyCapPosition p = k.getPosition();
				keyCapSize = p.getMinimalKeyCapSize();
				x = p.getX(keyCapSize);
				y = p.getY(keyCapSize);
				sb.append('@');
				sb.append(p.toMinimizedString());
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toMinimizedString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		else return props.toString();
		if (props.isEmpty()) return sb.toString();
		return props.toString() + "\n" + sb.toString();
	}
}
