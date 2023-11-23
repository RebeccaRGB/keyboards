package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Shape;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SVGShapeDefs {
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
	private final Map<Key,String> shapeIDs;
	private final StringBuffer defs;
	
	public SVGShapeDefs(KeyCapMold mold, float moldScale, float keyCapSize) {
		this.mold = mold;
		this.moldScale = moldScale;
		this.keyCapSize = keyCapSize;
		this.shapeIDs = new HashMap<Key,String>();
		this.defs = new StringBuffer();
	}
	
	public String getShapeID(KeyCapShape shape, String vs, Color color, Float opacity) {
		Key key = new Key(shape, vs, color, opacity);
		String id = shapeIDs.get(key);
		if (id == null) {
			shapeIDs.put(key, (id = "shape" + shapeIDs.size()));
			Shape sh = shape.toAWTShape(keyCapSize / moldScale);
			LayeredObject lo = mold.createLayeredObject(sh, vs, color, opacity);
			String svg = lo.toSVG("", "", KeyCapUnits.ROUNDING).trim();
			if (svg.startsWith("<g>")) {
				defs.append("<g id=\"" + id + "\">" + svg.substring(3) + "\n");
			} else {
				defs.append("<g id=\"" + id + "\">\n" + svg + "\n</g>\n");
			}
		}
		return id;
	}
	
	public String toString() {
		return defs.toString();
	}
}
