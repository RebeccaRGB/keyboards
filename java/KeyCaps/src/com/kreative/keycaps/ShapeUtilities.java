package com.kreative.keycaps;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeUtilities {
	public static Shape add(Shape base, Shape... args) {
		Area area = new Area(base);
		for (Shape arg : args) area.add(new Area(arg));
		return area;
	}
	
	public static Shape subtract(Shape base, Shape... args) {
		Area area = new Area(base);
		for (Shape arg : args) area.subtract(new Area(arg));
		return area;
	}
	
	public static Shape intersect(Shape base, Shape... args) {
		Area area = new Area(base);
		for (Shape arg : args) area.intersect(new Area(arg));
		return area;
	}
	
	public static Shape exclusiveOr(Shape base, Shape... args) {
		Area area = new Area(base);
		for (Shape arg : args) area.exclusiveOr(new Area(arg));
		return area;
	}
	
	public static Shape expand(Shape shape, float width) {
		if (shape == null || width <= 0) return shape;
		BasicStroke stroke = new BasicStroke(width * 2);
		Area expanded = new Area(shape);
		expanded.add(new Area(stroke.createStrokedShape(shape)));
		return expanded;
	}
	
	public static Shape contract(Shape shape, float width) {
		if (shape == null || width <= 0) return shape;
		BasicStroke stroke = new BasicStroke(width * 2);
		Area contracted = new Area(shape);
		contracted.subtract(new Area(stroke.createStrokedShape(shape)));
		return contracted;
	}
	
	public static Shape roundCorners(Shape shape, float radius) {
		if (shape == null || radius <= 0) return shape;
		BasicStroke ss = new BasicStroke(radius * 2);
		BasicStroke rs = new BasicStroke(radius * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		Area rounded = new Area(shape);
		rounded.subtract(new Area(ss.createStrokedShape(shape)));
		rounded.add(new Area(rs.createStrokedShape(rounded)));
		return rounded;
	}
	
	public static Shape strokeCenter(Shape shape, float width) {
		if (shape == null || width <= 0) return null;
		BasicStroke stroke = new BasicStroke(width);
		return stroke.createStrokedShape(shape);
	}
	
	public static Shape strokeInside(Shape shape, float width) {
		if (shape == null || width <= 0) return null;
		BasicStroke stroke = new BasicStroke(width * 2);
		Area stroked = new Area(stroke.createStrokedShape(shape));
		stroked.intersect(new Area(shape));
		return stroked;
	}
	
	public static Shape strokeOutside(Shape shape, float width) {
		if (shape == null || width <= 0) return null;
		BasicStroke stroke = new BasicStroke(width * 2);
		Area stroked = new Area(stroke.createStrokedShape(shape));
		stroked.subtract(new Area(shape));
		return stroked;
	}
	
	public static Shape translate(Shape shape, float dx, float dy) {
		if (shape == null || (dx == 0 && dy == 0)) return shape;
		AffineTransform tx = AffineTransform.getTranslateInstance(dx, dy);
		return tx.createTransformedShape(shape);
	}
	
	public static Rectangle2D getWidestRect(Shape shape, AffineTransform tx) {
		if (shape == null) return null;
		SortedSet<Float> xCoords = new TreeSet<Float>();
		SortedSet<Float> yCoords = new TreeSet<Float>();
		float[] c = new float[6];
		for (PathIterator i = shape.getPathIterator(tx); !i.isDone(); i.next()) {
			switch (i.currentSegment(c)) {
				case PathIterator.SEG_MOVETO:
				case PathIterator.SEG_LINETO:
					xCoords.add(c[0]);
					yCoords.add(c[1]);
					break;
				case PathIterator.SEG_QUADTO:
					xCoords.add(c[2]);
					yCoords.add(c[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					xCoords.add(c[4]);
					yCoords.add(c[5]);
					break;
			}
		}
		Rectangle2D maxRect = null;
		for (float y0 : yCoords) for (float y1 : yCoords) if (y1 > y0) {
			for (float x0 : xCoords) for (float x1 : xCoords) if (x1 > x0) {
				Rectangle2D r = new Rectangle2D.Float(x0, y0, x1 - x0, y1 - y0);
				if (shape.contains(r)) {
					if (maxRect == null || r.getWidth() > maxRect.getWidth()) {
						maxRect = r;
					} else if (r.getWidth() < maxRect.getWidth()) {
						continue;
					} else if (r.getHeight() > maxRect.getHeight()) {
						maxRect = r;
					}
				}
			}
		}
		return maxRect;
	}
	
	private static String valueToString(float value, float rounding) {
		value = Math.round(value * rounding) / rounding;
		if (value == (int)value) return Integer.toString((int)value);
		return Float.toString(value);
	}
	
	public static String toSVGViewBox(Shape shape, float padding, float rounding) {
		Rectangle2D bounds = shape.getBounds2D();
		String xs = valueToString((float)bounds.getX() - padding, rounding);
		String ys = valueToString((float)bounds.getY() - padding, rounding);
		String ws = valueToString((float)bounds.getWidth() + padding * 2, rounding);
		String hs = valueToString((float)bounds.getHeight() + padding * 2, rounding);
		return (
			" width=\"" + ws + "\" height=\"" + hs + "\"" +
			" viewBox=\"" + xs + " " + ys + " " + ws + " " + hs + "\""
		);
	}
	
	public static String toSVGPath(Shape shape, AffineTransform tx, float rounding) {
		StringBuffer s = new StringBuffer();
		float[] c = new float[6];
		for (PathIterator i = shape.getPathIterator(tx); !i.isDone(); i.next()) {
			switch (i.currentSegment(c)) {
				case PathIterator.SEG_MOVETO:
					s.append(" M "); s.append(valueToString(c[0], rounding));
					s.append(" "); s.append(valueToString(c[1], rounding));
					break;
				case PathIterator.SEG_LINETO:
					s.append(" L "); s.append(valueToString(c[0], rounding));
					s.append(" "); s.append(valueToString(c[1], rounding));
					break;
				case PathIterator.SEG_QUADTO:
					s.append(" Q "); s.append(valueToString(c[0], rounding));
					s.append(" "); s.append(valueToString(c[1], rounding));
					s.append(" "); s.append(valueToString(c[2], rounding));
					s.append(" "); s.append(valueToString(c[3], rounding));
					break;
				case PathIterator.SEG_CUBICTO:
					s.append(" C "); s.append(valueToString(c[0], rounding));
					s.append(" "); s.append(valueToString(c[1], rounding));
					s.append(" "); s.append(valueToString(c[2], rounding));
					s.append(" "); s.append(valueToString(c[3], rounding));
					s.append(" "); s.append(valueToString(c[4], rounding));
					s.append(" "); s.append(valueToString(c[5], rounding));
					break;
				case PathIterator.SEG_CLOSE:
					s.append(" Z");
					break;
			}
		}
		return s.toString().trim();
	}
	
	private static final Pattern SVG_PATH_TOKEN = Pattern.compile(
		"([A-Za-z])|([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?)");
	
	public static Shape fromSVGPath(String d) {
		float sx = 0, sy = 0;
		float cx = 0, cy = 0;
		float ccx = 0, ccy = 0;
		float[] cc = new float[8];
		int ci = 0, cn = 2;
		char cm = 'M';
		GeneralPath path = new GeneralPath();
		Matcher m = SVG_PATH_TOKEN.matcher(d);
		while (m.find()) {
			if (m.group(1) != null && m.group(1).length() > 0) {
				cm = m.group(1).charAt(0);
				if (cm == 'Z' || cm == 'z') {
					path.closePath();
					ccx = cx = sx;
					ccy = cy = sy;
					cm -= 13;
				}
				switch (cm) {
					case 'M': case 'm': cn = 2; break;
					case 'L': case 'l': cn = 2; break;
					case 'H': case 'h': cn = 1; break;
					case 'V': case 'v': cn = 1; break;
					case 'C': case 'c': cn = 6; break;
					case 'S': case 's': cn = 4; break;
					case 'Q': case 'q': cn = 4; break;
					case 'T': case 't': cn = 2; break;
					case 'A': case 'a': cn = 7; break;
					default: cn = 0; break;
				}
				ci = 0;
			}
			if (m.group(2) != null && m.group(2).length() > 0) {
				cc[ci++] = Float.parseFloat(m.group(2));
				if (ci >= cn) {
					ci = 0;
					switch (cm) {
						case 'M':
							sx = ccx = cx = cc[0]; sy = ccy = cy = cc[1];
							path.moveTo(cx, cy); cm--; break;
						case 'm':
							sx = ccx = cx += cc[0]; sy = ccy = cy += cc[1];
							path.moveTo(cx, cy); cm--; break;
						case 'L':
							ccx = cx = cc[0]; ccy = cy = cc[1];
							path.lineTo(cx, cy); break;
						case 'l':
							ccx = cx += cc[0]; ccy = cy += cc[1];
							path.lineTo(cx, cy); break;
						case 'H':
							ccx = cx = cc[0]; ccy = cy;
							path.lineTo(cx, cy); break;
						case 'h':
							ccx = cx += cc[0]; ccy = cy;
							path.lineTo(cx, cy); break;
						case 'V':
							ccx = cx; ccy = cy = cc[0];
							path.lineTo(cx, cy); break;
						case 'v':
							ccx = cx; ccy = cy += cc[0];
							path.lineTo(cx, cy); break;
						case 'C':
							path.curveTo(cc[0], cc[1], cc[2], cc[3], cc[4], cc[5]);
							ccx = cc[2]; ccy = cc[3]; cx = cc[4]; cy = cc[5]; break;
						case 'c':
							cc[0]+=cx; cc[1]+=cy; cc[2]+=cx; cc[3]+=cy; cc[4]+=cx; cc[5]+=cy;
							path.curveTo(cc[0], cc[1], cc[2], cc[3], cc[4], cc[5]);
							ccx = cc[2]; ccy = cc[3]; cx = cc[4]; cy = cc[5]; break;
						case 'S':
							path.curveTo(cx * 2 - ccx, cy * 2 - ccy, cc[0], cc[1], cc[2], cc[3]);
							ccx = cc[0]; ccy = cc[1]; cx = cc[2]; cy = cc[3]; break;
						case 's':
							cc[0] += cx; cc[1] += cy; cc[2] += cx; cc[3] += cy;
							path.curveTo(cx * 2 - ccx, cy * 2 - ccy, cc[0], cc[1], cc[2], cc[3]);
							ccx = cc[0]; ccy = cc[1]; cx = cc[2]; cy = cc[3]; break;
						case 'Q':
							ccx = cc[0]; ccy = cc[1]; cx = cc[2]; cy = cc[3];
							path.quadTo(ccx, ccy, cx, cy); break;
						case 'q':
							ccx = cx + cc[0]; ccy = cy + cc[1]; cx += cc[2]; cy += cc[3];
							path.quadTo(ccx, ccy, cx, cy); break;
						case 'T':
							ccx = cx * 2 - ccx; ccy = cy * 2 - ccy; cx = cc[0]; cy = cc[1];
							path.quadTo(ccx, ccy, cx, cy); break;
						case 't':
							ccx = cx * 2 - ccx; ccy = cy * 2 - ccy; cx += cc[0]; cy += cc[1];
							path.quadTo(ccx, ccy, cx, cy); break;
					}
				}
			}
		}
		return path;
	}
	
	public static Shape[] fromSVGPath(String[] d) {
		Shape[] shapes = new Shape[d.length];
		for (int i = 0; i < d.length; i++) {
			if (d[i] != null) {
				shapes[i] = fromSVGPath(d[i]);
			}
		}
		return shapes;
	}
	
	public static Shape[][] fromSVGPath(String[][] d) {
		Shape[][] shapes = new Shape[d.length][];
		for (int i = 0; i < d.length; i++) {
			if (d[i] != null) {
				shapes[i] = new Shape[d[i].length];
				for (int j = 0; j < d[i].length; j++) {
					if (d[i][j] != null) {
						shapes[i][j] = fromSVGPath(d[i][j]);
					}
				}
			}
		}
		return shapes;
	}
}
