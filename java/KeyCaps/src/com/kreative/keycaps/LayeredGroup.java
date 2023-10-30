package com.kreative.keycaps;

import java.awt.Color;
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
	
	public String toSVG(String prefix, String indent) {
		StringBuffer sb = new StringBuffer(prefix);
		sb.append("<g");
		if (color != null) {
			sb.append(" fill=\"#");
			sb.append(Integer.toHexString(0xFF000000 | color.getRGB()).substring(2));
			sb.append("\"");
		}
		if (opacity != null) {
			sb.append(" opacity=\"");
			sb.append(opacity);
			sb.append("\"");
		}
		sb.append(">\n");
		for (LayeredObject o : contents) {
			sb.append(o.toSVG(prefix + indent, indent));
			sb.append("\n");
		}
		sb.append(prefix);
		sb.append("</g>");
		return sb.toString();
	}
}
