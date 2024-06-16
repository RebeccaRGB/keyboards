package com.kreative.keycaps;

import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

public class AWTPathDefs {
	private final Map<String,Shape> pathShapes = new HashMap<String,Shape>();
	
	public Shape getPathShape(String path) {
		Shape shape = pathShapes.get(path);
		if (shape == null) {
			shape = ShapeUtilities.fromSVGPath(path);
			pathShapes.put(path, shape);
		}
		return shape;
	}
}
