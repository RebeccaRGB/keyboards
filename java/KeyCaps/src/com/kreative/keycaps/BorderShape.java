package com.kreative.keycaps;

import static com.kreative.keycaps.ShapeUtilities.fromSVGPath;
import static com.kreative.keycaps.ShapeUtilities.translate;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

public class BorderShape {
	private final int w, h;
	private final Shape tl, t, tr;
	private final Shape itl, itr;
	private final Shape l, c, r;
	private final Shape ibl, ibr;
	private final Shape bl, b, br;
	
	public BorderShape(
		int width, int height,
		Shape tl, Shape t, Shape tr,
		Shape itl, Shape itr,
		Shape l, Shape c, Shape r,
		Shape ibl, Shape ibr,
		Shape bl, Shape b, Shape br
	) {
		this.w = width; this.h = height;
		this.tl = tl; this.t = t; this.tr = tr;
		this.itl = itl; this.itr = itr;
		this.l = l; this.c = c; this.r = r;
		this.ibl = ibl; this.ibr = ibr;
		this.bl = bl; this.b = b; this.br = br;
	}
	
	public BorderShape(
		int width, int height,
		String tl, String t, String tr,
		String itl, String itr,
		String l, String c, String r,
		String ibl, String ibr,
		String bl, String b, String br
	) {
		this.w = width; this.h = height;
		this.tl = (tl == null || (tl = tl.trim()).length() == 0) ? null : fromSVGPath(tl);
		this.t = (t == null || (t = t.trim()).length() == 0) ? null : fromSVGPath(t);
		this.tr = (tr == null || (tr = tr.trim()).length() == 0) ? null : fromSVGPath(tr);
		this.itl = (itl == null || (itl = itl.trim()).length() == 0) ? null : fromSVGPath(itl);
		this.itr = (itr == null || (itr = itr.trim()).length() == 0) ? null : fromSVGPath(itr);
		this.l = (l == null || (l = l.trim()).length() == 0) ? null : fromSVGPath(l);
		this.c = (c == null || (c = c.trim()).length() == 0) ? null : fromSVGPath(c);
		this.r = (r == null || (r = r.trim()).length() == 0) ? null : fromSVGPath(r);
		this.ibl = (ibl == null || (ibl = ibl.trim()).length() == 0) ? null : fromSVGPath(ibl);
		this.ibr = (ibr == null || (ibr = ibr.trim()).length() == 0) ? null : fromSVGPath(ibr);
		this.bl = (bl == null || (bl = bl.trim()).length() == 0) ? null : fromSVGPath(bl);
		this.b = (b == null || (b = b.trim()).length() == 0) ? null : fromSVGPath(b);
		this.br = (br == null || (br = br.trim()).length() == 0) ? null : fromSVGPath(br);
	}
	
	public Shape create(Shape shape) {
		if (shape == null) return null;
		Area area = new Area();
		addShape(area, shape);
		return area.isEmpty() ? null : area;
	}
	
	private void addShape(Area area, Shape shape) {
		addContent(area, shape);
		float sx = 0, sy = 0, cx = 0, cy = 0;
		float[] c = new float[6];
		for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
			switch (i.currentSegment(c)) {
				case PathIterator.SEG_MOVETO:
					sx = cx = c[0];
					sy = cy = c[1];
					break;
				case PathIterator.SEG_LINETO:
					addLine(area, shape, cx, cy, c[0], c[1]);
					addPoint(area, shape, cx = c[0], cy = c[1]);
					break;
				case PathIterator.SEG_QUADTO:
					addLine(area, shape, cx, cy, c[2], c[3]);
					addPoint(area, shape, cx = c[2], cy = c[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					addLine(area, shape, cx, cy, c[4], c[5]);
					addPoint(area, shape, cx = c[4], cy = c[5]);
					break;
				case PathIterator.SEG_CLOSE:
					if (cx == sx && cy == sy) break;
					addLine(area, shape, cx, cy, sx, sy);
					addPoint(area, shape, cx = sx, cy = sy);
					break;
			}
		}
	}
	
	private void addLine(Area area, Shape shape, float x0, float y0, float x1, float y1) {
		if (y0 == y1) {
			if (shape.contains((x0 + x1) / 2, y0 + 1) && t != null) {
				float x2 = Math.min(x0, x1);
				float x3 = Math.max(x0, x1);
				if (!shape.contains(x2 - 1, y0 + 1)) x2 += w;
				if (!shape.contains(x3 + 1, y0 + 1)) x3 -= w;
				AffineTransform tx = AffineTransform.getTranslateInstance(x2, y0);
				tx.scale((x3 - x2) / w, 1);
				area.add(new Area(tx.createTransformedShape(t)));
			}
			if (shape.contains((x0 + x1) / 2, y0 - 1) && b != null) {
				float x2 = Math.min(x0, x1);
				float x3 = Math.max(x0, x1);
				if (!shape.contains(x2 - 1, y0 - 1)) x2 += w;
				if (!shape.contains(x3 + 1, y0 - 1)) x3 -= w;
				AffineTransform tx = AffineTransform.getTranslateInstance(x2, y0 - h);
				tx.scale((x3 - x2) / w, 1);
				area.add(new Area(tx.createTransformedShape(b)));
			}
		}
		if (x0 == x1) {
			if (shape.contains(x0 + 1, (y0 + y1) / 2) && l != null) {
				float y2 = Math.min(y0, y1);
				float y3 = Math.max(y0, y1);
				if (!shape.contains(x0 + 1, y2 - 1)) y2 += h;
				if (!shape.contains(x0 + 1, y3 + 1)) y3 -= h;
				AffineTransform tx = AffineTransform.getTranslateInstance(x0, y2);
				tx.scale(1, (y3 - y2) / h);
				area.add(new Area(tx.createTransformedShape(l)));
			}
			if (shape.contains(x0 - 1, (y0 + y1) / 2) && r != null) {
				float y2 = Math.min(y0, y1);
				float y3 = Math.max(y0, y1);
				if (!shape.contains(x0 - 1, y2 - 1)) y2 += h;
				if (!shape.contains(x0 - 1, y3 + 1)) y3 -= h;
				AffineTransform tx = AffineTransform.getTranslateInstance(x0 - w, y2);
				tx.scale(1, (y3 - y2) / h);
				area.add(new Area(tx.createTransformedShape(r)));
			}
		}
	}
	
	private void addPoint(Area area, Shape shape, float x, float y) {
		boolean istl = shape.contains(x + 1, y + 1);
		boolean istr = shape.contains(x - 1, y + 1);
		boolean isbl = shape.contains(x + 1, y - 1);
		boolean isbr = shape.contains(x - 1, y - 1);
		if (!istr && !isbl) {
			if (istl && tl != null) area.add(new Area(translate(tl, x, y)));
			if (isbr && br != null) area.add(new Area(translate(br, x - w, y - h)));
		} else if (!istl && !isbr) {
			if (istr && tr != null) area.add(new Area(translate(tr, x - w, y)));
			if (isbl && bl != null) area.add(new Area(translate(bl, x, y - h)));
		} else {
			if ( istl &&  istr && !isbl && !isbr && t   != null) area.add(new Area(translate(t, x-w/2f, y)));
			if (!istl &&  istr &&  isbl &&  isbr && itl != null) area.add(new Area(translate(itl, x-w, y-h)));
			if ( istl && !istr &&  isbl &&  isbr && itr != null) area.add(new Area(translate(itr, x, y-h)));
			if ( istl && !istr &&  isbl && !isbr && l   != null) area.add(new Area(translate(l, x, y-h/2f)));
			if ( istl &&  istr &&  isbl &&  isbr && c   != null) area.add(new Area(translate(c, x-w/2f, y-h/2f)));
			if (!istl &&  istr && !isbl &&  isbr && r   != null) area.add(new Area(translate(r, x-w, y-h/2f)));
			if ( istl &&  istr && !isbl &&  isbr && ibl != null) area.add(new Area(translate(ibl, x-w, y)));
			if ( istl &&  istr &&  isbl && !isbr && ibr != null) area.add(new Area(translate(ibr, x, y)));
			if (!istl && !istr &&  isbl &&  isbr && b   != null) area.add(new Area(translate(b, x-w/2f, y-h)));
		}
	}
	
	private void addContent(Area area, Shape shape) {
		if (c == null) return;
		BasicStroke stroke = new BasicStroke(Math.min(w, h) * 2);
		Area strokedArea = new Area(stroke.createStrokedShape(shape));
		Area filledArea = new Area(shape); filledArea.subtract(strokedArea);
		area.add(filledArea);
	}
}
