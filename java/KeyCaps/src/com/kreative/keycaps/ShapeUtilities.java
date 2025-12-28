package com.kreative.keycaps;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ShapeUtilities {
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
	
	public static Shape simplify(Shape shape, AffineTransform tx) {
		if (shape == null) return null;
		ArrayList<float[]> segments = new ArrayList<float[]>();
		int seg; float startX = 0, startY = 0; float[] c = new float[6];
		for (PathIterator i = shape.getPathIterator(tx); !i.isDone(); i.next()) {
			switch (seg = i.currentSegment(c)) {
				case PathIterator.SEG_MOVETO:
					startX = c[0]; startY = c[1];
					// fallthrough;
				case PathIterator.SEG_LINETO:
					segments.add(new float[]{seg, c[0], c[1]});
					break;
				case PathIterator.SEG_QUADTO:
					segments.add(new float[]{seg, c[0], c[1], c[2], c[3]});
					break;
				case PathIterator.SEG_CUBICTO:
					segments.add(new float[]{seg, c[0], c[1], c[2], c[3], c[4], c[5]});
					break;
				case PathIterator.SEG_CLOSE:
					segments.add(new float[]{seg, startX, startY});
					break;
			}
		}
		seg = segments.size();
		for (int i = segments.size() - 3; i >= 0; i--) {
			float[] p0 = segments.get(i+0);
			float[] p1 = segments.get(i+1);
			float[] p2 = segments.get(i+2);
			if (p1[0] == PathIterator.SEG_LINETO && (
				p2[0] == PathIterator.SEG_LINETO ||
				p2[0] == PathIterator.SEG_CLOSE
			)) {
				float x01 = p1[p1.length-2] - p0[p0.length-2];
				float y01 = p1[p1.length-1] - p0[p0.length-1];
				float x12 = p2[p2.length-2] - p1[p1.length-2];
				float y12 = p2[p2.length-1] - p1[p1.length-1];
				if (x01*y12 == y01*x12) segments.remove(i+1);
			}
		}
		if (seg == segments.size()) return shape;
		GeneralPath path = new GeneralPath();
		for (float[] segment : segments) {
			switch ((int)(c = segment)[0]) {
				case PathIterator.SEG_MOVETO:
					path.moveTo(c[1], c[2]);
					break;
				case PathIterator.SEG_LINETO:
					path.lineTo(c[1], c[2]);
					break;
				case PathIterator.SEG_QUADTO:
					path.quadTo(c[1], c[2], c[3], c[4]);
					break;
				case PathIterator.SEG_CUBICTO:
					path.curveTo(c[1], c[2], c[3], c[4], c[5], c[6]);
					break;
				case PathIterator.SEG_CLOSE:
					path.closePath();
					break;
			}
		}
		return path;
	}
	
	public static final Comparator<Rectangle2D> WIDEST = new Comparator<Rectangle2D>() {
		public int compare(Rectangle2D a, Rectangle2D b) {
			if (a.getWidth() > b.getWidth()) return 1;
			if (a.getWidth() < b.getWidth()) return -1;
			if (a.getHeight() > b.getHeight()) return 1;
			if (a.getHeight() < b.getHeight()) return -1;
			return 0;
		}
	};
	
	public static final Comparator<Rectangle2D> TALLEST = new Comparator<Rectangle2D>() {
		public int compare(Rectangle2D a, Rectangle2D b) {
			if (a.getHeight() > b.getHeight()) return 1;
			if (a.getHeight() < b.getHeight()) return -1;
			if (a.getWidth() > b.getWidth()) return 1;
			if (a.getWidth() < b.getWidth()) return -1;
			return 0;
		}
	};
	
	public static Rectangle2D getWidestRect(Shape shape, AffineTransform tx) {
		return getMaxRect(shape, tx, WIDEST);
	}
	
	public static Rectangle2D getTallestRect(Shape shape, AffineTransform tx) {
		return getMaxRect(shape, tx, TALLEST);
	}
	
	public static Rectangle2D getMaxRect(Shape shape, AffineTransform tx, Comparator<Rectangle2D> cmp) {
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
					if (maxRect == null || cmp.compare(r, maxRect) > 0) {
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
						case 'A':
							ccx = cx = cc[5]; ccy = cy = cc[6];
							svgArcTo(path, cc[0], cc[1], cc[2], cc[3]!=0, cc[4]!=0, cx, cy); break;
						case 'a':
							ccx = cx += cc[5]; ccy = cy += cc[6];
							svgArcTo(path, cc[0], cc[1], cc[2], cc[3]!=0, cc[4]!=0, cx, cy); break;
					}
				}
			}
		}
		return path;
	}
	
	private static void svgArcTo(
		GeneralPath p, double rx, double ry, double a,
		boolean large, boolean sweep, double x, double y
	) {
		Point2D p0 = p.getCurrentPoint();
		if (p0 == null) throw new IllegalPathStateException("missing initial moveto in path definition");
		Shape arc = createSvgArc(p0.getX(), p0.getY(), rx, ry, a, large, sweep, x, y);
		if (arc != null) p.append(arc, true);
		p.lineTo(x, y);
	}
	
	private static Shape createSvgArc(
		double x0, double y0, double rx, double ry, double a,
		boolean large, boolean sweep, double x, double y
	) {
		if (x0 == x && y0 == y) return null;
		if (rx == 0 || ry == 0) return null;
		double dx2 = (x0 - x) / 2;
		double dy2 = (y0 - y) / 2;
		a = Math.toRadians(a % 360);
		double ca = Math.cos(a);
		double sa = Math.sin(a);
		double x1 = sa * dy2 + ca * dx2;
		double y1 = ca * dy2 - sa * dx2;
		rx = Math.abs(rx);
		ry = Math.abs(ry);
		double Prx = rx * rx;
		double Pry = ry * ry;
		double Px1 = x1 * x1;
		double Py1 = y1 * y1;
		double rc = Px1/Prx + Py1/Pry;
		if (rc > 1) {
			rx = Math.sqrt(rc) * rx;
			ry = Math.sqrt(rc) * ry;
			Prx = rx * rx;
			Pry = ry * ry;
		}
		double s = (large == sweep) ? -1 : 1;
		double sq = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1)) / ((Prx*Py1)+(Pry*Px1));
		if (sq < 0) sq = 0;
		double m = s * Math.sqrt(sq);
		double cx1 = m *  ((rx * y1) / ry);
		double cy1 = m * -((ry * x1) / rx);
		double sx2 = (x0 + x) / 2;
		double sy2 = (y0 + y) / 2;
		double cx = sx2 + ca * cx1 - sa * cy1;
		double cy = sy2 + sa * cx1 + ca * cy1;
		double ux = (x1 - cx1) / rx;
		double uy = (y1 - cy1) / ry;
		double vx = (-x1 -cx1) / rx;
		double vy = (-y1 -cy1) / ry;
		double sn = Math.sqrt(ux*ux + uy*uy);
		double sp = ux;
		double ss = (uy < 0) ? -1 : 1;
		double as = Math.toDegrees(ss * Math.acos(sp / sn));
		double en = Math.sqrt((ux*ux + uy*uy) * (vx*vx + vy*vy));
		double ep = ux * vx + uy * vy;
		double es = (ux * vy - uy * vx < 0) ? -1 : 1;
		double ae = Math.toDegrees(es * Math.acos(ep / en));
		if (!sweep && ae > 0) ae -= 360;
		if (sweep && ae < 0) ae += 360;
		ae %= 360;
		as %= 360;
		Arc2D.Double arc = new Arc2D.Double();
		arc.x = cx - rx;
		arc.y = cy - ry;
		arc.width = rx * 2;
		arc.height = ry * 2;
		arc.start = -as;
		arc.extent = -ae;
		double acx = arc.getCenterX();
		double acy = arc.getCenterY();
		AffineTransform t = AffineTransform.getRotateInstance(a, acx, acy);
		return t.createTransformedShape(arc);
	}
	
	private ShapeUtilities() {}
}
