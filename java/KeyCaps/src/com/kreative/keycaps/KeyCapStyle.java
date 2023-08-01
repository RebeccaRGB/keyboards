package com.kreative.keycaps;

import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyCapStyle {
	private static final Pattern PATH_TOKEN = Pattern.compile("([A-Za-z])|([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?)");
	private static final float DECIMAL_PLACES = 1000;
	
	public static final KeyCapStyle KCAP_KBIT = new KeyCapStyle(5, 5, new String[][] {
		{
		    "#000000",
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
		},
		{
		    "#888888",
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
		},
		{
		    "#DDDDDD",
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
		},
		{
		    "#EEEEEE",
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
		},
		{
		    "#CCCCCC",
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
		},
		{
		    "#EEEEEE",
		    "M 4.354 3.646 L 1.756 1.049 C 1.405 1.14 1.14 1.405 1.049 1.756 l 2.597 2.597 L 4.354 3.646 z",
		    null,
		    "M 3.707 1.293 C 3.526 1.112 3.276 1 3 1 H 2.586 L 0.293 3.293 L 1 4 L 3.707 1.293 z",
		    null, null, null, null, null, null, null,
		    "M 1.049 3.244 C 1.14 3.595 1.405 3.86 1.756 3.951 L 4 1.707 V 1 L 3.646 0.646 L 1.049 3.244 z",
		    null, null
		},
	}, 5, 5, 5, 5, "#000000");
	
	public static final KeyCapStyle KCAP_ICL8 = new KeyCapStyle(7, 7, new String[][] {
		{
			"#000000",
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
		},
		{
			"#555555",
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
		},
		{
			"#777777",
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
		},
		{
			"#888888",
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
		},
		{
			"#AAAAAA",
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
		},
		{
			"#BBBBBB",
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
		},
		{
			"#DDDDDD",
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
		},
		{
			"#EEEEEE",
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
		},
		{
			"#CCCCCC",
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
		},
		{
			"#555555",
			null, null, null, null, null, null, null, null, null, null, null, null,
			"M 2 1.293 V 1.6 C 2 1.821 1.821 2 1.6 2 H 1.293 l 2.951 2.951 C 4.595 4.86 4.86 4.595 4.951 4.244 L 2 1.293 z"
		},
		{
			"#777777",
			null, null,
			"M 0.8 6 C 0.91 6 1 6.089 1 6.2 v 0.507 l 3.951 -3.951c -0.046 -0.176 -0.12 -0.339 -0.244 -0.463 C 4.583 2.168 4.42 2.094 4.244 2.049 L 0.293 6 H 0.8 z",
			null, null, null, null, null,
			"M 5.4 2 C 5.179 2 5 1.821 5 1.6 V 1.293 L 1.283 5.011 c 0.086 0.146 0.184 0.284 0.303 0.403 c 0.12 0.119 0.258 0.217 0.404 0.303 L 5.707 2 H 5.4 z",
			null, null, null,
			"M 1 0.293 V 0.8 C 1 0.91 0.91 1 0.8 1 H 0.293 l 1 1 H 1.6 C 1.821 2 2 1.821 2 1.6 V 1.293 L 1 0.293 z"
		},
		{
			"#EEEEEE",
			"M 6 6.2 C 6 6.089 6.089 6 6.2 6 h 0.507 L 2.756 2.049 C 2.405 2.14 2.14 2.405 2.049 2.756 L 6 6.707 V 6.2 z",
			null, null, null,
			"M 1.6 5 C 1.821 5 2 5.179 2 5.4 v 0.307 L 5.717 1.99 C 5.631 1.844 5.533 1.705 5.414 1.586 C 5.295 1.466 5.156 1.369 5.01 1.283 L 1.293 5 H 1.6 z",
			null, null, null, null, null,
			"M 6.2 1 C 6.089 1 6 0.91 6 0.8 V 0.293 L 2.049 4.244 C 2.094 4.42 2.168 4.583 2.293 4.707 c 0.124 0.124 0.288 0.198 0.463 0.244 L 6.708 1 H 6.2 z",
			null, null
		},
	}, 7, 7, 7, 7, "#000000");
	
	private final int pathWidth;
	private final int pathHeight;
	private final String[] layerColors;
	private final Shape[][] layerShapes;
	private final int top, left, bottom, right;
	private final String textColor;
	
	public KeyCapStyle(
		int pathWidth, int pathHeight, String[][] layerPaths,
		int top, int left, int bottom, int right, String textColor
	) {
		this.pathWidth = pathWidth;
		this.pathHeight = pathHeight;
		this.layerColors = new String[layerPaths.length];
		this.layerShapes = new Shape[layerPaths.length][];
		for (int i = 0; i < layerPaths.length; i++) {
			layerColors[i] = layerPaths[i][0];
			layerShapes[i] = new Shape[layerPaths[i].length - 1];
			for (int j = 0; j < layerShapes[i].length; j++) {
				String path = layerPaths[i][j + 1];
				if (path == null || path.length() == 0) continue;
				layerShapes[i][j] = fromSVGPath(path);
			}
		}
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.textColor = textColor;
	}
	
	private Area[] layeredArea(Area[] areas, int pi, AffineTransform tx) {
		if (areas == null) areas = new Area[layerShapes.length];
		for (int li = 0; li < layerShapes.length; li++) {
			Shape shape = layerShapes[li][pi];
			if (shape == null) continue;
			if (tx != null) shape = tx.createTransformedShape(shape);
			if (areas[li] == null) areas[li] = new Area(shape);
			else areas[li].add(new Area(shape));
		}
		return areas;
	}
	
	private Area[] layeredAreaForContent(Area[] areas, Shape shape) {
		if (shape == null) return areas;
		if (areas == null) areas = new Area[layerShapes.length];
		BasicStroke stroke = new BasicStroke(Math.min(pathWidth, pathHeight) * 2);
		Area strokedArea = new Area(stroke.createStrokedShape(shape));
		Area area = new Area(shape); area.subtract(strokedArea);
		for (int li = 0; li < layerShapes.length; li++) {
			if (layerShapes[li][6] == null) continue;
			if (areas[li] == null) areas[li] = area;
			else areas[li].add(area);
		}
		return areas;
	}
	
	private Area[] layeredAreaForPoint(Area[] areas, Shape shape, float x, float y) {
		if (shape == null) return areas;
		boolean tl = shape.contains(x+1, y+1);
		boolean tr = shape.contains(x-1, y+1);
		boolean bl = shape.contains(x+1, y-1);
		boolean br = shape.contains(x-1, y-1);
		if (!tr && !bl) {
			if (tl) areas = layeredArea(areas, 0, AffineTransform.getTranslateInstance(x, y));
			if (br) areas = layeredArea(areas, 12, AffineTransform.getTranslateInstance(x-pathWidth, y-pathHeight));
		} else if (!tl && !br) {
			if (tr) areas = layeredArea(areas, 2, AffineTransform.getTranslateInstance(x-pathWidth, y));
			if (bl) areas = layeredArea(areas, 10, AffineTransform.getTranslateInstance(x, y-pathHeight));
		} else {
			if ( tl &&  tr && !bl && !br) areas = layeredArea(areas, 1, AffineTransform.getTranslateInstance(x-pathWidth/2, y));
			if (!tl &&  tr &&  bl &&  br) areas = layeredArea(areas, 3, AffineTransform.getTranslateInstance(x-pathWidth, y-pathHeight));
			if ( tl && !tr &&  bl &&  br) areas = layeredArea(areas, 4, AffineTransform.getTranslateInstance(x, y-pathHeight));
			if ( tl && !tr &&  bl && !br) areas = layeredArea(areas, 5, AffineTransform.getTranslateInstance(x, y-pathHeight/2));
			if ( tl &&  tr &&  bl &&  br) areas = layeredArea(areas, 6, AffineTransform.getTranslateInstance(x-pathWidth/2, y-pathHeight/2));
			if (!tl &&  tr && !bl &&  br) areas = layeredArea(areas, 7, AffineTransform.getTranslateInstance(x-pathWidth, y-pathHeight/2));
			if ( tl &&  tr && !bl &&  br) areas = layeredArea(areas, 8, AffineTransform.getTranslateInstance(x-pathWidth, y));
			if ( tl &&  tr &&  bl && !br) areas = layeredArea(areas, 9, AffineTransform.getTranslateInstance(x, y));
			if (!tl && !tr &&  bl &&  br) areas = layeredArea(areas, 11, AffineTransform.getTranslateInstance(x-pathWidth/2, y-pathHeight));
		}
		return areas;
	}
	
	private Area[] layeredAreaForLine(Area[] areas, Shape shape, float x0, float y0, float x1, float y1) {
		if (shape == null) return areas;
		if (y0 == y1) {
			if (shape.contains((x0+x1)/2, y0+1)) {
				float x2 = Math.min(x0, x1);
				float x3 = Math.max(x0, x1);
				if (!shape.contains(x2-1, y0+1)) x2 += pathWidth;
				if (!shape.contains(x3+1, y0+1)) x3 -= pathWidth;
				AffineTransform tx = AffineTransform.getTranslateInstance(x2, y0);
				tx.scale((x3 - x2) / pathWidth, 1);
				areas = layeredArea(areas, 1, tx);
			}
			if (shape.contains((x0+x1)/2, y0-1)) {
				float x2 = Math.min(x0, x1);
				float x3 = Math.max(x0, x1);
				if (!shape.contains(x2-1, y0-1)) x2 += pathWidth;
				if (!shape.contains(x3+1, y0-1)) x3 -= pathWidth;
				AffineTransform tx = AffineTransform.getTranslateInstance(x2, y0 - pathHeight);
				tx.scale((x3 - x2) / pathWidth, 1);
				areas = layeredArea(areas, 11, tx);
			}
		}
		if (x0 == x1) {
			if (shape.contains(x0+1, (y0+y1)/2)) {
				float y2 = Math.min(y0, y1);
				float y3 = Math.max(y0, y1);
				if (!shape.contains(x0+1, y2-1)) y2 += pathHeight;
				if (!shape.contains(x0+1, y3+1)) y3 -= pathHeight;
				AffineTransform tx = AffineTransform.getTranslateInstance(x0, y2);
				tx.scale(1, (y3 - y2) / pathHeight);
				areas = layeredArea(areas, 5, tx);
			}
			if (shape.contains(x0-1, (y0+y1)/2)) {
				float y2 = Math.min(y0, y1);
				float y3 = Math.max(y0, y1);
				if (!shape.contains(x0-1, y2-1)) y2 += pathHeight;
				if (!shape.contains(x0-1, y3+1)) y3 -= pathHeight;
				AffineTransform tx = AffineTransform.getTranslateInstance(x0 - pathWidth, y2);
				tx.scale(1, (y3 - y2) / pathHeight);
				areas = layeredArea(areas, 7, tx);
			}
		}
		return areas;
	}
	
	public Area[] layeredAreaFromShape(Area[] areas, Shape shape) {
		if (shape == null) return areas;
		areas = layeredAreaForContent(areas, shape);
		float sx = 0, sy = 0, cx = 0, cy = 0;
		float[] c = new float[6];
		for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
			switch (i.currentSegment(c)) {
				case PathIterator.SEG_MOVETO:
					sx = cx = c[0];
					sy = cy = c[1];
					break;
				case PathIterator.SEG_LINETO:
					areas = layeredAreaForLine(areas, shape, cx, cy, c[0], c[1]);
					areas = layeredAreaForPoint(areas, shape, cx = c[0], cy = c[1]);
					break;
				case PathIterator.SEG_QUADTO:
					areas = layeredAreaForLine(areas, shape, cx, cy, c[2], c[3]);
					areas = layeredAreaForPoint(areas, shape, cx = c[2], cy = c[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					areas = layeredAreaForLine(areas, shape, cx, cy, c[4], c[5]);
					areas = layeredAreaForPoint(areas, shape, cx = c[4], cy = c[5]);
					break;
				case PathIterator.SEG_CLOSE:
					if (cx == sx && cy == sy) break;
					areas = layeredAreaForLine(areas, shape, cx, cy, sx, sy);
					areas = layeredAreaForPoint(areas, shape, cx = sx, cy = sy);
					break;
			}
		}
		return areas;
	}
	
	public Area[] layeredAreaFromSVGPath(Area[] areas, String path) {
		return layeredAreaFromShape(areas, fromSVGPath(path));
	}
	
	public String layeredAreaToSVGPaths(Area[] areas) {
		if (areas == null) return null;
		StringBuffer sb = new StringBuffer();
		for (int li = 0; li < layerShapes.length; li++) {
			Shape shape = areas[li];
			if (shape == null) continue;
			sb.append("<path fill=\"");
			sb.append(layerColors[li]);
			sb.append("\" d=\"");
			sb.append(toSVGPath(shape, DECIMAL_PLACES));
			sb.append("\"/>\n");
		}
		return sb.toString();
	}
	
	public Insets getInsets() {
		return new Insets(top, left, bottom, right);
	}
	
	public String getTextColor() {
		return textColor;
	}
	
	private static GeneralPath fromSVGPath(String d) {
		float sx = 0, sy = 0;
		float cx = 0, cy = 0;
		float ccx = 0, ccy = 0;
		float[] cc = new float[8];
		int ci = 0, cn = 2;
		char cm = 'M';
		GeneralPath path = new GeneralPath();
		Matcher m = PATH_TOKEN.matcher(d);
		while (m.find()) {
			if (m.group(1) != null && m.group(1).length() > 0) {
				cm = m.group(1).charAt(0);
				if (cm == 'Z' || cm == 'z') {
					path.closePath();
					ccx = cx = sx;
					ccy = cy = sy;
					cm -= 13;
				}
				cn = (
					(cm == 'M' || cm == 'm') ? 2 :
					(cm == 'L' || cm == 'l') ? 2 :
					(cm == 'H' || cm == 'h') ? 1 :
					(cm == 'V' || cm == 'v') ? 1 :
					(cm == 'C' || cm == 'c') ? 6 :
					(cm == 'S' || cm == 's') ? 4 :
					(cm == 'Q' || cm == 'q') ? 4 :
					(cm == 'T' || cm == 't') ? 2 :
					(cm == 'A' || cm == 'a') ? 7 :
					Integer.MAX_VALUE
				);
				ci = 0;
			}
			if (m.group(2) != null && m.group(2).length() > 0) {
				cc[ci++] = Float.parseFloat(m.group(2));
				if (ci >= cn) {
					ci = 0;
					switch (cm) {
						case 'M': path.moveTo((sx = ccx = cx = cc[0]), (sy = ccy = cy = cc[1])); cm--; break;
						case 'm': path.moveTo((sx = ccx = cx += cc[0]), (sy = ccy = cy += cc[1])); cm--; break;
						case 'L': path.lineTo((ccx = cx = cc[0]), (ccy = cy = cc[1])); break;
						case 'l': path.lineTo((ccx = cx += cc[0]), (ccy = cy += cc[1])); break;
						case 'H': path.lineTo((ccx = cx = cc[0]), (ccy = cy)); break;
						case 'h': path.lineTo((ccx = cx += cc[0]), (ccy = cy)); break;
						case 'V': path.lineTo((ccx = cx), (ccy = cy = cc[0])); break;
						case 'v': path.lineTo((ccx = cx), (ccy = cy += cc[0])); break;
						case 'C':
							path.curveTo(cc[0], cc[1], cc[2], cc[3], cc[4], cc[5]);
							ccx = cc[2]; ccy = cc[3]; cx = cc[4]; cy = cc[5]; break;
						case 'c':
							path.curveTo(cc[0]+=cx, cc[1]+=cy, cc[2]+=cx, cc[3]+=cy, cc[4]+=cx, cc[5]+=cy);
							ccx = cc[2]; ccy = cc[3]; cx = cc[4]; cy = cc[5]; break;
						case 'S':
							path.curveTo(cx*2-ccx, cy*2-ccy, cc[0], cc[1], cc[2], cc[3]);
							ccx = cc[0]; ccy = cc[1]; cx = cc[2]; cy = cc[3]; break;
						case 's':
							path.curveTo(cx*2-ccx, cy*2-ccy, cc[0]+=cx, cc[1]+=cy, cc[2]+=cx, cc[3]+=cx);
							ccx = cc[0]; ccy = cc[1]; cx = cc[2]; cy = cc[3]; break;
						case 'Q':
							path.quadTo(cc[0], cc[1], cc[2], cc[3]);
							ccx = cc[0]; ccy = cc[1]; cx = cc[2]; cy = cc[3]; break;
						case 'q':
							path.quadTo(cc[0]+=cx, cc[1]+=cy, cc[2]+=cx, cc[3]+=cy);
							ccx = cc[0]; ccy = cc[1]; cx = cc[2]; cy = cc[3]; break;
						case 'T':
							path.quadTo(cx*2-ccx, cy*2-ccy, cc[0], cc[1]);
							ccx = cx*2-ccx; ccy = cy*2-ccy; cx = cc[0]; cy = cc[1]; break;
						case 't':
							path.quadTo(cx*2-ccx, cy*2-ccy, cc[0]+=cx, cc[1]+=cy);
							ccx = cx*2-ccx; ccy = cy*2-ccy; cx = cc[0]; cy = cc[1]; break;
					}
				}
			}
		}
		return path;
	}
	
	private static String toSVGPath(Shape shape, float r) {
		StringBuffer s = new StringBuffer();
		float[] c = new float[6];
		for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
			switch (i.currentSegment(c)) {
				case PathIterator.SEG_MOVETO:
					s.append(" M "); s.append(ftoa(c[0], r));
					s.append(" "); s.append(ftoa(c[1], r));
					break;
				case PathIterator.SEG_LINETO:
					s.append(" L "); s.append(ftoa(c[0], r));
					s.append(" "); s.append(ftoa(c[1], r));
					break;
				case PathIterator.SEG_QUADTO:
					s.append(" Q "); s.append(ftoa(c[0], r));
					s.append(" "); s.append(ftoa(c[1], r));
					s.append(" "); s.append(ftoa(c[2], r));
					s.append(" "); s.append(ftoa(c[3], r));
					break;
				case PathIterator.SEG_CUBICTO:
					s.append(" C "); s.append(ftoa(c[0], r));
					s.append(" "); s.append(ftoa(c[1], r));
					s.append(" "); s.append(ftoa(c[2], r));
					s.append(" "); s.append(ftoa(c[3], r));
					s.append(" "); s.append(ftoa(c[4], r));
					s.append(" "); s.append(ftoa(c[5], r));
					break;
				case PathIterator.SEG_CLOSE:
					s.append(" Z");
					break;
			}
		}
		return s.toString().trim();
	}
	
	private static String ftoa(float v, float r) {
		v = Math.round(v * r) / r;
		if (v == (int)v) return Integer.toString((int)v);
		return Float.toString(v);
	}
	
	private static String viewBox(Rectangle r) {
		return (
			" width=\"" + r.width + "\" height=\"" + r.height + "\"" +
			" viewBox=\"" + r.x + " " + r.y + " " + r.width + " " + r.height + "\""
		);
	}
	
	public static void main(String[] args) {
		KeyCapStyle cs = KeyCapStyle.KCAP_KBIT;
		for (String arg : args) {
			Shape shape = fromSVGPath(arg);
			String vbox = viewBox(shape.getBounds());
			Area[] area = cs.layeredAreaFromShape(null, shape);
			String path = cs.layeredAreaToSVGPaths(area);
			System.out.println("<?xml version=\"1.0\"?>");
			System.out.println("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"" + vbox + ">");
			System.out.println("<!-- path d=\"" + arg + "\" -->");
			System.out.println(path.trim());
			System.out.println("</svg>");
		}
	}
}
