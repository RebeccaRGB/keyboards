package com.kreative.keycaps;

import java.awt.geom.Point2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyCap implements Comparable<KeyCap> {
	private static final Pattern SPEC_PATTERN = Pattern.compile(
		"(?<s>" + KeyCapShape.PATTERN_STRING + "\\s*)?" +
		"(?<l>" + KeyCapLegend.PATTERN_STRING + ")?"
	);
	public static final String SPEC_PATTERN_STRING = (
		"(" + KeyCapShape.PATTERN_STRING + "\\s*)?" +
		"(" + KeyCapLegend.PATTERN_STRING + ")?"
	);
	private static final float DECIMAL_PLACES = 1000;
	
	private float x;
	private float y;
	private float keyCapSize;
	private KeyCapShape shape;
	private KeyCapLegend legend;
	private final PropertyMap props;
	
	public KeyCap(float x, float y, float keyCapSize, KeyCapShape shape, KeyCapLegend legend) {
		this.x = x;
		this.y = y;
		this.keyCapSize = keyCapSize;
		this.shape = (shape != null) ? shape : KeyCapShape.DEFAULT;
		this.legend = (legend != null) ? legend : new KeyCapLegend();
		this.props = new PropertyMap();
	}
	
	public KeyCap(float x, float y, float keyCapSize, String shape, String legend) {
		this.x = x;
		this.y = y;
		this.keyCapSize = keyCapSize;
		this.shape = (shape != null) ? new KeyCapShape(shape, keyCapSize) : KeyCapShape.DEFAULT;
		this.legend = (legend != null) ? KeyCapLegend.parse(legend) : new KeyCapLegend();
		this.props = new PropertyMap();
	}
	
	public KeyCap(float x, float y, float keyCapSize, String spec) {
		this.x = x;
		this.y = y;
		this.keyCapSize = keyCapSize;
		Matcher m = SPEC_PATTERN.matcher(spec);
		if (m.matches()) {
			String ss = m.group("s");
			String ls = m.group("l");
			this.shape = (ss != null && ss.length() > 0) ? new KeyCapShape(ss, keyCapSize) : KeyCapShape.DEFAULT;
			this.legend = (ls != null && ls.length() > 0) ? KeyCapLegend.parse(ls): new KeyCapLegend();
		} else {
			this.shape = KeyCapShape.DEFAULT;
			this.legend = new KeyCapLegend();
		}
		this.props = new PropertyMap();
	}
	
	public float getX(float keyCapSize) {
		return this.x * keyCapSize / this.keyCapSize;
	}
	
	public float getY(float keyCapSize) {
		return this.y * keyCapSize / this.keyCapSize;
	}
	
	public Point2D.Float getLocation(float keyCapSize) {
		return new Point2D.Float(
			this.x * keyCapSize / this.keyCapSize,
			this.y * keyCapSize / this.keyCapSize
		);
	}
	
	public void setLocation(float x, float y, float keyCapSize) {
		this.x = x;
		this.y = y;
		this.keyCapSize = keyCapSize;
	}
	
	public void setLocation(Point2D p, float keyCapSize) {
		this.x = (float)p.getX();
		this.y = (float)p.getY();
		this.keyCapSize = keyCapSize;
	}
	
	public float getKeyCapSize() {
		return this.keyCapSize;
	}
	
	public float getMinimalKeyCapSize() {
		if (oopsAllInt(getX(KeyCapShape.U), getY(KeyCapShape.U))) return KeyCapShape.U;
		if (oopsAllInt(getX(KeyCapShape.V), getY(KeyCapShape.V))) return KeyCapShape.V;
		if (oopsAllInt(getX(KeyCapShape.W), getY(KeyCapShape.W))) return KeyCapShape.W;
		if (oopsAllInt(getX(KeyCapShape.IN), getY(KeyCapShape.IN))) return KeyCapShape.IN;
		if (oopsAllInt(getX(KeyCapShape.MM), getY(KeyCapShape.MM))) return KeyCapShape.MM;
		if (oopsAllInt(getX(KeyCapShape.PT), getY(KeyCapShape.PT))) return KeyCapShape.PT;
		return this.keyCapSize;
	}
	
	public void setKeyCapSize(float keyCapSize) {
		this.x *= keyCapSize / this.keyCapSize;
		this.y *= keyCapSize / this.keyCapSize;
		this.keyCapSize = keyCapSize;
	}
	
	public KeyCapShape getShape() {
		return this.shape;
	}
	
	public void setKeyCapShape(KeyCapShape shape) {
		this.shape = (shape != null) ? shape : KeyCapShape.DEFAULT;
	}
	
	public void setKeyCapShape(String shape) {
		this.shape = (shape != null) ? new KeyCapShape(shape, keyCapSize) : KeyCapShape.DEFAULT;
	}
	
	public KeyCapLegend getLegend() {
		return this.legend;
	}
	
	public void setKeyCapLegend(KeyCapLegend legend) {
		this.legend = (legend != null) ? legend : new KeyCapLegend();
	}
	
	public void setKeyCapLegend(String legend) {
		this.legend = (legend != null) ? KeyCapLegend.parse(legend) : new KeyCapLegend();
	}
	
	public PropertyMap getPropertyMap() {
		return this.props;
	}
	
	public String toString() {
		return shape.toString() + legend.toString();
	}
	
	public String toNormalizedString() {
		return shape.toNormalizedString() + legend.toNormalizedString();
	}
	
	public String toMinimizedString() {
		return shape.toMinimizedString() + legend.toMinimizedString();
	}
	
	public boolean isAt(float x, float y, float keyCapSize) {
		int cx = Math.round((this.getX(keyCapSize) - x) * DECIMAL_PLACES);
		int cy = Math.round((this.getY(keyCapSize) - y) * DECIMAL_PLACES);
		return cx == 0 && cy == 0;
	}
	
	public int compareTo(KeyCap o) {
		int cx = Math.round(this.getX(DECIMAL_PLACES) - o.getX(DECIMAL_PLACES));
		int cy = Math.round(this.getY(DECIMAL_PLACES) - o.getY(DECIMAL_PLACES));
		return (cy != 0) ? cy : (cx != 0) ? cx : 0;
	}
	
	private static boolean oopsAllInt(float... values) {
		for (float v : values) if (v != (int)v) return false;
		return true;
	}
}
