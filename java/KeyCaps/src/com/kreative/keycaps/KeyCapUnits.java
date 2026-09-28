package com.kreative.keycaps;

public final class KeyCapUnits {
	public static final float ROUNDING = 1000;
	
	public static final float U = 1;
	public static final float V = 4;
	public static final float W = 20;
	public static final float IN = 0.75f;
	public static final float CM = 1.905f;
	public static final float MM = 19.05f;
	public static final float PC = 4.5f;
	public static final float PT = 54;
	public static final float PX = 72;
	public static final float Q = 76.2f;
	
	public static float parseUnit(String s, float def) {
		if (s == null || (s = s.trim()).length() == 0) return def;
		if (s.startsWith("/")) {
			try { return Float.parseFloat(s.substring(1).trim()); }
			catch (NumberFormatException e) { return def; }
		}
		if (s.equalsIgnoreCase("u")) return U;
		if (s.equalsIgnoreCase("v")) return V;
		if (s.equalsIgnoreCase("w")) return W;
		if (s.equalsIgnoreCase("in")) return IN;
		if (s.equalsIgnoreCase("cm")) return CM;
		if (s.equalsIgnoreCase("mm")) return MM;
		if (s.equalsIgnoreCase("pc")) return PC;
		if (s.equalsIgnoreCase("pt")) return PT;
		if (s.equalsIgnoreCase("px")) return PX;
		if (s.equalsIgnoreCase("Q")) return Q;
		return def;
	}
	
	public static float minimalUnit(float keyCapSize, float... values) {
		if (oopsAllInt(U, keyCapSize, values)) return U;
		if (oopsAllInt(V, keyCapSize, values)) return V;
		if (oopsAllInt(W, keyCapSize, values)) return W;
		if (oopsAllInt(IN, keyCapSize, values)) return IN;
		if (oopsAllInt(CM, keyCapSize, values)) return CM;
		if (oopsAllInt(MM, keyCapSize, values)) return MM;
		if (oopsAllInt(PC, keyCapSize, values)) return PC;
		if (oopsAllInt(PT, keyCapSize, values)) return PT;
		if (oopsAllInt(PX, keyCapSize, values)) return PX;
		if (oopsAllInt(Q, keyCapSize, values)) return Q;
		return keyCapSize;
	}
	
	private static boolean oopsAllInt(float n, float d, float... values) {
		for (float value : values) {
			float v = value * n / d;
			if (v != (int)v) return false;
		}
		return true;
	}
	
	public static String unitToString(float keyCapSize) {
		if (keyCapSize == U) return "u";
		if (keyCapSize == V) return "v";
		if (keyCapSize == W) return "w";
		if (keyCapSize == IN) return "in";
		if (keyCapSize == CM) return "cm";
		if (keyCapSize == MM) return "mm";
		if (keyCapSize == PC) return "pc";
		if (keyCapSize == PT) return "pt";
		if (keyCapSize == PX) return "px";
		if (keyCapSize == Q) return "Q";
		return "/" + valueToString(keyCapSize);
	}
	
	public static String valueToString(float value) {
		value = Math.round(value * ROUNDING) / ROUNDING;
		if (value == (int)value) return Integer.toString((int)value);
		return Float.toString(value);
	}
	
	public static String valuesToString(String delimiter, float... values) {
		if (values == null || values.length == 0) return "";
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (float v : values) {
			if (first) {
				sb.append(valueToString(v));
				first = false;
			} else {
				if (delimiter != null) sb.append(delimiter);
				else if (v >= 0) sb.append("+");
				sb.append(valueToString(v));
			}
		}
		return sb.toString();
	}
	
	private KeyCapUnits() {}
}
