package com.kreative.keycaps;

import java.awt.geom.Point2D;

public class KeyCapPosition {
	public static final KeyCapPosition DEFAULT = new KeyCapPosition(0, 0, KeyCapUnits.U);
	
	private final float x;
	private final float y;
	private final float keyCapSize;
	
	public KeyCapPosition(float x, float y, float keyCapSize) {
		this.x = x;
		this.y = y;
		this.keyCapSize = keyCapSize;
	}
	
	public KeyCapPosition(Point2D p, float keyCapSize) {
		this.x = (float)p.getX();
		this.y = (float)p.getY();
		this.keyCapSize = keyCapSize;
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
	
	public float getKeyCapSize() {
		return this.keyCapSize;
	}
	
	public float getMinimalKeyCapSize() {
		return KeyCapUnits.minimalUnit(this.keyCapSize, this.x, this.y);
	}
	
	public KeyCapPosition setKeyCapSize(float keyCapSize) {
		return new KeyCapPosition(
			this.x * keyCapSize / this.keyCapSize,
			this.y * keyCapSize / this.keyCapSize,
			keyCapSize
		);
	}
	
	public String toString() {
		String s = KeyCapUnits.valuesToString(",", this.x, this.y);
		return s + KeyCapUnits.unitToString(this.keyCapSize);
	}
	
	public String toNormalizedString() {
		return setKeyCapSize(KeyCapUnits.U).toString();
	}
	
	public String toMinimizedString() {
		return setKeyCapSize(getMinimalKeyCapSize()).toString();
	}
	
	public int hashCode() {
		return toNormalizedString().hashCode();
	}
	
	public boolean equals(Object o) {
		return (this == o) || (
			(o instanceof KeyCapPosition) &&
			this.toNormalizedString().equals(((KeyCapPosition)o).toNormalizedString())
		);
	}
}
