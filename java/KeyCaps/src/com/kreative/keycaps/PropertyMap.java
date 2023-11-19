package com.kreative.keycaps;

import static com.kreative.keycaps.ColorUtilities.getPaletteColor;
import static com.kreative.keycaps.ColorUtilities.getPaletteOpacity;
import static com.kreative.keycaps.ColorUtilities.parseColorIndex;
import static com.kreative.keycaps.StringUtilities.quote;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

public class PropertyMap extends HashMap<String,Object> {
	private static final long serialVersionUID = 1L;
	
	private String parseKey(KeyCapParser p) {
		if (p.hasNextID()) return p.next();
		if (p.hasNextQuote('\'')) return p.nextQuote('\'');
		if (p.hasNextQuote('\"')) return p.nextQuote('\"');
		if (p.hasNextQuote('`')) return p.nextQuote('`');
		throw p.expected("property key");
	}
	
	private String parseValue(KeyCapParser p) {
		if (p.hasNextID()) return p.next();
		if (p.hasNextQuote('\'')) return p.nextQuote('\'');
		if (p.hasNextQuote('\"')) return p.nextQuote('\"');
		if (p.hasNextQuote('`')) return p.nextQuote('`');
		if (p.hasNextCoded()) return p.nextCoded();
		if (p.hasNextFloat()) return p.next();
		throw p.expected("property value");
	}
	
	public void parse(KeyCapParser p) {
		if (p.hasNextChar('{')) {
			p.next();
			while (true) {
				if (p.hasNextChar('}')) { p.next(); return; }
				String key = parseKey(p);
				if (p.hasNextChar(':')) {
					p.next();
					put(key, parseValue(p));
				} else {
					put(key, null);
				}
				if (p.hasNextChar(',')) { p.next(); continue; }
				if (p.hasNextChar('}')) { p.next(); return; }
				throw p.expected(", or }");
			}
		}
	}
	
	public void parse(String s) {
		KeyCapParser p = new KeyCapParser(s);
		parse(p);
		p.expectEnd();
	}
	
	public boolean containsAny(String... keys) {
		for (String key : keys) {
			if (containsKey(key)) {
				return true;
			}
		}
		return false;
	}
	
	public Object getAny(String... keys) {
		for (String key : keys) {
			if (containsKey(key)) {
				return get(key);
			}
		}
		return null;
	}
	
	public String getString(String... keys) {
		Object value = getAny(keys);
		if (value == null) return null;
		return value.toString();
	}
	
	public Integer getInteger(String... keys) {
		Object value = getAny(keys);
		if (value == null) return null;
		if (value instanceof Integer) return (Integer)value;
		if (value instanceof Number) return ((Number)value).intValue();
		try {
			String s = value.toString().trim().toLowerCase();
			if (s.startsWith("0x")) return (int)Long.parseLong(s.substring(2), 16);
			if (s.startsWith("0o")) return (int)Long.parseLong(s.substring(2), 8);
			if (s.startsWith("$")) return (int)Long.parseLong(s.substring(1), 16);
			if (s.startsWith("0")) return (int)Long.parseLong(s.substring(1), 8);
			return (int)Long.parseLong(s, 10);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public Long getLong(String... keys) {
		Object value = getAny(keys);
		if (value == null) return null;
		if (value instanceof Long) return (Long)value;
		if (value instanceof Number) return ((Number)value).longValue();
		try {
			String s = value.toString().trim().toLowerCase();
			if (s.startsWith("0x")) return Long.parseLong(s.substring(2), 16);
			if (s.startsWith("0o")) return Long.parseLong(s.substring(2), 8);
			if (s.startsWith("$")) return Long.parseLong(s.substring(1), 16);
			if (s.startsWith("0")) return Long.parseLong(s.substring(1), 8);
			return Long.parseLong(s, 10);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public Float getFloat(String... keys) {
		Object value = getAny(keys);
		if (value == null) return null;
		if (value instanceof Float) return (Float)value;
		if (value instanceof Number) return ((Number)value).floatValue();
		try { return Float.parseFloat(value.toString().trim()); }
		catch (NumberFormatException e) { return null; }
	}
	
	public Double getDouble(String... keys) {
		Object value = getAny(keys);
		if (value == null) return null;
		if (value instanceof Double) return (Double)value;
		if (value instanceof Number) return ((Number)value).doubleValue();
		try { return Double.parseDouble(value.toString().trim()); }
		catch (NumberFormatException e) { return null; }
	}
	
	public Anchor getAnchor(String... keys) {
		Object value = getAny(keys);
		if (value == null) return null;
		if (value instanceof Anchor) return (Anchor)value;
		return Anchor.parseAnchor(value.toString());
	}
	
	public Color getColor(String... keys) {
		Object value = getAny(keys);
		if (value == null) return null;
		if (value instanceof Color) return (Color)value;
		if (value instanceof Number) return getPaletteColor(((Number)value).intValue());
		return getPaletteColor(parseColorIndex(value.toString()));
	}
	
	public Float getOpacity(String... keys) {
		Object value = getAny(keys);
		if (value == null) return null;
		if (value instanceof Color) return ((Color)value).getAlpha() / 255f;
		if (value instanceof Float) return (Float)value;
		if (value instanceof Double) return ((Double)value).floatValue();
		if (value instanceof BigDecimal) return ((BigDecimal)value).floatValue();
		if (value instanceof Number) return getPaletteOpacity(((Number)value).intValue());
		return getPaletteOpacity(parseColorIndex(value.toString()));
	}
	
	public String toString() {
		if (isEmpty()) return "";
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		boolean first = true;
		String[] keys = keySet().toArray(new String[size()]);
		Arrays.sort(keys);
		for (String key : keys) {
			if (first) first = false;
			else sb.append(",");
			sb.append(formatKey(key));
			Object value = get(key);
			if (value == null) continue;
			sb.append(":");
			sb.append(formatValue(value));
		}
		sb.append("}");
		return sb.toString();
	}
	
	private static boolean isID(String s) {
		int i = 0, n = s.length();
		while (i < n) {
			int ch = s.codePointAt(i);
			if (!Character.isLetter(ch)) return false;
			i += Character.charCount(ch);
		}
		return true;
	}
	
	private static String formatKey(String s) {
		if (isID(s)) return s;
		if (!s.contains("\'")) return quote(s, '\'');
		if (!s.contains("\"")) return quote(s, '\"');
		if (!s.contains("`")) return quote(s, '`');
		return quote(s, '\'');
	}
	
	private static boolean isNumber(Object o) {
		try {
			double d = (
				(o instanceof Number) ?
				((Number)o).doubleValue() :
				Double.parseDouble(o.toString())
			);
			return !(Double.isNaN(d) || Double.isInfinite(d));
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private static String formatValue(Object o) {
		String s = o.toString();
		if (isNumber(o)) return s;
		if (!s.contains("\'")) return quote(s, '\'');
		if (!s.contains("\"")) return quote(s, '\"');
		if (!s.contains("`")) return quote(s, '`');
		return quote(s, '\'');
	}
}
