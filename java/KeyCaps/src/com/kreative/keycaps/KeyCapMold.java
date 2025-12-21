package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Shape;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class KeyCapMold {
	public abstract Color getDefaultKeyCapColor();
	
	public Color getDefaultLegendColor(Color keyCapColor) {
		if (keyCapColor == null) keyCapColor = getDefaultKeyCapColor();
		return ColorUtilities.contrastingColor(keyCapColor);
	}
	
	public abstract Padding getPadding();
	public abstract LayeredObject createLayeredObject(Shape shape, String vs, Color color, Float opacity);
	public abstract Shape createTopTextArea(Shape shape, String vs);
	public abstract Shape createFrontTextArea(Shape shape, String vs);
	
	private static final String FLOAT_PATTERN = "([+-]?)([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?";
	
	private static final Pattern FLAT_PATTERN = Pattern.compile(
		"^((com[.]kreative[.]keycaps[.])?FlatKeyCapMold|[Ff]lat)[(]" +
		"\\s*(?<p1>" + FLOAT_PATTERN + ")\\s*," +
		"\\s*(?<p2>" + FLOAT_PATTERN + ")\\s*[)]$"
	);
	
	public static KeyCapMold forName(String name) {
		try {
			Class<?> cls = Class.forName(name);
			return cls.asSubclass(KeyCapMold.class).getConstructor().newInstance();
		} catch (Throwable t) {}
		try {
			Class<?> cls = Class.forName("com.kreative.keycaps." + name);
			return cls.asSubclass(KeyCapMold.class).getConstructor().newInstance();
		} catch (Throwable t) {}
		try {
			Class<?> cls = Class.forName("com.kreative.keycaps." + name + "KeyCapMold");
			return cls.asSubclass(KeyCapMold.class).getConstructor().newInstance();
		} catch (Throwable t) {}
		try {
			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			Class<?> cls = Class.forName("com.kreative.keycaps." + name + "KeyCapMold");
			return cls.asSubclass(KeyCapMold.class).getConstructor().newInstance();
		} catch (Throwable t) {}
		
		Matcher fm = FLAT_PATTERN.matcher(name);
		if (fm.matches()) {
			float p1 = Float.parseFloat(fm.group("p1"));
			float p2 = Float.parseFloat(fm.group("p2"));
			return new FlatKeyCapMold(p1, p2);
		}
		return null;
	}
}
