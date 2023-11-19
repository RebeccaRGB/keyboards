package com.kreative.keycaps;

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
