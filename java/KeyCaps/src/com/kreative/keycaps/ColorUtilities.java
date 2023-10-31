package com.kreative.keycaps;

import java.awt.Color;

public class ColorUtilities {
	private static int clamp(int v) {
		return (v <= 0) ? 0 : (v >= 255) ? 255 : v;
	}
	
	public static Color multiplyAdd(
		Color color,
		float rm, float gm, float bm,
		float rb, float gb, float bb
	) {
		int r = clamp(Math.round(color.getRed() * rm + rb));
		int g = clamp(Math.round(color.getGreen() * gm + gb));
		int b = clamp(Math.round(color.getBlue() * bm + bb));
		return new Color(r, g, b);
	}
	
	public static Color contrastingColor(Color color) {
		int k = color.getRed() * 30 + color.getGreen() * 59 + color.getBlue() * 11;
		return (k < 12750) ? Color.white : Color.black;
	}
	
	private static final Color[] PALETTE_COLORS = {
		null,
		new Color(0x1a1a1a), // black
		new Color(0x333333), new Color(0x666666), new Color(0x999999), new Color(0xcccccc), // gray
		new Color(0x444433), new Color(0x777766), new Color(0xaaaa99), new Color(0xddddcc), // beige
		new Color(0x663333), new Color(0xcc0000), new Color(0xffbbcc), // red
		new Color(0x775533), new Color(0xff6600), new Color(0xffcc99), // orange
		new Color(0x666633), new Color(0xffcc00), new Color(0xffee99), // yellow
		new Color(0x336600), new Color(0x00aa00), new Color(0x77eeaa), // green
		new Color(0x003366), new Color(0x0033cc), new Color(0x66bbff), // blue
		new Color(0x330066), new Color(0x9900ff), new Color(0xbb99ff), // purple
		new Color(0x660033), new Color(0xee00cc), new Color(0xff99ff), // magenta
		new Color(0xf2f2f2) // white
	};
	
	public static Color getPaletteColor(int i) {
		// 32-bit
		int a = (i >> 24) & 255;
		if (a > 0) return new Color(i);
		// 16-bit
		a = (i >> 12) & 15;
		if (a > 0) {
			int r = ((i >> 8) & 15) * 17;
			int g = ((i >> 4) & 15) * 17;
			int b = ((i >> 0) & 15) * 17;
			return new Color(r, g, b);
		}
		// 8-bit
		a = (i >> 6) & 3;
		if (a > 0) {
			int r = ((i >> 4) & 3) * 85;
			int g = ((i >> 2) & 3) * 85;
			int b = ((i >> 0) & 3) * 85;
			return new Color(r, g, b);
		}
		// 6-bit
		return PALETTE_COLORS[i & 31];
	}
	
	public static Float getPaletteOpacity(int i) {
		// 32-bit
		int a = (i >> 24) & 255;
		if (a == 255) return 1f;
		if (a > 0) return a / 255f;
		// 16-bit
		a = (i >> 12) & 15;
		if (a == 15) return 1f;
		if (a > 0) return a / 15f;
		// 8-bit
		a = (i >> 6) & 3;
		if (a == 3) return 1f;
		if (a > 0) return a / 3f;
		// 6-bit
		if ((i & 32) == 0) {
			return ((i & 31) == 0) ? null : 1f;
		} else {
			return ((i & 31) == 0) ? 0f : 0.5f;
		}
	}
}
