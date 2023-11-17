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
	
	private KeyCapPosition position;
	private KeyCapShape shape;
	private KeyCapLegend legend;
	private final PropertyMap props;
	
	public KeyCap(float x, float y, float keyCapSize, KeyCapShape shape, KeyCapLegend legend) {
		this.position = new KeyCapPosition(x, y, keyCapSize);
		this.shape = (shape != null) ? shape : KeyCapShape.DEFAULT;
		this.legend = (legend != null) ? legend : new KeyCapLegend();
		this.props = new PropertyMap();
	}
	
	public KeyCap(float x, float y, float keyCapSize, String shape, String legend) {
		this.position = new KeyCapPosition(x, y, keyCapSize);
		this.shape = (shape != null) ? KeyCapShape.parse(shape, keyCapSize) : KeyCapShape.DEFAULT;
		this.legend = (legend != null) ? KeyCapLegend.parse(legend) : new KeyCapLegend();
		this.props = new PropertyMap();
	}
	
	public KeyCap(float x, float y, float keyCapSize, String spec) {
		this.position = new KeyCapPosition(x, y, keyCapSize);
		Matcher m = SPEC_PATTERN.matcher(spec);
		if (m.matches()) {
			String ss = m.group("s");
			String ls = m.group("l");
			this.shape = (ss != null && ss.length() > 0) ? KeyCapShape.parse(ss, keyCapSize) : KeyCapShape.DEFAULT;
			this.legend = (ls != null && ls.length() > 0) ? KeyCapLegend.parse(ls): new KeyCapLegend();
		} else {
			this.shape = KeyCapShape.DEFAULT;
			this.legend = new KeyCapLegend();
		}
		this.props = new PropertyMap();
	}
	
	public float getX(float keyCapSize) { return position.getX(keyCapSize); }
	public float getY(float keyCapSize) { return position.getY(keyCapSize); }
	public Point2D.Float getLocation(float keyCapSize) { return position.getLocation(keyCapSize); }
	public void setLocation(float x, float y, float keyCapSize) { position = new KeyCapPosition(x, y, keyCapSize); }
	public void setLocation(Point2D p, float keyCapSize) { position = new KeyCapPosition(p, keyCapSize); }
	public float getKeyCapSize() { return position.getKeyCapSize(); }
	public float getMinimalKeyCapSize() { return position.getMinimalKeyCapSize(); }
	public void setKeyCapSize(float keyCapSize) { position = position.setKeyCapSize(keyCapSize); }
	
	public KeyCapPosition getPosition() {
		return this.position;
	}
	
	public void setPosition(KeyCapPosition position) {
		this.position = (position != null) ? position : KeyCapPosition.DEFAULT;
	}
	
	public KeyCapShape getShape() {
		return this.shape;
	}
	
	public void setKeyCapShape(KeyCapShape shape) {
		this.shape = (shape != null) ? shape : KeyCapShape.DEFAULT;
	}
	
	public void setKeyCapShape(String shape) {
		this.shape = (shape != null) ? KeyCapShape.parse(shape, position.getKeyCapSize()) : KeyCapShape.DEFAULT;
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
		int cx = Math.round((this.getX(keyCapSize) - x) * 1000000);
		int cy = Math.round((this.getY(keyCapSize) - y) * 1000000);
		return cx == 0 && cy == 0;
	}
	
	public int compareTo(KeyCap o) {
		int cx = Math.round(this.getX(1000000) - o.getX(1000000));
		int cy = Math.round(this.getY(1000000) - o.getY(1000000));
		return (cy != 0) ? cy : (cx != 0) ? cx : 0;
	}
}
