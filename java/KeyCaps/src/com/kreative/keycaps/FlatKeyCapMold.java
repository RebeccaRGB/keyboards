package com.kreative.keycaps;

import static com.kreative.keycaps.ShapeUtilities.contract;

import java.awt.Color;
import java.awt.Shape;

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
		shape = contract(shape, pathInset);
		if (color == null) color = Color.black;
		return new LayeredShape(shape, color, opacity);
	}
	
	public Shape createTopTextArea(Shape shape, String vs) {
		return contract(shape, textInset);
	}
	
	public Shape createFrontTextArea(Shape shape, String vs) {
		return null;
	}
}
