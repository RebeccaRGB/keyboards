package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.getPaletteColor;
import static com.kreative.keycaps.ColorUtilities.multiplyAdd;
import static com.kreative.keycaps.ShapeUtilities.add;
import static com.kreative.keycaps.ShapeUtilities.contract;
import static com.kreative.keycaps.ShapeUtilities.roundCorners;
import static com.kreative.keycaps.ShapeUtilities.subtract;
import static com.kreative.keycaps.ShapeUtilities.translate;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

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
	
	private Shape topShape(Shape shape, String vs) {
		if (vs == null || (vs = vs.trim()).length() == 0) return shape;
		List<String> vss = Arrays.asList(vs.split("\\s+"));
		if (vss.contains("s")) {
			// Stepped
			Rectangle2D r = ShapeUtilities.getWidestRect(shape, null);
			double s = Math.min(r.getWidth(), r.getHeight());
			return new Rectangle2D.Double(r.getCenterX() - s/2, r.getCenterY() - s/2, s, s);
		}
		if (vss.contains("sc")) {
			// Stepped Caps / Ctrl
			Rectangle2D r = ShapeUtilities.getWidestRect(shape, null);
			double s = Math.min(r.getWidth(), r.getHeight());
			return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth() - s/2, r.getHeight());
		}
		if (vss.contains("sb")) {
			// Stepped Backspace
			Rectangle2D r = ShapeUtilities.getWidestRect(shape, null);
			double s = Math.min(r.getWidth(), r.getHeight());
			return new Rectangle2D.Double(r.getX() + s/2, r.getY(), r.getWidth() - s/2, r.getHeight());
		}
		if (vss.contains("se")) {
			// Stepped Enter
			return ShapeUtilities.getTallestRect(shape, null);
		}
		return shape;
	}
	
	public LayeredObject createLayeredObject(Shape shape, String vs, Color color, Float opacity) {
		float b = maxBorder(shape);
		Shape top = topShape(shape, vs);
		Shape s0 = roundCorners(shape, 6);
		Shape s1 = roundCorners(contract(shape, 1), 5);
		Shape s2 = roundCorners(contract(shape, 2), 4);
		Shape s3, s4, s5;
		if (top != shape) {
			float a = (6 + b) / 2;
			Shape s3a = translate(roundCorners(contract(shape, a-3), 3), 0, 6-a);
			Shape s4a = translate(roundCorners(contract(shape, a-2), 2), 0, 6-a);
			Shape s5a = translate(roundCorners(contract(shape, a-1), 1), 0, 6-a);
			Shape s3b = translate(roundCorners(contract(top, b-3), 3), 0, 6-b);
			Shape s4b = translate(roundCorners(contract(top, b-2), 2), 0, 6-b);
			Shape s5b = translate(roundCorners(contract(top, b-1), 1), 0, 6-b);
			Shape s3c = translate(roundCorners(contract(top, b-a+2), 4), 0, 6-a);
			Shape s4c = translate(roundCorners(contract(top, b-a+1), 5), 0, 6-a);
			Shape s5c = translate(roundCorners(contract(top, b-a+0), 6), 0, 6-a);
			s3 = add(s3b, subtract(s3a, s3c));
			s4 = add(s4b, subtract(s4a, s4c));
			s5 = add(s5b, subtract(s5a, s5c));
		} else {
			s3 = translate(roundCorners(contract(shape, b-3), 3), 0, 6-b);
			s4 = translate(roundCorners(contract(shape, b-2), 2), 0, 6-b);
			s5 = translate(roundCorners(contract(shape, b-1), 1), 0, 6-b);
		}
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
		Shape top = topShape(shape, vs);
		return translate(contract(top, b), 0, 6-b);
	}
	
	public Shape createFrontTextArea(Shape shape, String vs) {
		float b = maxBorder(shape);
		Shape top = topShape(shape, vs);
		Shape se = translate(contract(top, b-4), 0, 6-b);
		Shape ss = translate(contract(top, b), 0, b-2);
		return subtract(ss, se);
	}
}
