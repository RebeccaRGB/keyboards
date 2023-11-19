package com.kreative.keycaps;

import static com.kreative.keycaps.StringUtilities.quote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyCapLegend {
	public static enum Type {
		NONE(), // No legend
		F("function"), // Function or modifier key
		G("function", "altfunction"), // Dual-function or marked modifier key
		L("letter"), // Letter key
		A("letter", "altletter"), // Letter key with letter alt
		T("letter", "altunshifted", "altshifted"), // Letter key with shifted/symbol alt
		S("unshifted", "shifted"), // Shifted/symbol key
		Z("unshifted", "shifted", "altletter"), // Shifted/symbol key with letter alt
		Q("unshifted", "shifted", "altunshifted", "altshifted"), // Shifted/symbol key with shifted/symbol alt
		N("numpad", "numpadfunction"); // Numpad key with non-numlock function
		private final String[] paramKeys;
		private Type(String... paramKeys) {
			this.paramKeys = paramKeys;
		}
		public static Type forName(String name) {
			for (Type t : values()) {
				if (t.toString().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return null;
		}
		public static Type forItems(List<KeyCapLegendItem> i) {
			switch (i.size()) {
				case 4: return Q;
				case 3: return T;
				case 2: return (i.get(0) != null && i.get(0).isImpliedLetterOrSymbol() &&
				                i.get(1) != null && i.get(1).isImpliedLetterOrSymbol()) ? S : G;
				case 1: return (i.get(0) != null && i.get(0).isImpliedLetterOrSymbol()) ? L : F;
				default: return NONE;
			}
		}
		public static Type forKeys(Collection<String> keys) {
			for (Type t : values()) {
				if (Arrays.asList(t.paramKeys).containsAll(keys)) {
					return t;
				}
			}
			return null;
		}
	}
	
	private static String parseKey(KeyCapParser p) {
		if (p.hasNextID()) return p.next();
		if (p.hasNextQuote('\'')) return p.nextQuote('\'');
		if (p.hasNextQuote('\"')) return p.nextQuote('\"');
		if (p.hasNextQuote('`')) return p.nextQuote('`');
		return null;
	}
	
	public static KeyCapLegend parse(KeyCapParser p) {
		PropertyMap props = new PropertyMap();
		Type type = null;
		List<KeyCapLegendItem> items = new ArrayList<KeyCapLegendItem>();
		Map<String,KeyCapLegendItem> kwitems = new HashMap<String,KeyCapLegendItem>();
		if (p.hasNextChar('[')) {
			p.next();
			props.parse(p);
			int tm = p.mark();
			if (p.hasNextID()) {
				String t = p.next();
				if (p.hasNextChar(',')) {
					p.next();
					type = Type.forName(t);
				} else {
					p.reset(tm);
				}
			}
			while (true) {
				if (p.hasNextChar(']')) { p.next(); break; }
				int km = p.mark();
				String key = parseKey(p);
				if (key != null) {
					if (p.hasNextChar(':')) {
						p.next();
					} else {
						key = null;
						p.reset(km);
					}
				}
				KeyCapLegendItem item = KeyCapLegendItem.parse(p);
				((item != null) ? item.getPropertyMap() : new PropertyMap()).parse(p);
				if (key != null) kwitems.put(key, item);
				else items.add(item);
				if (p.hasNextChar(',')) { p.next(); continue; }
				if (p.hasNextChar(']')) { p.next(); break; }
				throw p.expected(", or ]");
			}
		} else {
			if (p.hasNextID()) {
				type = Type.forName(p.next());
			}
			while (true) {
				try { items.add(KeyCapLegendItem.parse(p)); }
				catch (IllegalArgumentException e) { break; }
			}
		}
		KeyCapLegend legend = new KeyCapLegend();
		legend.props.putAll(props);
		if (type == null) type = Type.forItems(items);
		for (int i = 0, n = Math.min(items.size(), type.paramKeys.length); i < n; i++) {
			if (items.get(i) != null) legend.items.put(type.paramKeys[i], items.get(i));
		}
		legend.items.putAll(kwitems);
		return legend;
	}
	
	public static KeyCapLegend parse(String s) {
		KeyCapParser p = new KeyCapParser(s);
		KeyCapLegend legend = parse(p);
		p.expectEnd();
		return legend;
	}
	
	private final PropertyMap props = new PropertyMap();
	private final Map<String,KeyCapLegendItem> items = new HashMap<String,KeyCapLegendItem>();
	
	public PropertyMap getPropertyMap() { return this.props; }
	public Map<String,KeyCapLegendItem> getLegendItems() { return this.items; }
	
	public Type getType() {
		return Type.forKeys(items.keySet());
	}
	
	public KeyCapLegendItem getItem(int i) {
		Type type = getType();
		if (type == null) return null;
		return items.get(type.paramKeys[i]);
	}
	
	public String toString() {
		Type type = getType();
		if (type == null) return toNormalizedString();
		if (hasProperties()) return toNormalizedString();
		int n = minParamCount(type);
		if (n == 0) return "";
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		if (canImplyType(type, n)) {
			for (int i = 0; i < n; i++) {
				if (i > 0) sb.append(',');
				sb.append(formatParameter(type, i));
			}
		} else {
			sb.append(type);
			for (int i = 0; i < n; i++) {
				sb.append(',');
				sb.append(formatParameter(type, i));
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	public String toNormalizedString() {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		sb.append(props.toString());
		boolean first = true;
		String[] keys = items.keySet().toArray(new String[items.size()]);
		Arrays.sort(keys);
		for (String key : keys) {
			if (first) first = false;
			else sb.append(",");
			sb.append(formatKey(key));
			sb.append(":");
			KeyCapLegendItem value = items.get(key);
			if (value == null) {
				sb.append("$");
			} else {
				sb.append(value.toString());
				sb.append(value.getPropertyMap().toString());
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	public String toMinimizedString() {
		Type type = getType();
		if (type == null) return toNormalizedString();
		if (hasProperties()) return toNormalizedString();
		int n = minParamCount(type);
		if (n == 0) return "";
		StringBuffer sb = new StringBuffer();
		if (canImplyType(type, n)) {
			for (int i = 0; i < n; i++) {
				sb.append(formatParameter(type, i));
			}
		} else {
			sb.append('[');
			sb.append(type);
			for (int i = 0; i < n; i++) {
				sb.append(',');
				sb.append(formatParameter(type, i));
			}
			sb.append(']');
		}
		return sb.toString();
	}
	
	public int hashCode() {
		return toNormalizedString().hashCode();
	}
	
	public boolean equals(Object o) {
		return (this == o) || (
			(o instanceof KeyCapLegend) &&
			this.toNormalizedString().equals(((KeyCapLegend)o).toNormalizedString())
		);
	}
	
	private boolean hasProperties() {
		if (!props.isEmpty()) return true;
		for (KeyCapLegendItem item : items.values()) {
			if (item != null && !item.getPropertyMap().isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	private int minParamCount(Type type) {
		int n = type.paramKeys.length - 1;
		while (n >= 0 && items.get(type.paramKeys[n]) == null) n--;
		return n + 1;
	}
	
	private boolean canImplyType(Type type, int n) {
		return (
			(type == Type.L && n == 1 && isImpliedLetterOrSymbol(type, 0)) ||
			(type == Type.F && n == 1 && !isImpliedLetterOrSymbol(type, 0)) ||
			(type == Type.S && n == 2 && (isImpliedLetterOrSymbol(type, 0) && isImpliedLetterOrSymbol(type, 1))) ||
			(type == Type.G && n == 2 && !(isImpliedLetterOrSymbol(type, 0) && isImpliedLetterOrSymbol(type, 1))) ||
			(type == Type.T && n == 3) ||
			(type == Type.Q && n == 4)
		);
	}
	
	private boolean isImpliedLetterOrSymbol(Type type, int i) {
		KeyCapLegendItem item = items.get(type.paramKeys[i]);
		if (item == null) return false;
		return item.isImpliedLetterOrSymbol();
	}
	
	private String formatParameter(Type type, int i) {
		KeyCapLegendItem item = items.get(type.paramKeys[i]);
		if (item == null) return "$";
		return item.toString();
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
}
