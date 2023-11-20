package com.kreative.keycaps;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public enum Anchor {
	CENTER("5", "c", "center", "centre", "m", "mid", "middle"),
	NORTHWEST("7", "nw", "northwest", "ul", "upperleft", "tl", "topleft"),
	NORTH("8", "n", "north", "u", "up", "upper", "t", "top"),
	NORTHEAST("9", "ne", "northeast", "ur", "upperright", "tr", "topright"),
	EAST("6", "e", "east", "r", "right"),
	SOUTHEAST("3", "se", "southeast", "lr", "lowerright", "br", "bottomright"),
	SOUTH("2", "s", "south", "d", "down", "lower", "b", "bottom"),
	SOUTHWEST("1", "sw", "southwest", "ll", "lowerleft", "bl", "bottomleft"),
	WEST("4", "w", "west", "l", "left");
	
	private final String[] names;
	private Anchor(String... names) {
		this.names = names;
	}
	
	private static final Map<String,Anchor> map = new HashMap<String,Anchor>();
	static {
		for (Anchor a : values()) {
			for (String name : a.names) {
				map.put(name, a);
			}
		}
	}
	
	public static Anchor parseAnchor(String name) {
		if (name == null) return null;
		return map.get(name.trim().toLowerCase());
	}
	
	public final float getX(float bx, float bw, float cw) {
		switch (this) {
			case NORTHWEST: case WEST: case SOUTHWEST: return bx;
			case NORTH: case CENTER: case SOUTH: return bx + (bw - cw) / 2;
			case NORTHEAST: case EAST: case SOUTHEAST: return bx + (bw - cw);
			default: throw new IllegalStateException();
		}
	}
	
	public final float getY(float by, float bh, float ch) {
		switch (this) {
			case NORTHWEST: case NORTH: case NORTHEAST: return by;
			case WEST: case CENTER: case EAST: return by + (bh - ch) / 2;
			case SOUTHWEST: case SOUTH: case SOUTHEAST: return by + (bh - ch);
			default: throw new IllegalStateException();
		}
	}
	
	public final Rectangle2D.Float divide(Rectangle2D r, float sw, float sh) {
		if (r == null) return null;
		float x = getX((float)r.getX(), (float)r.getWidth(), sw *= r.getWidth());
		float y = getY((float)r.getY(), (float)r.getHeight(), sh *= r.getHeight());
		return new Rectangle2D.Float(x, y, sw, sh);
	}
	
	public final String getTextAnchor() {
		switch (this) {
			case NORTHWEST: case WEST: case SOUTHWEST: return "start";
			case NORTH: case CENTER: case SOUTH: return "middle";
			case NORTHEAST: case EAST: case SOUTHEAST: return "end";
			default: throw new IllegalStateException();
		}
	}
}
