package com.kreative.keycaps;

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
	
	public final float getX(float x, float width) {
		switch (this) {
			case NORTHWEST: case WEST: case SOUTHWEST: return x;
			case NORTH: case CENTER: case SOUTH: return x - width / 2;
			case NORTHEAST: case EAST: case SOUTHEAST: return x - width;
			default: throw new IllegalStateException();
		}
	}
	
	public final float getY(float y, float height) {
		switch (this) {
			case NORTHWEST: case NORTH: case NORTHEAST: return y;
			case WEST: case CENTER: case EAST: return y - height / 2;
			case SOUTHWEST: case SOUTH: case SOUTHEAST: return y - height;
			default: throw new IllegalStateException();
		}
	}
	
	public final String getTextAnchor() {
		switch (this) {
			case NORTHWEST: case WEST: case SOUTHWEST: return "left";
			case NORTH: case CENTER: case SOUTH: return "middle";
			case NORTHEAST: case EAST: case SOUTHEAST: return "right";
			default: throw new IllegalStateException();
		}
	}
}
