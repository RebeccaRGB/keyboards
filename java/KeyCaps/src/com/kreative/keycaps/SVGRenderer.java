package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.colorToString;
import static com.kreative.keycaps.KeyCapUnits.ROUNDING;
import static com.kreative.keycaps.KeyCapUnits.valueToString;
import static com.kreative.keycaps.KeyCapUnits.valuesToString;
import static com.kreative.keycaps.ShapeUtilities.toSVGViewBox;
import static com.kreative.keycaps.StringUtilities.stringWidth;
import static com.kreative.keycaps.StringUtilities.xmlSpecialChars;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.Collections;

public class SVGRenderer {
	private final KeyCapMold mold;
	private final float moldScale;
	private final float keyCapSize;
	private final KeyCapEngraver e;
	
	public SVGRenderer(KeyCapMold mold, float moldScale, float keyCapSize, KeyCapEngraver e) {
		this.mold = mold;
		this.moldScale = moldScale;
		this.keyCapSize = keyCapSize;
		this.e = e;
	}
	
	public String render(KeyCapLayout layout) {
		String vbox = toSVGViewBox(layout.getBounds(keyCapSize), 0, ROUNDING);
		SVGShapeDefs shapeDefs = new SVGShapeDefs(mold, moldScale, keyCapSize);
		SVGPathDefs pathDefs = new SVGPathDefs();
		StringBuffer keyboard = new StringBuffer();
		
		PropertyMap lp = layout.getPropertyMap();
		String lclass = lp.containsAny("class") ? lp.getString("class") : null;
		String lid = lp.containsAny("id") ? lp.getString("id") : null;
		Color lcc = lp.containsAny("cc") ? lp.getColor("cc") : null;
		Float lco = lp.containsAny("co") ? lp.getOpacity("co") : null;
		Color llc = lp.containsAny("lc") ? lp.getColor("lc") : null;
		Float llo = lp.containsAny("lo") ? lp.getOpacity("lo") : null;
		Anchor lha = lp.containsAny("ha", "a") ? lp.getAnchor("ha", "a") : null;
		Anchor lva = lp.containsAny("va", "a") ? lp.getAnchor("va", "a") : null;
		
		keyboard.append("<g class=\"keyboard");
		if (lclass != null && lclass.length() > 0) keyboard.append(" " + lclass);
		keyboard.append("\"");
		if (lid != null && lid.length() > 0) keyboard.append(" id=\"" + lid + "\"");
		keyboard.append(">\n");
		
		Collections.sort(layout);
		for (KeyCap k : layout) {
			PropertyMap kp = k.getPropertyMap();
			String kclass = kp.containsAny("class") ? kp.getString("class") : null;
			String kid = kp.containsAny("id") ? kp.getString("id") : null;
			Color kcc = kp.containsAny("cc") ? kp.getColor("cc") : lcc;
			Float kco = kp.containsAny("co") ? kp.getOpacity("co") : lco;
			Color klc = kp.containsAny("lc") ? kp.getColor("lc") : llc;
			Float klo = kp.containsAny("lo") ? kp.getOpacity("lo") : llo;
			Anchor kha = kp.containsAny("ha", "a") ? kp.getAnchor("ha", "a") : lha;
			Anchor kva = kp.containsAny("va", "a") ? kp.getAnchor("va", "a") : lva;
			
			keyboard.append("<g class=\"key");
			if (kclass != null && kclass.length() > 0) keyboard.append(" " + kclass);
			keyboard.append("\"");
			if (kid != null && kid.length() > 0) keyboard.append(" id=\"" + kid + "\"");
			keyboard.append(">\n");
			
			KeyCapShape shape = k.getShape();
			String shapeID = shapeDefs.getShapeID(shape, kcc, kco);
			Point2D.Float pos = k.getPosition().getLocation(keyCapSize);
			String tx = "translate(" + valuesToString(" ", +pos.x, -pos.y) + ")";
			if (moldScale != 1) tx += " scale(" + valuesToString(" ", moldScale, moldScale) + ")";
			keyboard.append("<use xlink:href=\"#" + shapeID + "\" transform=\"" + tx + "\"/>\n");
			
			KeyCapLegend legend = k.getLegend();
			PropertyMap jp = legend.getPropertyMap();
			Color jlc = jp.containsAny("lc") ? jp.getColor("lc") : klc;
			Float jlo = jp.containsAny("lo") ? jp.getOpacity("lo") : klo;
			Anchor jha = jp.containsAny("ha", "a") ? jp.getAnchor("ha", "a") : kha;
			Anchor jva = jp.containsAny("va", "a") ? jp.getAnchor("va", "a") : kva;
			
			for (KeyCapEngraver.TextBox tb : e.makeBoxes(mold, moldScale, shape, keyCapSize, legend)) {
				if (tb == null || tb.item == null) continue;
				PropertyMap ip = tb.item.getPropertyMap();
				Color ilc = ip.containsAny("lc") ? ip.getColor("lc") : jlc;
				Float ilo = ip.containsAny("lo") ? ip.getOpacity("lo") : jlo;
				Anchor iha = ip.containsAny("ha", "a") ? ip.getAnchor("ha", "a") : jha;
				Anchor iva = ip.containsAny("va", "a") ? ip.getAnchor("va", "a") : jva;
				
				StringBuffer tas = new StringBuffer();
				String tcs = textColorString(ilc, kcc);
				if (tcs != null) tas.append(" fill=\"" + tcs + "\"");
				String tos = (ilo != null) ? valueToString(ilo) : null;
				if (tos != null) tas.append(" opacity=\"" + tos + "\"");
				
				if (tb.path != null && tb.path.length() > 0) {
					String pathID = pathDefs.getPathID(tb.path);
					float x = ((iha != null) ? iha : tb.anchor).getX(tb.x, tb.lineHeight) + pos.x;
					float y = ((iva != null) ? iva : tb.anchor).getY(tb.y, tb.lineHeight) - pos.y;
					float w = tb.lineHeight;
					float h = tb.lineHeight;
					tx = "translate(" + valuesToString(" ", x, y) + ") scale(" + valuesToString(" ", w, h) + ")";
					keyboard.append("<use xlink:href=\"#" + pathID + "\" transform=\"" + tx + "\"" + tas + "/>\n");
				}
				
				if (tb.text != null && tb.text.length() > 0) {
					String[] lines = tb.text.split("\r\n|\r|\n");
					float th = tb.lineHeight * lines.length;
					float ta = tb.lineHeight * 0.8f - pos.y;
					float x = tb.x + pos.x;
					float y = ((iva != null) ? iva : tb.anchor).getY(tb.y, th) + ta;
					String a = ((iha != null) ? iha : tb.anchor).getTextAnchor();
					for (int i = 0; i < lines.length; i++) {
						keyboard.append("<text");
						keyboard.append(" x=\"" + valueToString(x) + "\"");
						keyboard.append(" y=\"" + valueToString(y + tb.lineHeight * i) + "\"");
						keyboard.append(" text-anchor=\"" + a + "\"");
						keyboard.append(" font-family=\"Arial\"");
						keyboard.append(" font-size=\"" + valueToString(tb.lineHeight) + "\"");
						if (stringWidth(lines[i], "Arial", Font.PLAIN, tb.lineHeight) > tb.maxWidth) {
							keyboard.append(" textLength=\"" + valueToString(tb.maxWidth) + "\"");
							keyboard.append(" lengthAdjust=\"spacingAndGlyphs\"");
						}
						keyboard.append(tas);
						keyboard.append(">");
						keyboard.append(xmlSpecialChars(lines[i]));
						keyboard.append("</text>\n");
					}
				}
			}
			
			keyboard.append("</g>\n");
		}
		
		keyboard.append("</g>\n");
		
		StringBuffer svg = new StringBuffer();
		svg.append("<?xml version=\"1.0\"?>\n");
		svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\"");
		svg.append(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"");
		svg.append(" version=\"1.1\"" + vbox + ">\n");
		svg.append("<defs>\n");
		svg.append(shapeDefs.toString());
		svg.append(pathDefs.toString());
		svg.append("</defs>\n");
		svg.append(keyboard.toString());
		svg.append("</svg>\n");
		return svg.toString();
	}
	
	private String textColorString(Color itemColor, Color capColor) {
		if (itemColor != null) return colorToString(itemColor, "black");
		if (capColor != null) return colorToString(mold.getDefaultLegendColor(capColor), "black");
		return colorToString(mold.getDefaultLegendColor(mold.getDefaultKeyCapColor()), "black");
	}
}
