package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Shape;
import java.util.Arrays;
import java.util.List;

public class FlatKeyCapMold extends KeyCapMold {
	private final float pathInset;
	private final float textInset;
	
	public FlatKeyCapMold() {
		this.pathInset = 1;
		this.textInset = 7;
	}
	
	public FlatKeyCapMold(float pathInset, float textInset) {
		this.pathInset = pathInset;
		this.textInset = textInset;
	}
	
	public Color getDefaultKeyCapColor() {
		return Color.black;
	}
	
	public Padding getPadding() {
		return new Padding(0, 0, 0, 0);
	}
	
	public LayeredObject createLayeredObject(Shape shape, String vs, Color color, Float opacity) {
		List<String> vss = Arrays.asList(StringUtilities.split(vs));
		shape = KeyCapVariants.applyTransformations(shape, vss);
		shape = ShapeUtilities.contract(shape, pathInset);
		if (color == null) color = Color.black;
		return new LayeredShape(shape, color, opacity);
	}
	
	public Shape createTopTextArea(Shape shape, String vs) {
		List<String> vss = Arrays.asList(StringUtilities.split(vs));
		shape = KeyCapVariants.getActiveArea(shape, vss);
		shape = KeyCapVariants.applyTransformations(shape, vss);
		return ShapeUtilities.contract(shape, textInset);
	}
	
	public Shape createFrontTextArea(Shape shape, String vs) {
		return null;
	}
}
