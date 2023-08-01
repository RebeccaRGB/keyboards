package com.kreative.keycaps;

import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public abstract class KeyCapEngraver {
	public static enum Anchor {
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
	
	public static class TextBox {
		public final float x;
		public final float y;
		public final float lineHeight;
		public final Anchor anchor;
		public final String text;
		public final String path;
		private TextBox(float x, float y, float lineHeight, Anchor anchor, String text, String path) {
			this.x = x;
			this.y = y;
			this.lineHeight = lineHeight;
			this.anchor = anchor;
			this.text = text;
			this.path = path;
		}
	}
	
	public abstract TextBox[] makeBoxes(KeyCapStyle style, float styleScale, KeyCapShape shape, float keyCapSize, KeyCapLegend legend);
	
	public static final KeyCapEngraver DEFAULT = new KeyCapEngraver() {
		public TextBox[] makeBoxes(KeyCapStyle style, float styleScale, KeyCapShape shape, float keyCapSize, KeyCapLegend legend) {
			float baseLineHeight = keyCapSize;
			Rectangle2D.Float bbx = shape.getBounds(keyCapSize);
			if (bbx.height > keyCapSize) {
				if (bbx.width > shape.getAdvanceWidth(keyCapSize)) {
					bbx.height /= 2;
				} else {
					bbx.y += bbx.height / 2;
					bbx.height /= 2;
				}
			}
			Insets insets = style.getInsets();
			bbx.x += insets.left * styleScale;
			bbx.y += insets.top * styleScale;
			bbx.width -= (insets.left + insets.right) * styleScale;
			bbx.height -= (insets.top + insets.bottom) * styleScale;
			baseLineHeight -= (insets.top + insets.bottom) * styleScale;
			ArrayList<TextBox> boxes = new ArrayList<TextBox>();
			switch (legend.getType()) {
				case F:
					boxes.add(new TextBox(bbx.x+bbx.width*0.5f, bbx.y+bbx.height*0.5f, baseLineHeight/3, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					break;
				case G:
					boxes.add(new TextBox(bbx.x+bbx.width*0.5f, bbx.y+bbx.height*0.75f, baseLineHeight/3, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.5f, bbx.y+bbx.height*0.25f, baseLineHeight/3, Anchor.CENTER, legend.getText(1), legend.getPath(1)));
					break;
				case L:
					boxes.add(new TextBox(bbx.x+bbx.width*0.5f, bbx.y+bbx.height*0.5f, baseLineHeight/2, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					break;
				case S:
					boxes.add(new TextBox(bbx.x+bbx.width*0.5f, bbx.y+bbx.height*0.75f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.5f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(1), legend.getPath(1)));
					break;
				case A:
					boxes.add(new TextBox(bbx.x+bbx.width*0.25f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.75f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(1), legend.getPath(1)));
					break;
				case T:
					boxes.add(new TextBox(bbx.x+bbx.width*0.25f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.75f, bbx.y+bbx.height*0.75f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(1), legend.getPath(1)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.75f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(2), legend.getPath(2)));
					break;
				case Z:
					boxes.add(new TextBox(bbx.x+bbx.width*0.25f, bbx.y+bbx.height*0.75f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.25f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(1), legend.getPath(1)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.75f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(2), legend.getPath(2)));
					break;
				case Q:
					boxes.add(new TextBox(bbx.x+bbx.width*0.25f, bbx.y+bbx.height*0.75f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.25f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(1), legend.getPath(1)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.75f, bbx.y+bbx.height*0.75f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(2), legend.getPath(2)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.75f, bbx.y+bbx.height*0.25f, baseLineHeight/2.5f, Anchor.CENTER, legend.getText(3), legend.getPath(3)));
					break;
				case N:
					boxes.add(new TextBox(bbx.x+bbx.width*0.5f, bbx.y+bbx.height*0.3f, baseLineHeight/2, Anchor.CENTER, legend.getText(0), legend.getPath(0)));
					boxes.add(new TextBox(bbx.x+bbx.width*0.5f, bbx.y+bbx.height*0.8f, baseLineHeight/3, Anchor.CENTER, legend.getText(1), legend.getPath(1)));
					break;
				case NONE:
					break;
			}
			return boxes.toArray(new TextBox[boxes.size()]);
		}
	};
}
