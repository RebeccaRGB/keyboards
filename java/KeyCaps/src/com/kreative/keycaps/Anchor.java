package com.kreative.keycaps;

public enum Anchor {
	CENTER, NORTHWEST, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST;
	
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
