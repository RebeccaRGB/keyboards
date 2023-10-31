package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Shape;

public class LayeredShape extends LayeredObject {
	public final Shape shape;
	public final Color color;
	public final Float opacity;
	
	public LayeredShape(Shape shape, Color color, Float opacity) {
		this.shape = shape;
		this.color = color;
		this.opacity = opacity;
	}
	
	public String toSVG(String prefix, String indent) {
		StringBuffer sb = new StringBuffer(prefix);
		sb.append("<path d=\"");
		sb.append(ShapeUtilities.toSVGPath(shape, null, 1000));
		if (color != null) {
			sb.append("\" fill=\"#");
			sb.append(Integer.toHexString(0xFF000000 | color.getRGB()).substring(2));
		}
		if (opacity != null) {
			sb.append("\" opacity=\"");
			sb.append(opacity);
		}
		sb.append("\"/>");
		return sb.toString();
	}
}