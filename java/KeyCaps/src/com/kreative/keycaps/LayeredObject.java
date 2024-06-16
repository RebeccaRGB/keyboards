package com.kreative.keycaps;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public abstract class LayeredObject {
	public abstract String toSVG(String prefix, String indent, float rounding);
	public abstract void paint(Graphics2D g, AffineTransform tx);
}
