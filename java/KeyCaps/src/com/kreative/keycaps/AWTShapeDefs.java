package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Shape;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AWTShapeDefs {
	private static final class Key {
		private final KeyCapShape shape;
		private final String vs;
		private final Color color;
		private final Float opacity;
		private Key(KeyCapShape shape, String vs, Color color, Float opacity) {
			this.shape = shape;
			this.vs = vs;
			this.color = color;
			this.opacity = opacity;
		}
		private List<Object> asList() {
			return Arrays.asList(shape, vs, color, opacity);
		}
		public int hashCode() {
			return asList().hashCode();
		}
		public boolean equals(Object o) {
			return (this == o) || (
				(o instanceof Key) &&
				this.asList().equals(((Key)o).asList())
			);
		}
	}
	
	private final KeyCapMold mold;
	private final float moldScale;
	private final float keyCapSize;
	private final Map<Key,LayeredObject> shapes;
	
	public AWTShapeDefs(KeyCapMold mold, float moldScale, float keyCapSize) {
		this.mold = mold;
		this.moldScale = moldScale;
		this.keyCapSize = keyCapSize;
		this.shapes = new HashMap<Key,LayeredObject>();
	}
	
	public LayeredObject getLayeredObject(KeyCapShape shape, String vs, Color color, Float opacity) {
		Key key = new Key(shape, vs, color, opacity);
		LayeredObject lo = shapes.get(key);
		if (lo == null) {
			Shape sh = shape.toAWTShape(keyCapSize / moldScale);
			lo = mold.createLayeredObject(sh, vs, color, opacity);
			shapes.put(key, lo);
		}
		return lo;
	}
}
