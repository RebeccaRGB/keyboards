package com.kreative.keycaps;

import java.awt.geom.Point2D;

public class KeyCap implements Comparable<KeyCap> {
	private static final float CMP_ROUNDING = 1000000;
	
	public static KeyCap parse(KeyCapParser p, float x, float y, float keyCapSize) {
		KeyCapShape shape = KeyCapShape.parse(p, keyCapSize);
		KeyCapLegend legend = KeyCapLegend.parse(p);
		KeyCap k = new KeyCap(x, y, keyCapSize, shape, legend);
		k.props.parse(p);
		return k;
	}
	
	public static KeyCap parse(String s, float x, float y, float keyCapSize) {
		KeyCapParser p = new KeyCapParser(s);
		KeyCap k = parse(p, x, y, keyCapSize);
		p.expectEnd();
		return k;
	}
	
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
		int cx = Math.round((this.getX(keyCapSize) - x) * CMP_ROUNDING);
		int cy = Math.round((this.getY(keyCapSize) - y) * CMP_ROUNDING);
		return cx == 0 && cy == 0;
	}
	
	public int compareTo(KeyCap o) {
		int cx = Math.round(this.getX(CMP_ROUNDING) - o.getX(CMP_ROUNDING));
		int cy = Math.round(this.getY(CMP_ROUNDING) - o.getY(CMP_ROUNDING));
		return (cy != 0) ? cy : (cx != 0) ? cx : 0;
	}
}
