package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.getPaletteColor;
import static com.kreative.keycaps.ColorUtilities.multiplyAdd;
import static com.kreative.keycaps.ShapeUtilities.contract;
import static com.kreative.keycaps.ShapeUtilities.roundCorners;
import static com.kreative.keycaps.ShapeUtilities.subtract;
import static com.kreative.keycaps.ShapeUtilities.translate;

import java.awt.Color;
import java.awt.Shape;
import java.util.Arrays;

public class MaxKeyCapMold extends KeyCapMold {
	public Color getDefaultKeyCapColor() {
		return getPaletteColor(31);
	}
	
	public LayeredObject createLayeredObject(Shape shape, Color color, Float opacity) {
		Shape s0 = roundCorners(shape, 6);
		Shape s1 = roundCorners(contract(shape, 1), 5);
		Shape s2 = roundCorners(contract(shape, 2), 4);
		Shape s3 = translate(roundCorners(contract(shape, 9), 3), 0, -6);
		Shape s4 = translate(roundCorners(contract(shape, 10), 2), 0, -6);
		Shape s5 = translate(roundCorners(contract(shape, 11), 1), 0, -6);
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
	
	public Shape createTopTextArea(Shape shape) {
		return translate(contract(shape, 12), 0, -6);
	}
	
	public Shape createFrontTextArea(Shape shape) {
		Shape se = translate(contract(shape, 8), 0, -6);
		Shape ss = translate(contract(shape, 12), 0, 10);
		return subtract(ss, se);
	}
}
