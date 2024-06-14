package com.kreative.keycaps;

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
	
	public KeyCap(KeyCapPosition position, KeyCapShape shape, KeyCapLegend legend) {
		this.position = (position != null) ? position : KeyCapPosition.DEFAULT;
		this.shape = (shape != null) ? shape : KeyCapShape.DEFAULT;
		this.legend = (legend != null) ? legend : new KeyCapLegend();
		this.props = new PropertyMap();
	}
	
	public KeyCapPosition getPosition() {
		return this.position;
	}
	
	public void setPosition(KeyCapPosition position) {
		this.position = (position != null) ? position : KeyCapPosition.DEFAULT;
	}
	
	public KeyCapShape getShape() {
		return this.shape;
	}
	
	public void setShape(KeyCapShape shape) {
		this.shape = (shape != null) ? shape : KeyCapShape.DEFAULT;
	}
	
	public KeyCapLegend getLegend() {
		return this.legend;
	}
	
	public void setLegend(KeyCapLegend legend) {
		this.legend = (legend != null) ? legend : new KeyCapLegend();
	}
	
	public PropertyMap getPropertyMap() {
		return this.props;
	}
	
	public String toString() {
		String s = shape.toString() + legend.toString() + props.toString();
		return (s.length() > 0) ? s : "[]";
	}
	
	public String toNormalizedString() {
		String s = shape.toNormalizedString() + legend.toNormalizedString() + props.toString();
		return (s.length() > 0) ? s : "[]";
	}
	
	public String toMinimizedString() {
		String s = shape.toMinimizedString() + legend.toMinimizedString() + props.toString();
		return (s.length() > 0) ? s : "[]";
	}
	
	public boolean isAt(float x, float y, float keyCapSize) {
		int cx = Math.round((position.getX(keyCapSize) - x) * CMP_ROUNDING);
		int cy = Math.round((position.getY(keyCapSize) - y) * CMP_ROUNDING);
		return cx == 0 && cy == 0;
	}
	
	public int compareTo(KeyCap o) {
		int cx = Math.round(position.getX(CMP_ROUNDING) - o.position.getX(CMP_ROUNDING));
		int cy = Math.round(position.getY(CMP_ROUNDING) - o.position.getY(CMP_ROUNDING));
		return (cy != 0) ? -cy : (cx != 0) ? cx : 0;
	}
}
