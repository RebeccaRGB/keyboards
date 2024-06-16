package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class LayeredShape extends LayeredObject {
	public final Shape shape;
	public final Color color;
	public final Float opacity;
	
	public LayeredShape(Shape shape, Color color, Float opacity) {
		this.shape = shape;
		this.color = color;
		this.opacity = opacity;
	}
	
	public String toSVG(String prefix, String indent, float rounding) {
		StringBuffer sb = new StringBuffer(prefix);
		sb.append("<path d=\"");
		sb.append(ShapeUtilities.toSVGPath(shape, null, rounding));
		if (color != null) {
			sb.append("\" fill=\"");
			sb.append(ColorUtilities.colorToString(color, null));
		}
		if (opacity != null) {
			sb.append("\" opacity=\"");
			sb.append(opacity);
		}
		sb.append("\"/>");
		return sb.toString();
	}
	
	public void paint(Graphics2D g, AffineTransform tx) {
		Color base = g.getColor();
		g.setColor(ColorUtilities.overrideColor(base, color, opacity));
		g.fill((tx != null) ? tx.createTransformedShape(shape) : shape);
		g.setColor(base);
	}
}
