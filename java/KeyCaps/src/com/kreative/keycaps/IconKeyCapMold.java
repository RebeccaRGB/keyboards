package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.getPaletteColor;
import static com.kreative.keycaps.ColorUtilities.getPaletteOpacity;
import static com.kreative.keycaps.ColorUtilities.multiplyAdd;
import static com.kreative.keycaps.ShapeUtilities.contract;
import static com.kreative.keycaps.ShapeUtilities.toSVGViewBox;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

public class IconKeyCapMold extends KeyCapMold {
	private static final Color defaultColor = new Color(0xCCCCCC);
	private static final int pathInset = 7;
	private static final int layerCount = 12;
	
	private static final float[][] layerColors = {
		{ 0, 0, 0, 0, 0, 0 },
		{ 5/12f, 5/12f, 5/12f, 0, 0, 0 },
		{ 7/12f, 7/12f, 7/12f, 0, 0, 0 },
		{ 8/12f, 8/12f, 8/12f, 0, 0, 0 },
		{ 10/12f, 10/12f, 10/12f, 0, 0, 0 },
		{ 11/12f, 11/12f, 11/12f, 0, 0, 0 },
		{ 13/12f, 13/12f, 13/12f, 0, 0, 0 },
		{ 14/12f, 14/12f, 14/12f, 0, 0, 0 },
		{ 1, 1, 1, 0, 0, 0 },
		{ 5/12f, 5/12f, 5/12f, 0, 0, 0 },
		{ 7/12f, 7/12f, 7/12f, 0, 0, 0 },
		{ 14/12f, 14/12f, 14/12f, 0, 0, 0 },
	};
	
	private static final BorderShape[] layerShapes = {
		new BorderShape(
			7, 7,
			"M 7 1 H 3 C 1.896 1 1 1.896 1 3 v 4 h 6 V 1 z",
			"M 0 1 h 7 v 6 h -7 z",
			"M 6 7 V 3 c 0 -1.104 -0.896 -2 -2 -2 H 0 v 6 H 6 z",
			"M 6 6.2 C 6 6.089 6.089 6 6.2 6 H 7 V 0 H 0 v 7 h 6 V 6.2 z",
			"M 0 0 v 6 h 0.8 C 0.91 6 1 6.089 1 6.2 V 7 h 6 V 0 H 0 z",
			"M 1 0 h 6 v 7 h -6 z",
			null,
			"M 0 0 h 6 v 7 h -6 z",
			"M 6.2 1 C 6.089 1 6 0.91 6 0.8 V 0 H 0 v 7 h 7 V 1 H 6.2 z",
			"M 1 0.8 C 1 0.91 0.91 1 0.8 1 H 0 v 6 h 7 V 0 H 1 V 0.8 z",
			"M 1 0 v 4 c 0 1.104 0.896 2 2 2 h 4 V 0 H 1 z",
			"M 0 0 h 7 v 6 h -7 z",
			"M 0 6 h 4 c 1.104 0 2 -0.896 2 -2 V 0 H 0 V 6 z"
		),
		new BorderShape(
			7, 7,
			null, null,
			"M 5 3 c 0 -0.276 -0.112 -0.526 -0.293 -0.707 L 0 7 h 5 V 3 z",
			"M 5 5.4 C 5 5.179 5.179 5 5.4 5 H 7 V 0 H 0 v 7 h 5 V 5.4 z",
			"M 1.6 5 c 0.11 0 0.211 0.045 0.283 0.117 L 7 0 H 0 v 5 H 1.6 z",
			null, null,
			"M 0 0 h 5 v 7 h -5 z",
			"M 5 1.6 V 0 H 0 v 7 l 5.117 -5.117 C 5.045 1.811 5 1.71 5 1.6 z",
			null,
			"M 2.293 4.707 C 2.474 4.888 2.724 5 3 5 h 4 V 0 L 2.293 4.707 z",
			"M 0 0 h 7 v 5 h -7 z",
			"M 0 5 h 4 c 0.553 0 1 -0.447 1 -1 V 0 H 0 V 5 z"
		),
		new BorderShape(
			7, 7,
			null, null,
			"M 4 3.8 c 0 -0.221 -0.09 -0.421 -0.234 -0.565 L 0 7 h 4 V 3.8 z",
			"M 4 4.6 C 4 4.269 4.269 4 4.6 4 H 7 V 0 H 0 v 7 h 4 V 4.6 z",
			"M 2.4 4 c 0.166 0 0.315 0.067 0.424 0.176 L 7 0 H 0 v 4 H 2.4 z",
			null, null,
			"M 0 0 h 4 v 7 h -4 z",
			"M 4 2.4 V 0 H 0 v 7 l 4.176 -4.176 C 4.067 2.716 4 2.566 4 2.4 z",
			null,
			"M 3.234 3.766 C 3.379 3.91 3.579 4 3.8 4 H 7 V 0 L 3.234 3.766 z",
			"M 0 0 h 7 v 4 h -7 z",
			"M 0 4 h 3.2 C 3.642 4 4 3.642 4 3.2 V 0 H 0 V 4 z"
		),
		new BorderShape(
			7, 7,
			null, null,
			"M 3 4.6 c 0 -0.166 -0.067 -0.316 -0.176 -0.424 L 0 7 h 3 V 4.6 z",
			"M 3 3.8 C 3 3.358 3.358 3 3.8 3 H 7 V 0 H 0 v 7 h 3 V 3.8 z",
			"M 3.2 3 c 0.221 0 0.421 0.09 0.565 0.234 L 7 0 H 0 v 3 H 3.2 z",
			null, null,
			"M 0 0 h 3 v 7 h -3 z",
			"M 3 3.2 V 0 H 0 v 7 l 3.234 -3.234 C 3.09 3.621 3 3.421 3 3.2 z",
			null,
			"M 4.176 2.824 C 4.284 2.933 4.435 3 4.6 3 H 7 V 0 L 4.176 2.824 z",
			"M 0 0 h 7 v 3 h -7 z",
			"M 0 3 h 2.4 C 2.731 3 3 2.731 3 2.4 V 0 H 0 V 3 z"
		),
		new BorderShape(
			7, 7,
			"M 7 2 H 3 C 2.448 2 2 2.448 2 3 v 4 h 5 V 2 z",
			"M 0 2 h 7 v 5 h -7 z",
			"M 4.707 2.293 C 4.526 2.112 4.276 2 4 2 H 0 v 5 h 2 V 5.4 c 0 -0.11 -0.045 -0.21 -0.117 -0.283 L 4.707 2.293 z",
			"M 2 3 c 0 -0.552 0.448 -1 1 -1 h 4 V 0 H 0 v 7 h 2 V 3 z",
			"M 0 0 v 2 h 4 c 0.276 0 0.526 0.112 0.707 0.293 L 1.883 5.117 C 1.955 5.189 2 5.29 2 5.4 V 7 h 5 V 0 H 0 z",
			"M 2 0 h 5 v 7 h -5 z",
			null,
			"M 0 0 h 2 v 7 h -2 z",
			"M 5.4 2c -0.11 0 -0.21 -0.045 -0.283 -0.117 L 2.293 4.707 C 2.112 4.526 2 4.276 2 4 V 0 H 0 v 7 h 7 V 2 H 5.4 z",
			"M 2 1.6 C 2 1.821 1.821 2 1.6 2 H 0 v 5 h 7 V 0 H 2 V 1.6 z",
			"M 2 0 v 4 c 0 0.276 0.112 0.526 0.293 0.707 l 2.824 -2.824 C 5.189 1.955 5.29 2 5.4 2 H 7 V 0 H 2 z",
			"M 0 0 h 7 v 2 h -7 z",
			"M 0 2 h 1.6 C 1.821 2 2 1.821 2 1.6 V 0 H 0 V 2 z"
		),
		new BorderShape(
			7, 7,
			"M 7 3 H 3.8 C 3.358 3 3 3.358 3 3.8 V 7 h 4 V 3 z",
			"M 0 3 h 7 v 4 h -7 z",
			"M 3.766 3.234 C 3.621 3.09 3.421 3 3.2 3 H 0 v 4 L 3.766 3.234 z",
			null,
			"M 3 4.6 V 7 h 4 V 0 L 2.824 4.176 C 2.933 4.284 3 4.435 3 4.6 z",
			"M 3 0 h 4 v 7 h -4 z",
			null, null,
			"M 4.6 3 C 4.435 3 4.284 2.933 4.176 2.824 L 0 7 h 7 V 3 H 4.6 z",
			"M 3 2.4 C 3 2.731 2.731 3 2.4 3 H 0 v 4 h 7 V 0 H 3 V 2.4 z",
			"M 3 3.2 c 0 0.221 0.09 0.421 0.234 0.565 L 7 0 H 3 V 3.2 z",
			null, null
		),
		new BorderShape(
			7, 7,
			"M 7 4 H 4.6 C 4.269 4 4 4.269 4 4.6 V 7 h 3 V 4 z",
			"M 0 4 h 7 v 3 h -7 z",
			"M 2.824 4.176 C 2.716 4.067 2.566 4 2.4 4 H 0 v 3 L 2.824 4.176 z",
			null,
			"M 4 3.8 V 7 h 3 V 0 L 3.766 3.234 C 3.91 3.379 4 3.579 4 3.8 z",
			"M 4 0 h 3 v 7 h -3 z",
			null, null,
			"M 3.8 4 C 3.579 4 3.379 3.91 3.234 3.766 L 0 7 h 7 V 4 H 3.8 z",
			"M 4 3.2 C 4 3.642 3.642 4 3.2 4 H 0 v 3 h 7 V 0 H 4 V 3.2 z",
			"M 4 2.4 c 0 0.166 0.067 0.315 0.176 0.424 L 7 0 H 4 V 2.4 z",
			null, null
		),
		new BorderShape(
			7, 7,
			"M 7 5 H 5.4 C 5.179 5 5 5.179 5 5.4 V 7 h 2 V 5 z",
			"M 0 5 h 7 v 2 h -7 z",
			"M 1.883 5.117 C 1.811 5.045 1.71 5 1.6 5 H 0 v 2 L 1.883 5.117 z",
			null,
			"M 5 3 v 4 h 2 V 0 L 4.707 2.293 C 4.888 2.474 5 2.724 5 3 z",
			"M 5 0 h 2 v 7 h -2 z",
			null, null,
			"M 3 5 C 2.724 5 2.474 4.888 2.293 4.707 L 0 7 h 7 V 5 H 3 z",
			"M 5 4 c 0 0.553 -0.447 1 -1 1 H 0 v 2 h 7 V 0 H 5 V 4 z",
			"M 5 1.6 c 0 0.11 0.045 0.211 0.117 0.283 L 7 0 H 5 V 1.6 z",
			null, null
		),
		new BorderShape(
			7, 7,
			"M 7 6 H 6.2 C 6.089 6 6 6.089 6 6.2 V 7 h 1 V 6 z",
			"M 0 6 h 7 v 1 h -7 z",
			"M 1 7 V 6.2 C 1 6.089 0.91 6 0.8 6 H 0 v 1 H 1 z",
			"M 1 3 c 0 -1.104 0.896 -2 2 -2 h 4 V 0 H 0 v 7 h 1 V 3 z",
			"M 0 0 v 1 h 4 c 1.104 0 2 0.896 2 2 v 4 h 1 V 0 H 0 z",
			"M 6 0 h 1 v 7 h -1 z",
			"M 0 0 h 7 v 7 h -7 z",
			"M 0 0 h 1 v 7 h -1 z",
			"M 3 6 C 1.896 6 1 5.104 1 4 V 0 H 0 v 7 h 7 V 6 H 3 z",
			"M 6 4 c 0 1.104 -0.896 2 -2 2 H 0 v 1 h 7 V 0 H 6 V 4 z",
			"M 6 0 v 0.8 C 6 0.91 6.089 1 6.2 1 H 7 V 0 H 6 z",
			"M 0 0 h 7 v 1 h -7 z",
			"M 0 1 h 0.8 C 0.91 1 1 0.91 1 0.8 V 0 H 0 V 1 z"
		),
		new BorderShape(
			7, 7,
			null, null, null, null, null, null, null, null, null, null, null, null,
			"M 2 1.293 V 1.6 C 2 1.821 1.821 2 1.6 2 H 1.293 l 2.951 2.951 C 4.595 4.86 4.86 4.595 4.951 4.244 L 2 1.293 z"
		),
		new BorderShape(
			7, 7,
			null, null,
			"M 0.8 6 C 0.91 6 1 6.089 1 6.2 v 0.507 l 3.951 -3.951c -0.046 -0.176 -0.12 -0.339 -0.244 -0.463 C 4.583 2.168 4.42 2.094 4.244 2.049 L 0.293 6 H 0.8 z",
			null, null, null, null, null,
			"M 5.4 2 C 5.179 2 5 1.821 5 1.6 V 1.293 L 1.283 5.011 c 0.086 0.146 0.184 0.284 0.303 0.403 c 0.12 0.119 0.258 0.217 0.404 0.303 L 5.707 2 H 5.4 z",
			null, null, null,
			"M 1 0.293 V 0.8 C 1 0.91 0.91 1 0.8 1 H 0.293 l 1 1 H 1.6 C 1.821 2 2 1.821 2 1.6 V 1.293 L 1 0.293 z"
		),
		new BorderShape(
			7, 7,
			"M 6 6.2 C 6 6.089 6.089 6 6.2 6 h 0.507 L 2.756 2.049 C 2.405 2.14 2.14 2.405 2.049 2.756 L 6 6.707 V 6.2 z",
			null, null, null,
			"M 1.6 5 C 1.821 5 2 5.179 2 5.4 v 0.307 L 5.717 1.99 C 5.631 1.844 5.533 1.705 5.414 1.586 C 5.295 1.466 5.156 1.369 5.01 1.283 L 1.293 5 H 1.6 z",
			null, null, null, null, null,
			"M 6.2 1 C 6.089 1 6 0.91 6 0.8 V 0.293 L 2.049 4.244 C 2.094 4.42 2.168 4.583 2.293 4.707 c 0.124 0.124 0.288 0.198 0.463 0.244 L 6.708 1 H 6.2 z",
			null, null
		),
	};
	
	public Color getDefaultKeyCapColor() {
		return defaultColor;
	}
	
	public LayeredObject createLayeredObject(Shape shape, Color color, Float opacity) {
		if (color == null) color = getDefaultKeyCapColor();
		List<LayeredObject> layers = new ArrayList<LayeredObject>();
		for (int li = 0; li < layerCount; li++) {
			Shape ls = layerShapes[li].create(shape);
			if (ls == null) continue;
			float[] c = layerColors[li];
			Color lc = multiplyAdd(color, c[0], c[1], c[2], c[3], c[4], c[5]);
			layers.add(new LayeredShape(ls, lc, null));
		}
		return new LayeredGroup(layers, null, null);
	}
	
	public Shape createTopTextArea(Shape shape) {
		return contract(shape, pathInset);
	}
	
	public Shape createFrontTextArea(Shape shape) {
		return null;
	}
	
	public static void main(String[] args) {
		IconKeyCapMold mold = new IconKeyCapMold();
		String vbox = toSVGViewBox(new Rectangle(0, 0, 54*16, 54*16), 0, 1000);
		System.out.println("<?xml version=\"1.0\"?>");
		System.out.println("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"" + vbox + ">");
		for (int i = 0; i < 256; i++) {
			Rectangle r = new Rectangle((i & 15) * 54, (i >> 4) * 54, 54, 54);
			LayeredObject o = mold.createLayeredObject(r, getPaletteColor(i), getPaletteOpacity(i));
			System.out.println(o.toSVG("  ", "  "));
		}
		System.out.println("</svg>");
	}
}
