package com.kreative.keycaps;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public abstract class KeyCapEngraver {
	public static class TextBox {
		public final float x;
		public final float y;
		public final float maxWidth;
		public final float lineHeight;
		public final Anchor anchor;
		public final KeyCapLegendItem item;
		public final String text;
		public final String path;
		private TextBox(float x, float y, float maxWidth, float lineHeight, Anchor anchor, KeyCapLegendItem item) {
			this.x = x;
			this.y = y;
			this.maxWidth = maxWidth;
			this.lineHeight = lineHeight;
			this.anchor = anchor;
			this.item = item;
			this.text = (item != null) ? item.getText() : null;
			this.path = (item != null) ? item.getPath() : null;
		}
	}
	
	public abstract TextBox[] makeBoxes(KeyCapMold mold, float styleScale, KeyCapShape shape, float keyCapSize, KeyCapLegend legend);
	
	public static final KeyCapEngraver DEFAULT = new KeyCapEngraver() {
		public TextBox[] makeBoxes(KeyCapMold mold, float styleScale, KeyCapShape shape, float keyCapSize, KeyCapLegend legend) {
			Shape cs = shape.toAWTShape(keyCapSize);
			Shape ts = mold.createTopTextArea(cs);
			Rectangle2D cbox = ShapeUtilities.getWidestRect(cs, null);
			Rectangle2D tbox = ShapeUtilities.getWidestRect(ts, null);
			float lh = keyCapSize - (float)(cbox.getHeight() - tbox.getHeight());
			float x = (float)tbox.getX(), y = (float)tbox.getY();
			float w = (float)tbox.getWidth(), h = (float)tbox.getHeight();
			ArrayList<TextBox> boxes = new ArrayList<TextBox>();
			switch (legend.getType()) {
				case F:
					boxes.add(new TextBox(x+w*0.5f, y+h*0.5f, w, lh/3, Anchor.CENTER, legend.getItem(0)));
					break;
				case G:
					boxes.add(new TextBox(x+w*0.5f, y+h*0.75f, w, lh/3, Anchor.CENTER, legend.getItem(0)));
					boxes.add(new TextBox(x+w*0.5f, y+h*0.25f, w, lh/3, Anchor.CENTER, legend.getItem(1)));
					break;
				case L:
					boxes.add(new TextBox(x+w*0.5f, y+h*0.5f, w, lh/2, Anchor.CENTER, legend.getItem(0)));
					break;
				case S:
					boxes.add(new TextBox(x+w*0.5f, y+h*0.75f, w, lh/2.5f, Anchor.CENTER, legend.getItem(0)));
					boxes.add(new TextBox(x+w*0.5f, y+h*0.25f, w, lh/2.5f, Anchor.CENTER, legend.getItem(1)));
					break;
				case A:
					boxes.add(new TextBox(x+w*0.25f, y+h*0.25f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(0)));
					boxes.add(new TextBox(x+w*0.75f, y+h*0.25f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(1)));
					break;
				case T:
					boxes.add(new TextBox(x+w*0.25f, y+h*0.25f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(0)));
					boxes.add(new TextBox(x+w*0.75f, y+h*0.75f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(1)));
					boxes.add(new TextBox(x+w*0.75f, y+h*0.25f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(2)));
					break;
				case Z:
					boxes.add(new TextBox(x+w*0.25f, y+h*0.75f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(0)));
					boxes.add(new TextBox(x+w*0.25f, y+h*0.25f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(1)));
					boxes.add(new TextBox(x+w*0.75f, y+h*0.25f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(2)));
					break;
				case Q:
					boxes.add(new TextBox(x+w*0.25f, y+h*0.75f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(0)));
					boxes.add(new TextBox(x+w*0.25f, y+h*0.25f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(1)));
					boxes.add(new TextBox(x+w*0.75f, y+h*0.75f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(2)));
					boxes.add(new TextBox(x+w*0.75f, y+h*0.25f, w*0.5f, lh/2.5f, Anchor.CENTER, legend.getItem(3)));
					break;
				case N:
					boxes.add(new TextBox(x+w*0.5f, y+h*0.3f, w, lh/2, Anchor.CENTER, legend.getItem(0)));
					boxes.add(new TextBox(x+w*0.5f, y+h*0.8f, w, lh/3, Anchor.CENTER, legend.getItem(1)));
					break;
				case NONE:
					break;
			}
			return boxes.toArray(new TextBox[boxes.size()]);
		}
	};
}
