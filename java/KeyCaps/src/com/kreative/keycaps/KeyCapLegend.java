package com.kreative.keycaps;

import static com.kreative.keycaps.StringUtilities.quote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeyCapLegend implements Map<String,KeyCapLegendItem> {
	public static final String KEY_FUNCTION = "F";
	public static final String KEY_ALT_FUNCTION = "AF";
	public static final String KEY_LEFT_FUNCTION = "LF";
	public static final String KEY_RIGHT_FUNCTION = "RF";
	public static final String KEY_LETTER = "L";
	public static final String KEY_UNSHIFTED = "U";
	public static final String KEY_SHIFTED = "S";
	public static final String KEY_ALT_LETTER = "AL";
	public static final String KEY_ALT_UNSHIFTED = "AU";
	public static final String KEY_ALT_SHIFTED = "AS";
	public static final String KEY_NUMPAD = "N";
	public static final String KEY_NUMPAD_FUNCTION = "NF";
	
	public static final String KEY_FRONT_FUNCTION = "FF";
	public static final String KEY_FRONT_ALT_FUNCTION = "FAF";
	public static final String KEY_FRONT_LEFT_FUNCTION = "FLF";
	public static final String KEY_FRONT_RIGHT_FUNCTION = "FRF";
	public static final String KEY_FRONT_LETTER = "FL";
	public static final String KEY_FRONT_UNSHIFTED = "FU";
	public static final String KEY_FRONT_SHIFTED = "FS";
	public static final String KEY_FRONT_ALT_LETTER = "FAL";
	public static final String KEY_FRONT_ALT_UNSHIFTED = "FAU";
	public static final String KEY_FRONT_ALT_SHIFTED = "FAS";
	public static final String KEY_FRONT_NUMPAD = "FN";
	public static final String KEY_FRONT_NUMPAD_FUNCTION = "FNF";
	
	public static enum Type {
		NONE(),
		F(KEY_FUNCTION),
		G(KEY_FUNCTION, KEY_ALT_FUNCTION),
		H(KEY_LEFT_FUNCTION, KEY_RIGHT_FUNCTION),
		L(KEY_LETTER),
		A(KEY_LETTER, KEY_ALT_LETTER),
		T(KEY_LETTER, KEY_ALT_UNSHIFTED, KEY_ALT_SHIFTED),
		S(KEY_UNSHIFTED, KEY_SHIFTED),
		Z(KEY_UNSHIFTED, KEY_SHIFTED, KEY_ALT_LETTER),
		Q(KEY_UNSHIFTED, KEY_SHIFTED, KEY_ALT_UNSHIFTED, KEY_ALT_SHIFTED),
		N(KEY_NUMPAD, KEY_NUMPAD_FUNCTION);
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
		legend.addAll(type, items);
		legend.putAll(kwitems);
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
	
	public void addAll(Type type, List<KeyCapLegendItem> items) {
		if (type == null) type = Type.forItems(items);
		for (int i = 0, n = Math.min(items.size(), type.paramKeys.length); i < n; i++) {
			if (items.get(i) != null) this.items.put(type.paramKeys[i], items.get(i));
		}
	}
	
	public boolean containsAny(String... keys) {
		for (String key : keys) {
			if (items.containsKey(key)) {
				return true;
			}
		}
		return false;
	}
	
	public void clear() { items.clear(); }
	public boolean containsKey(Object key) { return items.containsKey(key); }
	public boolean containsValue(Object item) { return items.containsValue(item); }
	public Set<Map.Entry<String,KeyCapLegendItem>> entrySet() { return items.entrySet(); }
	public KeyCapLegendItem get(Object key) { return items.get(key); }
	public boolean isEmpty() { return items.isEmpty(); }
	public Set<String> keySet() { return items.keySet(); }
	public KeyCapLegendItem put(String key, KeyCapLegendItem item) { return items.put(key, item); }
	public void putAll(Map<? extends String, ? extends KeyCapLegendItem> map) { items.putAll(map); }
	public KeyCapLegendItem remove(Object key) { return items.remove(key); }
	public int size() { return items.size(); }
	public Collection<KeyCapLegendItem> values() { return items.values(); }
	
	public String toString() {
		Type type = Type.forKeys(items.keySet());
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
		Type type = Type.forKeys(items.keySet());
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
	
	public Type getExplicitType() {
		Type type = Type.forKeys(items.keySet());
		if (type == null) return null;
		int n = minParamCount(type);
		if (n == 0) return null;
		if (canImplyType(type, n)) return null;
		return type;
	}
	
	public List<KeyCapLegendItem> getItemList() {
		Type type = Type.forKeys(items.keySet());
		if (type == null) return null;
		int n = minParamCount(type);
		if (n == 0) return null;
		if (!canImplyType(type, n)) return null;
		List<KeyCapLegendItem> items = new ArrayList<KeyCapLegendItem>();
		for (String key : type.paramKeys) items.add(this.items.get(key));
		return items;
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
