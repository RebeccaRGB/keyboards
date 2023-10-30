package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Shape;

public abstract class KeyCapMold {
	public abstract Color getDefaultKeyCapColor();
	
	public Color getDefaultLegendColor(Color keyCapColor) {
		if (keyCapColor == null) keyCapColor = getDefaultKeyCapColor();
		return ColorUtilities.contrastingColor(keyCapColor);
	}
	
	public abstract LayeredObject createLayeredObject(Shape shape, Color color, Float opacity);
	public abstract Shape createTopTextArea(Shape shape);
	public abstract Shape createFrontTextArea(Shape shape);
}
