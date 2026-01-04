package com.kreative.keycaps;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class KeyCapVariants {
	public static final String STEPPED = "s";
	public static final String STEPPED_CAPS_CTRL = "sc";
	public static final String STEPPED_BACKSPACE = "sb";
	public static final String STEPPED_ENTER = "se";
	public static final String RECESSED = "r";
	public static final String OBLIQUE = "i";
	public static final String EXTREME_OBLIQUE = "ii";
	
	public static Shape getActiveArea(Shape shape, List<String> vss) {
		if (shape == null || vss == null || vss.isEmpty()) return shape;
		if (vss.contains(STEPPED)) {
			Rectangle2D r = ShapeUtilities.getWidestRect(shape, null);
			double s = Math.min(r.getWidth(), r.getHeight());
			return new Rectangle2D.Double(r.getCenterX() - s/2, r.getCenterY() - s/2, s, s);
		}
		if (vss.contains(STEPPED_CAPS_CTRL)) {
			Rectangle2D r = ShapeUtilities.getWidestRect(shape, null);
			double s = Math.min(r.getWidth(), r.getHeight());
			return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth() - s/2, r.getHeight());
		}
		if (vss.contains(STEPPED_BACKSPACE)) {
			Rectangle2D r = ShapeUtilities.getWidestRect(shape, null);
			double s = Math.min(r.getWidth(), r.getHeight());
			return new Rectangle2D.Double(r.getX() + s/2, r.getY(), r.getWidth() - s/2, r.getHeight());
		}
		if (vss.contains(STEPPED_ENTER)) {
			return ShapeUtilities.getTallestRect(shape, null);
		}
		return shape;
	}
	
	public static Shape applyTransformations(Shape shape, List<String> vss) {
		if (shape == null || vss == null || vss.isEmpty()) return shape;
		for (String vs : vss) {
			if (vs.equals(OBLIQUE)) {
				shape = AffineTransform.getShearInstance(-0.25, 0).createTransformedShape(shape);
			}
			if (vs.equals(EXTREME_OBLIQUE)) {
				shape = AffineTransform.getShearInstance(-1, 0).createTransformedShape(shape);
			}
		}
		return shape;
	}
	
	private KeyCapVariants() {}
}
