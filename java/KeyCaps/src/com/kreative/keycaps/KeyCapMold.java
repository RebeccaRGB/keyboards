package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Shape;

public abstract class KeyCapMold {
	public abstract Color getDefaultKeyCapColor();
	
	public Color getDefaultLegendColor(Color keyCapColor) {
		if (keyCapColor == null) keyCapColor = getDefaultKeyCapColor();
		return ColorUtilities.contrastingColor(keyCapColor);
	}
	
	public abstract LayeredObject createLayeredObject(Shape shape, String vs, Color color, Float opacity);
	public abstract Shape createTopTextArea(Shape shape, String vs);
	public abstract Shape createFrontTextArea(Shape shape, String vs);
	
	public static KeyCapMold forName(String name) {
		try {
			Class<?> cls = Class.forName(name);
			return cls.asSubclass(KeyCapMold.class).getConstructor().newInstance();
		} catch (Exception e) {}
		try {
			Class<?> cls = Class.forName("com.kreative.keycaps." + name);
			return cls.asSubclass(KeyCapMold.class).getConstructor().newInstance();
		} catch (Exception e) {}
		try {
			Class<?> cls = Class.forName("com.kreative.keycaps." + name + "KeyCapMold");
			return cls.asSubclass(KeyCapMold.class).getConstructor().newInstance();
		} catch (Exception e) {}
		try {
			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			Class<?> cls = Class.forName("com.kreative.keycaps." + name + "KeyCapMold");
			return cls.asSubclass(KeyCapMold.class).getConstructor().newInstance();
		} catch (Exception e) {}
		return null;
	}
}
