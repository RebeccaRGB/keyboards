package com.kreative.keycaps;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyCapShape {
	private static final String SHAPE_TOKEN_STR = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?";
	private static final String UNIT_TOKEN_STR = "([A-Za-z]+|/\\s*(" + SHAPE_TOKEN_STR + "))";
	public static final String PATTERN_STRING = "(" + SHAPE_TOKEN_STR + "\\s*)*" + UNIT_TOKEN_STR;
	
	public static final KeyCapShape DEFAULT = new KeyCapShape(new float[0], KeyCapUnits.U);
	
	public static KeyCapShape parse(KeyCapParser p, float keyCapSize) {
		List<Float> shape = new ArrayList<Float>();
		while (p.hasNextFloat()) shape.add(p.nextFloat());
		if (p.hasNextUnit()) keyCapSize = p.nextUnit(keyCapSize);
		return new KeyCapShape(shape, keyCapSize);
	}
	
	public static KeyCapShape parse(String s, float keyCapSize) {
		KeyCapParser p = new KeyCapParser(s);
		KeyCapShape shape = parse(p, keyCapSize);
		p.expectEnd();
		return shape;
	}
	
	private final float[] shape;
	private final float keyCapSize;
	
	public KeyCapShape(float[] shape, float keyCapSize) {
		this.shape = clone(shape, 1);
		this.keyCapSize = keyCapSize;
	}
	
	public KeyCapShape(Float[] shape, float keyCapSize) {
		this.shape = clone(shape, 1);
		this.keyCapSize = keyCapSize;
	}
	
	public KeyCapShape(List<Float> shape, float keyCapSize) {
		this.shape = clone(shape, 1);
		this.keyCapSize = keyCapSize;
	}
	
	public float[] getShape(float keyCapSize) {
		return clone(this.shape, keyCapSize / this.keyCapSize);
	}
	
	public float getAdvanceWidth(float keyCapSize) {
		if (this.shape == null || this.shape.length == 0) return keyCapSize;
		return this.shape[0] * keyCapSize / this.keyCapSize;
	}
	
	public float getKeyCapSize() {
		return this.keyCapSize;
	}
	
	public float getMinimalKeyCapSize() {
		return KeyCapUnits.minimalUnit(keyCapSize, shape);
	}
	
	public KeyCapShape setKeyCapSize(float keyCapSize) {
		return new KeyCapShape(getShape(keyCapSize), keyCapSize);
	}
	
	public KeyCapShape normalize(boolean strict) {
		return new KeyCapShape(normalize(shape, strict, keyCapSize), keyCapSize);
	}
	
	public KeyCapShape minimize(boolean strict) {
		return new KeyCapShape(minimize(shape, strict, keyCapSize), keyCapSize);
	}
	
	public Rectangle2D.Float getBounds(float keyCapSize) {
		float[] shape = normalize(this.shape, false, this.keyCapSize);
		return getBounds(shape, keyCapSize / this.keyCapSize);
	}
	
	public Shape toAWTShape(float keyCapSize) {
		float[] shape = normalize(this.shape, false, this.keyCapSize);
		return toAWTShape(shape, keyCapSize / this.keyCapSize);
	}
	
	public String toString() {
		if (this.shape == null || this.shape.length == 0) return "";
		String s = KeyCapUnits.valuesToString(null, 1000, this.shape);
		return s + KeyCapUnits.unitToString(this.keyCapSize, 1000);
	}
	
	public String toNormalizedString() {
		return normalize(false).setKeyCapSize(KeyCapUnits.U).toString();
	}
	
	public String toMinimizedString() {
		return minimize(false).setKeyCapSize(getMinimalKeyCapSize()).toString();
	}
	
	public int hashCode() {
		return toNormalizedString().hashCode();
	}
	
	public boolean equals(Object o) {
		return (this == o) || (
			(o instanceof KeyCapShape) &&
			this.toNormalizedString().equals(((KeyCapShape)o).toNormalizedString())
		);
	}
	
	private static float[] clone(float[] shape, float scale) {
		if (shape == null) return null;
		int n = shape.length;
		float[] clone = new float[n];
		for (int i = 0; i < n; i++) clone[i] = shape[i] * scale;
		return clone;
	}
	
	private static float[] clone(Float[] shape, float scale) {
		if (shape == null) return null;
		int n = shape.length;
		float[] clone = new float[n];
		for (int i = 0; i < n; i++) clone[i] = shape[i] * scale;
		return clone;
	}
	
	private static float[] clone(List<Float> shape, float scale) {
		if (shape == null) return null;
		int n = shape.size();
		float[] clone = new float[n];
		for (int i = 0; i < n; i++) clone[i] = shape.get(i) * scale;
		return clone;
	}
	
	private static float[] complete(float[] shape, boolean strict, float u) {
		float cx = 0, cy = 0; int dir = 0;
		for (float c : shape) {
			if (strict && c == 0) throw new IllegalArgumentException("shape contains zero vector");
			switch (dir & 3) {
				case 0: cx += c; break;
				case 1: cy += c; break;
				case 2: cx -= c; break;
				case 3: cy -= c; break;
			}
			dir += (c < 0) ? 1 : 3;
		}
		if (cy == 0) {
			if (cx == 0) return null;
			if (strict) throw new IllegalArgumentException("shape is not closed");
			switch (dir & 3) {
				case 0: return new float[]{ -cx };
				case 2: return new float[]{ +cx };
			}
			if (u == 0) throw new IllegalArgumentException("shape cannot be closed");
			switch (dir & 3) {
				case 1: return new float[]{ -u, +cx, (cx < 0) ? -u : +u };
				case 3: return new float[]{ +u, +cx, (cx < 0) ? -u : +u };
				default: throw new IllegalStateException();
			}
		}
		if (cx == 0) {
			if (cy == 0) return null;
			switch (dir & 3) {
				case 1: return new float[]{ -cy };
				case 3: return new float[]{ +cy };
			}
			if (strict) throw new IllegalArgumentException("shape is not closed");
			if (u == 0) throw new IllegalArgumentException("shape cannot be closed");
			switch (dir & 3) {
				case 0: return new float[]{ -u, -cy, (cy < 0) ? +u : -u };
				case 2: return new float[]{ +u, -cy, (cy < 0) ? +u : -u };
				default: throw new IllegalStateException();
			}
		}
		switch (dir & 3) {
			case 0: return new float[]{ -cx, (cx < 0) ? +cy : -cy };
			case 2: return new float[]{ +cx, (cx < 0) ? +cy : -cy };
			case 1: return new float[]{ -cy/2, (cy < 0) ? -cx : +cx, (cx < 0) ? +cy/2 : -cy/2 };
			case 3: return new float[]{ +cy/2, (cy < 0) ? -cx : +cx, (cx < 0) ? +cy/2 : -cy/2 };
			default: throw new IllegalStateException();
		}
	}
	
	private static float[] normalize(float[] shape, boolean strict, float u) {
		if (shape == null || shape.length == 0) {
			return new float[]{ u, u, u, u };
		} else if (shape.length == 1) {
			return new float[]{ shape[0], u, shape[0], u };
		} else if (shape.length == 2) {
			return new float[]{ shape[0], shape[1], shape[0], shape[1] };
		} else {
			float[] ext = complete(shape, strict, u);
			if (ext == null) return shape;
			float[] newShape = new float[shape.length + ext.length];
			int i = 0;
			for (float c : shape) newShape[i++] = c;
			for (float c : ext) newShape[i++] = c;
			return newShape;
		}
	}
	
	private static float[] minimize(float[] shape, boolean strict, float u) {
		if (shape == null || shape.length == 0) {
			return new float[0];
		} else if (shape.length == 1) {
			if (shape[0] != u) return shape;
			return new float[0];
		} else if (shape.length == 2) {
			if (shape[1] != u) return shape;
			if (shape[0] != u) return new float[]{ shape[0] };
			return new float[0];
		} else {
			float[] normalizedShape = normalize(shape, strict, u);
			while (shape.length > 0) {
				int n = shape.length - 1;
				float[] newShape = new float[n];
				for (int i = 0; i < n; i++) newShape[i] = shape[i];
				try {
					float[] normNewShape = normalize(newShape, strict, u);
					if (!Arrays.equals(normalizedShape, normNewShape)) return shape;
					shape = newShape;
				} catch (IllegalArgumentException e) {
					return shape;
				}
			}
			return new float[0];
		}
	}
	
	private static Rectangle2D.Float getBounds(float[] shape, float scale) {
		if (shape == null || shape.length == 0) return null;
		float minx = 0, maxx = 0, miny = 0, maxy = 0;
		float cx = 0, cy = 0; int dir = 0;
		for (float c : shape) {
			switch (dir & 3) {
				case 0: cx += c; break;
				case 1: cy += c; break;
				case 2: cx -= c; break;
				case 3: cy -= c; break;
			}
			if (cx < minx) minx = cx;
			if (cx > maxx) maxx = cx;
			if (cy < miny) miny = cy;
			if (cy > maxy) maxy = cy;
			dir += (c < 0) ? 1 : 3;
		}
		return new Rectangle2D.Float(
			minx * scale,
			miny * scale,
			(maxx - minx) * scale,
			(maxy - miny) * scale
		);
	}
	
	private static Shape toAWTShape(float[] shape, float scale) {
		if (shape == null || shape.length == 0) return null;
		GeneralPath path = new GeneralPath(); path.moveTo(0, 0);
		float cx = 0, cy = 0; int dir = 0;
		for (float c : shape) {
			switch (dir & 3) {
				case 0: cx += c; break;
				case 1: cy += c; break;
				case 2: cx -= c; break;
				case 3: cy -= c; break;
			}
			path.lineTo(cx * scale, cy * scale);
			dir += (c < 0) ? 1 : 3;
		}
		if (cx == 0 && cy == 0) path.closePath();
		return path;
	}
}
