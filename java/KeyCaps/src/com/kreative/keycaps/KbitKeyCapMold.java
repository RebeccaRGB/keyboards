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

public class KbitKeyCapMold extends KeyCapMold {
	private static final Color defaultColor = new Color(0xCCCCCC);
	private static final int pathInset = 5;
	private static final int layerCount = 6;
	
	private static final float[][] layerColors = {
		{ 0, 0, 0, 0, 0, 0 },
		{ 8/12f, 8/12f, 8/12f, 0, 0, 0 },
		{ 13/12f, 13/12f, 13/12f, 0, 0, 0 },
		{ 14/12f, 14/12f, 14/12f, 0, 0, 0 },
		{ 1, 1, 1, 0, 0, 0 },
		{ 14/12f, 14/12f, 14/12f, 0, 0, 0 },
	};
	
	private static final BorderShape[] layerShapes = {
		new BorderShape(
			5, 5,
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    null,
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 5 v 5 h -5 z"
		),
		new BorderShape(
			5, 5,
		    null, null,
		    "M 4 2 c 0 -0.276 -0.112 -0.526 -0.293 -0.707 L 0 5 h 4 V 2 z",
		    "M 4 4.5 C 4 4.224 4.224 4 4.5 4 H 5 V 0 H 0 v 5 h 4 V 4.5 z",
		    "M 0.5 4 c 0.139 0 0.264 0.056 0.354 0.146 L 5 0 H 0 v 4 H 0.5 z",
		    null, null,
		    "M 0 0 h 4 v 5 h -4 z",
		    "M 4 0.5 V 0 H 0 v 5 l 4.146 -4.146 C 4.056 0.764 4 0.639 4 0.5 z",
		    null,
		    "M 1.293 3.707 C 1.474 3.888 1.724 4 2 4 h 3 V 0 L 1.293 3.707 z",
		    "M 0 0 h 5 v 4 h -5 z",
		    "M 0 0 v 4 h 3 c 0.553 0 1 -0.447 1 -1 V 0 H 0 z"
		),
		new BorderShape(
			5, 5,
		    "M 5 1 H 2 C 1.448 1 1 1.448 1 2 v 3 h 4 V 1 z",
		    "M 0 1 h 5 v 4 h -5 z",
		    "M 3.707 1.293 C 3.526 1.112 3.276 1 3 1 H 0 v 4 L 3.707 1.293 z",
		    null,
		    "M 1 4.5 V 5 h 4 V 0 L 0.854 4.146 C 0.944 4.237 1 4.362 1 4.5 z",
		    "M 1 0 h 4 v 5 h -4 z",
		    null, null,
		    "M 4.5 1 C 4.362 1 4.237 0.944 4.146 0.854 L 0 5 h 5 V 1 H 4.5 z",
		    "M 1 0 v 0.5 C 1 0.776 0.776 1 0.5 1 H 0 v 4 h 5 V 0 H 1 z",
		    "M 1 3 c 0 0.276 0.112 0.526 0.293 0.707 L 5 0 H 1 V 3 z",
		    null, null
		),
		new BorderShape(
			5, 5,
		    "M 5 3 H 3.5 C 3.224 3 3 3.224 3 3.5 V 5 h 2 V 3 z",
		    "M 0 3 h 5 v 2 h -5 z",
		    "M 1.854 3.146 C 1.764 3.056 1.639 3 1.5 3 H 0 v 2 L 1.854 3.146 z",
		    null,
		    "M 3 3 v 2 h 2 V 0 L 2.707 2.293 C 2.888 2.474 3 2.724 3 3 z",
		    "M 3 0 h 2 v 5 h -2 z",
		    null, null,
		    "M 3 3 C 2.724 3 2.474 2.888 2.293 2.707 L 0 5 h 5 V 3 H 3 z",
		    "M 3 0 v 2 c 0 0.553 -0.447 1 -1 1 H 0 v 2 h 5 V 0 H 3 z",
		    "M 3 1.5 c 0 0.139 0.056 0.264 0.146 0.354 L 5 0 H 3 V 1.5 z",
		    null, null
		),
		new BorderShape(
			5, 5,
		    "M 4 4 h 1 v 1 h -1 z",
		    "M 0 4 h 5 v 1 h -5 z",
		    "M 2 5 V 3.5 c 0 -0.138 -0.056 -0.263 -0.146 -0.354 L 1 4 H 0 v 1 H 2 z",
		    "M 2 3 c 0 -0.552 0.448 -1 1 -1 h 2 V 0 H 0 v 5 h 2 V 3 z",
		    "M 2 2 c 0.276 0 0.526 0.112 0.707 0.293 l 0.707 -0.707 C 3.776 1.948 4 2.448 4 3 v 2 h 1 V 0 H 0 v 2 H 2 z",
		    "M 4 0 h 1 v 5 h -1 z",
		    "M 0 0 h 5 v 5 h -5 z",
		    "M 0 0 h 2 v 5 h -2 z",
		    "M 3 4 C 2.448 4 1.948 3.776 1.586 3.414 l 0.707 -0.707 C 2.112 2.526 2 2.276 2 2 V 0 H 0 v 5 h 5 V 4 H 3 z",
		    "M 4 2 c 0 1.104 -0.896 2 -2 2 H 0 v 1 h 5 V 0 H 4 V 2 z",
		    "M 4 0 v 1 L 3.146 1.854 C 3.237 1.944 3.362 2 3.5 2 H 5 V 0 H 4 z",
		    "M 0 0 h 5 v 2 h -5 z",
		    "M 0 2 c 1.104 0 2 -0.896 2 -2 H 0 V 2 z"
		),
		new BorderShape(
			5, 5,
		    "M 4.354 3.646 L 1.756 1.049 C 1.405 1.14 1.14 1.405 1.049 1.756 l 2.597 2.597 L 4.354 3.646 z",
		    null,
		    "M 3.707 1.293 C 3.526 1.112 3.276 1 3 1 H 2.586 L 0.293 3.293 L 1 4 L 3.707 1.293 z",
		    null, null, null, null, null, null, null,
		    "M 1.049 3.244 C 1.14 3.595 1.405 3.86 1.756 3.951 L 4 1.707 V 1 L 3.646 0.646 L 1.049 3.244 z",
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
		KbitKeyCapMold mold = new KbitKeyCapMold();
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
