package com.kreative.keycaps;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyCapLayout extends ArrayList<KeyCap> {
	private static final long serialVersionUID = 1L;
	
	private static final String FLOAT_TOKEN_STR = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?";
	private static final String LOCATION_TOKEN_STR = (
		"((?<x>" + FLOAT_TOKEN_STR + ")\\s*)?,\\s*" +
		"((?<y>" + FLOAT_TOKEN_STR + ")\\s*)?" +
		"(?<u>([A-Za-z]+|/\\s*(" + FLOAT_TOKEN_STR + ")))?"
	);
	private static final Pattern ITEM_PATTERN = Pattern.compile(
		"(?<loc>(^|[@;])\\s*" + LOCATION_TOKEN_STR + ")|" +
		"(?<key>[:,]\\s*(?<k>" + KeyCap.SPEC_PATTERN_STRING + "))"
	);
	private static final float DECIMAL_PLACES = 1000;
	
	public void parse(String s) {
		float x = 0, y = 0, keyCapSize = 1;
		Matcher m = ITEM_PATTERN.matcher(s);
		while (m.find()) {
			String loc = m.group("loc");
			if (loc != null && loc.length() > 0) {
				String xs = m.group("x");
				String ys = m.group("y");
				String us = m.group("u");
				if (us != null && us.length() > 0) {
					float u = KeyCapShape.parseUnit(us, KeyCapShape.U);
					x *= u / keyCapSize;
					y *= u / keyCapSize;
					keyCapSize = u;
				}
				if (ys != null && ys.length() > 0) y = Float.parseFloat(ys);
				if (xs != null && xs.length() > 0) x = Float.parseFloat(xs);
			}
			String key = m.group("key");
			if (key != null && key.length() > 0) {
				String ks = m.group("k");
				KeyCap k = new KeyCap(x, y, keyCapSize, ks);
				x += k.getShape().getAdvanceWidth(keyCapSize);
				add(k);
			}
		}
	}
	
	public Rectangle2D.Float getBounds(float keyCapSize) {
		boolean first = true;
		float minx = 0;
		float miny = 0;
		float maxx = 0;
		float maxy = 0;
		for (KeyCap k : this) {
			Rectangle2D.Float kb = k.getShape().getBounds(keyCapSize);
			float kminx = kb.x + k.getX(keyCapSize);
			float kminy = kb.y - k.getY(keyCapSize);
			float kmaxx = kminx + kb.width;
			float kmaxy = kminy + kb.height;
			if (first) {
				first = false;
				minx = kminx;
				miny = kminy;
				maxx = kmaxx;
				maxy = kmaxy;
			} else {
				if (kminx < minx) minx = kminx;
				if (kminy < miny) miny = kminy;
				if (kmaxx > maxx) maxx = kmaxx;
				if (kmaxy > maxy) maxy = kmaxy;
			}
		}
		return new Rectangle2D.Float(minx, miny, maxx-minx, maxy-miny);
	}
	
	public String toSVG(KeyCapStyle cs, float csScale, KeyCapEngraver ce, float keyCapSize) {
		String vbox = viewBox(getBounds(keyCapSize), 0, DECIMAL_PLACES);
		StringBuffer shapeDefs = new StringBuffer();
		StringBuffer pathDefs = new StringBuffer();
		StringBuffer keyboard = new StringBuffer();
		HashMap<KeyCapShape,Integer> shapes = new HashMap<KeyCapShape,Integer>();
		HashMap<String,Integer> paths = new HashMap<String,Integer>();
		Collections.sort(this);
		for (KeyCap k : this) {
			Integer shapeId = shapes.get(k.getShape());
			if (shapeId == null) {
				shapes.put(k.getShape(), (shapeId = shapes.size()));
				Shape shape = k.getShape().toAWTShape(keyCapSize / csScale);
				shapeDefs.append("<g id=\"shape" + shapeId + "\">\n");
				shapeDefs.append(cs.layeredAreaToSVGPaths(cs.layeredAreaFromShape(null, shape)));
				shapeDefs.append("</g>\n");
			}
			keyboard.append("<g class=\"key\">\n");
			String tx = "translate(" + ftoa(+k.getX(keyCapSize), DECIMAL_PLACES) + " " + ftoa(-k.getY(keyCapSize), DECIMAL_PLACES) + ")";
			if (csScale != 1) tx += " scale(" + ftoa(csScale, DECIMAL_PLACES) + " " + ftoa(csScale, DECIMAL_PLACES) + ")";
			keyboard.append("<use xlink:href=\"#shape" + shapeId + "\" transform=\"" + tx + "\"/>\n");
			for (KeyCapEngraver.TextBox tb : ce.makeBoxes(cs, csScale, k.getShape(), keyCapSize, k.getLegend())) {
				if (tb.path != null && tb.path.length() > 0) {
					Integer pathId = paths.get(tb.path);
					if (pathId == null) {
						paths.put(tb.path, (pathId = paths.size()));
						pathDefs.append("<path id=\"path" + pathId + "\" d=\"" + tb.path + "\"/>\n");
					}
					float x = tb.anchor.getX(tb.x, tb.lineHeight) + k.getX(keyCapSize);
					float y = tb.anchor.getY(tb.y, tb.lineHeight) - k.getY(keyCapSize);
					tx = (
						"translate(" + ftoa(x, DECIMAL_PLACES) + " " +
						ftoa(y, DECIMAL_PLACES) + ") scale(" +
						ftoa(tb.lineHeight, DECIMAL_PLACES) + " " +
						ftoa(tb.lineHeight, DECIMAL_PLACES) + ")"
					);
					keyboard.append(
						"<use xlink:href=\"#path" + pathId + "\" transform=\"" +
						tx + "\" fill=\"" + cs.getTextColor() + "\"/>\n"
					);
				} else if (tb.text != null && tb.text.length() > 0) {
					float x = tb.x + k.getX(keyCapSize);
					String a = tb.anchor.getTextAnchor();
					String[] lines = tb.text.split("\r\n|\r|\n");
					for (int i = 0, n = lines.length; i < n; i++) {
						float y = tb.anchor.getY(tb.y, tb.lineHeight*n) + tb.lineHeight*i + tb.lineHeight*0.8f - k.getY(keyCapSize);
						String t = xmlSpecialChars(lines[i]);
						keyboard.append(
							"<text x=\"" + ftoa(x, DECIMAL_PLACES) + "\" y=\"" +
							ftoa(y, DECIMAL_PLACES) + "\" text-anchor=\"" + a +
							"\" font-family=\"Arial\" font-size=\"" +
							ftoa(tb.lineHeight, DECIMAL_PLACES) + "\" fill=\"" +
							cs.getTextColor() + "\">" + t + "</text>\n"
						);
					}
				}
			}
			keyboard.append("</g>\n");
		}
		StringBuffer svg = new StringBuffer();
		svg.append("<?xml version=\"1.0\"?>\n");
		svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\"" + vbox + ">\n");
		svg.append("<defs>\n");
		svg.append(shapeDefs.toString());
		svg.append(pathDefs.toString());
		svg.append("</defs>\n");
		svg.append("<g class=\"keyboard\">\n");
		svg.append(keyboard.toString());
		svg.append("</g>\n");
		svg.append("</svg>\n");
		return svg.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(this);
		for (KeyCap k : this) {
			if (sb.length() == 0 || !k.isAt(x, y, keyCapSize)) {
				if (sb.length() > 0) sb.append(";\n");
				keyCapSize = k.getKeyCapSize();
				sb.append('@');
				sb.append(ftoa((x = k.getX(keyCapSize)), DECIMAL_PLACES));
				sb.append(',');
				sb.append(ftoa((y = k.getY(keyCapSize)), DECIMAL_PLACES));
				sb.append(KeyCapShape.unitToString(keyCapSize, DECIMAL_PLACES));
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		return sb.toString();
	}
	
	public String toNormalizedString() {
		StringBuffer sb = new StringBuffer();
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(this);
		for (KeyCap k : this) {
			if (sb.length() == 0 || !k.isAt(x, y, keyCapSize)) {
				if (sb.length() > 0) sb.append(";\n");
				// keyCapSize = k.getKeyCapSize();
				sb.append('@');
				sb.append(ftoa((x = k.getX(keyCapSize)), DECIMAL_PLACES));
				sb.append(',');
				sb.append(ftoa((y = k.getY(keyCapSize)), DECIMAL_PLACES));
				sb.append(KeyCapShape.unitToString(keyCapSize, DECIMAL_PLACES));
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toNormalizedString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		return sb.toString();
	}
	
	public String toMinimizedString() {
		StringBuffer sb = new StringBuffer();
		float x = 0, y = 0, keyCapSize = 1;
		Collections.sort(this);
		for (KeyCap k : this) {
			if (sb.length() == 0 || !k.isAt(x, y, keyCapSize)) {
				if (sb.length() > 0) sb.append(";");
				keyCapSize = k.getMinimalKeyCapSize();
				sb.append('@');
				sb.append(ftoa((x = k.getX(keyCapSize)), DECIMAL_PLACES));
				sb.append(',');
				sb.append(ftoa((y = k.getY(keyCapSize)), DECIMAL_PLACES));
				sb.append(KeyCapShape.unitToString(keyCapSize, DECIMAL_PLACES));
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(k.toMinimizedString());
			x += k.getShape().getAdvanceWidth(keyCapSize);
		}
		if (sb.length() > 0) sb.append(";");
		return sb.toString();
	}
	
	private static String ftoa(float v, float r) {
		v = Math.round(v * r) / r;
		if (v == (int)v) return Integer.toString((int)v);
		return Float.toString(v);
	}
	
	private static String viewBox(Rectangle2D.Float bounds, float pad, float r) {
		return (
			" width=\"" + ftoa(bounds.width+pad+pad, r) + "\"" +
			" height=\"" + ftoa(bounds.height+pad+pad, r) + "\"" +
			" viewBox=\"" + ftoa(bounds.x-pad, r) + " " + ftoa(bounds.y-pad, r) +
			" " + ftoa(bounds.width+pad+pad, r) + " " + ftoa(bounds.height+pad+pad, r) + "\""
		);
	}
	
	private static String xmlSpecialChars(String s) {
		StringBuffer sb = new StringBuffer();
		int i = 0, n = s.length();
		while (i < n) {
			int ch = s.codePointAt(i);
			switch (ch) {
				case '&': sb.append("&amp;"); break;
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '\"': sb.append("&#34;"); break;
				case '\'': sb.append("&#39;"); break;
				default:
					if (ch >= 0x20 && ch < 0x7F) sb.append((char)ch);
					else sb.append("&#" + ch + ";");
					break;
			}
			i += Character.charCount(ch);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		KeyCapLayout kcl = new KeyCapLayout();
		String outputPath = null;
		boolean parseOutputPath = false;
		boolean parseArg = false;
		boolean parseFile = false;
		for (String arg : args) {
			if (parseOutputPath) {
				outputPath = arg;
				parseOutputPath = false;
			} else if (parseArg) {
				kcl.parse(arg);
				parseArg = false;
			} else if (parseFile) {
				StringBuffer contents = new StringBuffer();
				Scanner scanner = new Scanner(new File(arg), "UTF-8");
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine().trim();
					if (line.length() == 0 || line.startsWith("#")) continue;
					contents.append(line + "\n");
				}
				scanner.close();
				kcl.parse(contents.toString());
				parseFile = false;
			} else if (arg.startsWith("-")) {
				if (arg.equals("-i")) {
					StringBuffer contents = new StringBuffer();
					Scanner scanner = new Scanner(System.in, "UTF-8");
					while (scanner.hasNextLine()) {
						String line = scanner.nextLine().trim();
						if (line.length() == 0 || line.startsWith("#")) continue;
						contents.append(line + "\n");
					}
					scanner.close();
					kcl.parse(contents.toString());
				} else if (arg.equals("-f")) {
					parseFile = true;
				} else if (arg.equals("-c")) {
					parseArg = true;
				} else if (arg.equals("-o")) {
					parseOutputPath = true;
				} else {
					System.err.println("Unknown option: " + arg);
					return;
				}
			} else {
				kcl.parse(arg);
			}
		}
		PrintWriter out;
		if (outputPath != null) {
			FileOutputStream os = new FileOutputStream(new File(outputPath));
			out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
		}
		out.println(kcl.toSVG(KeyCapStyle.KCAP_ICL8, 1, KeyCapEngraver.DEFAULT, 48));
		out.println("<!-- toString():");
		String s = kcl.toString();
		out.println(s);
		out.println("-->");
		out.println("<!-- toNormalizedString():");
		String n = kcl.toNormalizedString();
		out.println(n);
		out.println("-->");
		out.println("<!-- toMinimizedString():");
		String m = kcl.toMinimizedString();
		out.println(m);
		out.println("-->");
		out.println("<!-- Tests:");
		KeyCapLayout skcl = new KeyCapLayout(); skcl.parse(s);
		check(out, skcl.toString(), s, "parse(s).toString() != s");
		check(out, skcl.toNormalizedString(), n, "parse(s).toNormalizedString() != n");
		check(out, skcl.toMinimizedString(), m, "parse(s).toMinimizedString() != m");
		KeyCapLayout nkcl = new KeyCapLayout(); nkcl.parse(n);
		check(out, nkcl.toNormalizedString(), n, "parse(n).toNormalizedString() != n");
		check(out, nkcl.toMinimizedString(), m, "parse(n).toMinimizedString() != m");
		KeyCapLayout mkcl = new KeyCapLayout(); mkcl.parse(m);
		check(out, mkcl.toNormalizedString(), n, "parse(m).toNormalizedString() != n");
		check(out, mkcl.toMinimizedString(), m, "parse(m).toMinimizedString() != m");
		out.println("-->");
		if (outputPath != null) out.close();
	}
	
	private static void check(PrintWriter out, String actual, String expected, String failMsg) {
		boolean passes = actual.equals(expected);
		out.println(passes ? "PASS" : ("FAIL: " + failMsg));
		if (!passes) {
			for (String line : expected.split("\n")) {
				out.println("-" + line);
			}
			for (String line : actual.split("\n")) {
				out.println("+" + line);
			}
		}
	}
}