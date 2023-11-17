package com.kreative.keycaps;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtilities {
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
	
	public static String colorToString(Color color, String def) {
		if (color == null) return def;
		return "#" + Integer.toHexString(0xFF000000 | color.getRGB()).substring(2);
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
		// 24-bit
		a = (i >> 18) & 63;
		if (a > 0) {
			float r = ((i >> 12) & 63) / 63f;
			float g = ((i >>  6) & 63) / 63f;
			float b = ((i >>  0) & 63) / 63f;
			return new Color(r, g, b);
		}
		// 16-bit
		a = (i >> 12) & 15;
		if (a > 0) {
			int r = ((i >> 8) & 15) * 17;
			int g = ((i >> 4) & 15) * 17;
			int b = ((i >> 0) & 15) * 17;
			return new Color(r, g, b);
		}
		// 12-bit
		a = (i >> 9) & 7;
		if (a > 0) {
			float r = ((i >> 6) & 7) / 7f;
			float g = ((i >> 3) & 7) / 7f;
			float b = ((i >> 0) & 7) / 7f;
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
		// 24-bit
		a = (i >> 18) & 63;
		if (a == 63) return 1f;
		if (a > 0) return a / 63f;
		// 16-bit
		a = (i >> 12) & 15;
		if (a == 15) return 1f;
		if (a > 0) return a / 15f;
		// 12-bit
		a = (i >> 9) & 7;
		if (a == 7) return 1f;
		if (a > 0) return a / 7f;
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
	
	private static final Pattern HEX_PATTERN = Pattern.compile("^#([0-9A-Fa-f]+)$");
	
	public static int parseColorIndex(String s) {
		if (s == null || (s = s.trim()).length() == 0) return 0;
		try {
			Matcher m = HEX_PATTERN.matcher(s);
			if (m.matches()) {
				int v = (int)Long.parseLong(m.group(1), 16);
				switch (m.group(1).length()) {
					case 8: return ((v >> 24) == 0) ? 32 : v;
					case 6: return 0xFF000000 | v;
					case 4: return ((v >> 12) == 0) ? 32 : v;
					case 3: return 0xF000 | v;
					default: return 0;
				}
			}
			if (s.startsWith("0X")) return (int)Long.parseLong(s.substring(2), 16);
			if (s.startsWith("0x")) return (int)Long.parseLong(s.substring(2), 16);
			if (s.startsWith("0O")) return (int)Long.parseLong(s.substring(2), 8);
			if (s.startsWith("0o")) return (int)Long.parseLong(s.substring(2), 8);
			if (s.startsWith("$")) return (int)Long.parseLong(s.substring(1), 16);
			if (s.startsWith("0")) return (int)Long.parseLong(s.substring(1), 8);
			return (int)Long.parseLong(s, 10);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	private ColorUtilities() {}
}
