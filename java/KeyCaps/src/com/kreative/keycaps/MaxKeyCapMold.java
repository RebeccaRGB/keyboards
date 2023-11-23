package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.getPaletteColor;
import static com.kreative.keycaps.ColorUtilities.multiplyAdd;
import static com.kreative.keycaps.ShapeUtilities.contract;
import static com.kreative.keycaps.ShapeUtilities.roundCorners;
import static com.kreative.keycaps.ShapeUtilities.subtract;
import static com.kreative.keycaps.ShapeUtilities.translate;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class MaxKeyCapMold extends KeyCapMold {
	public Color getDefaultKeyCapColor() {
		return getPaletteColor(31);
	}
	
	private float maxBorder(Shape shape) {
		Rectangle2D bounds = shape.getBounds2D();
		double size = Math.min(bounds.getWidth(), bounds.getHeight());
		float border = Math.round(size / 4);
		return (border < 6) ? 6 : (border > 12) ? 12 : border;
	}
	
	private boolean squareTop(String vs) {
		if (vs == null || (vs = vs.trim()).length() == 0) return false;
		return Arrays.asList(vs.split("\\s+")).contains("s");
	}
	
	private Shape squareTop(Shape shape) {
		Rectangle2D r = ShapeUtilities.getWidestRect(shape, null);
		double s = Math.min(r.getWidth(), r.getHeight());
		return new Rectangle2D.Double(r.getCenterX() - s/2, r.getCenterY() - s/2, s, s);
	}
	
	public LayeredObject createLayeredObject(Shape shape, String vs, Color color, Float opacity) {
		float b = maxBorder(shape);
		Shape s0 = roundCorners(shape, 6);
		Shape s1 = roundCorners(contract(shape, 1), 5);
		Shape s2 = roundCorners(contract(shape, 2), 4);
		if (squareTop(vs)) shape = squareTop(shape);
		Shape s3 = translate(roundCorners(contract(shape, b-3), 3), 0, 6-b);
		Shape s4 = translate(roundCorners(contract(shape, b-2), 2), 0, 6-b);
		Shape s5 = translate(roundCorners(contract(shape, b-1), 1), 0, 6-b);
		Color c0 = (color != null) ? color : getDefaultKeyCapColor();
		Color c1 = multiplyAdd(c0, 0.9f, 0.9f, 0.9f, 0, 0, 0);
		Color c2 = multiplyAdd(c0, 1.09f, 1.09f, 1.09f, 19.5f, 19.5f, 19.5f);
		if (opacity == null || opacity >= 1) {
			return new LayeredGroup(Arrays.<LayeredObject>asList(
				new LayeredShape(s0, Color.black, null),
				new LayeredShape(s1, c1, null),
				new LayeredShape(s2, c0, null),
				new LayeredShape(s3, c1, null),
				new LayeredShape(s4, c2, null)
			), null, null);
		} else if (opacity <= 0) {
			return new LayeredGroup(Arrays.<LayeredObject>asList(
				new LayeredShape(subtract(s0, s1), Color.black, null),
				new LayeredShape(subtract(s1, s2), Color.white, null),
				new LayeredShape(subtract(s3, s4), Color.black, null),
				new LayeredShape(subtract(s4, s5), Color.white, null)
			), null, null);
		} else {
			return new LayeredGroup(Arrays.<LayeredObject>asList(
				new LayeredGroup(Arrays.<LayeredObject>asList(
					new LayeredShape(s1, c0, null),
					new LayeredShape(s4, c2, null)
				), null, opacity),
				new LayeredShape(subtract(s0, s1), Color.black, null),
				new LayeredShape(subtract(s1, s2), c1, null),
				new LayeredShape(subtract(s3, s4), c1, null),
				new LayeredShape(subtract(s4, s5), c2, opacity)
			), null, null);
		}
	}
	
	public Shape createTopTextArea(Shape shape, String vs) {
		float b = maxBorder(shape);
		if (squareTop(vs)) shape = squareTop(shape);
		return translate(contract(shape, b), 0, 6-b);
	}
	
	public Shape createFrontTextArea(Shape shape, String vs) {
		float b = maxBorder(shape);
		if (squareTop(vs)) shape = squareTop(shape);
		Shape se = translate(contract(shape, b-4), 0, 6-b);
		Shape ss = translate(contract(shape, b), 0, b-2);
		return subtract(ss, se);
	}
}
