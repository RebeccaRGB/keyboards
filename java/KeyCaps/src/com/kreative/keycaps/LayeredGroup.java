package com.kreative.keycaps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LayeredGroup extends LayeredObject {
	public final List<LayeredObject> contents;
	public final Color color;
	public final Float opacity;
	
	public LayeredGroup(Collection<LayeredObject> contents, Color color, Float opacity) {
		this.contents = new ArrayList<LayeredObject>(contents);
		this.color = color;
		this.opacity = opacity;
	}
	
	public String toSVG(String prefix, String indent, float rounding) {
		StringBuffer sb = new StringBuffer(prefix);
		sb.append("<g");
		if (color != null) {
			sb.append(" fill=\"");
			sb.append(ColorUtilities.colorToString(color, null));
			sb.append("\"");
		}
		if (opacity != null) {
			sb.append(" opacity=\"");
			sb.append(opacity);
			sb.append("\"");
		}
		sb.append(">\n");
		for (LayeredObject o : contents) {
			sb.append(o.toSVG(prefix + indent, indent, rounding));
			sb.append("\n");
		}
		sb.append(prefix);
		sb.append("</g>");
		return sb.toString();
	}
	
	public void paint(Graphics2D g, AffineTransform tx) {
		Color base = g.getColor();
		g.setColor(ColorUtilities.overrideColor(base, color, opacity));
		for (LayeredObject o : contents) o.paint(g, tx);
		g.setColor(base);
	}
}
