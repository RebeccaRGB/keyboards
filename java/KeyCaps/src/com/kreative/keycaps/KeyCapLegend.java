package com.kreative.keycaps;

import java.util.ArrayList;
import java.util.Arrays;
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
	}
	
	public static KeyCapLegend parse(KeyCapParser p) {
		Type type = null;
		List<KeyCapLegendItem> items = new ArrayList<KeyCapLegendItem>();
		if (p.hasNextChar('[')) {
			p.next();
			if (p.hasNextID()) {
				type = Type.forName(p.next());
				if (p.hasNextChar(',')) p.next();
				else throw p.expected(",");
			}
			while (true) {
				if (p.hasNextChar(']')) { p.next(); break; }
				items.add(KeyCapLegendItem.parse(p));
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
		if (type == null) type = Type.forItems(items);
		KeyCapLegend legend = new KeyCapLegend();
		for (int i = 0, n = Math.min(items.size(), type.paramKeys.length); i < n; i++) {
			if (items.get(i) != null) legend.items.put(type.paramKeys[i], items.get(i));
		}
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
		for (Type type : Type.values()) {
			if (Arrays.asList(type.paramKeys).containsAll(items.keySet())) {
				return type;
			}
		}
		return null;
	}
	
	public String getText(int i) {
		Type type = getType();
		if (type == null) return null;
		KeyCapLegendItem item = items.get(type.paramKeys[i]);
		if (item == null) return null;
		return item.getText();
	}
	
	public String getPath(int i) {
		Type type = getType();
		if (type == null) return null;
		KeyCapLegendItem item = items.get(type.paramKeys[i]);
		if (item == null) return null;
		return item.getPath();
	}
	
	public String toString() {
		Type type = getType();
		if (type == null) return "";
		int n = minParamCount(type);
		if (n == 0) return "";
		boolean cit = canImplyType(type, n);
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		if (!cit) sb.append(type);
		for (int i = 0; i < n; i++) {
			if (!cit || i > 0) sb.append(',');
			sb.append(formatParameter(type, i));
		}
		sb.append(']');
		return sb.toString();
	}
	
	public String toNormalizedString() {
		Type type = getType();
		if (type == null) return "[]";
		int n = type.paramKeys.length;
		if (n == 0) return "[]";
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		sb.append(type);
		for (int i = 0; i < n; i++) {
			sb.append(',');
			sb.append(formatParameter(type, i));
		}
		sb.append(']');
		return sb.toString();
	}
	
	public String toMinimizedString() {
		Type type = getType();
		if (type == null) return "";
		int n = minParamCount(type);
		if (n == 0) return "";
		boolean cit = canImplyType(type, n);
		StringBuffer sb = new StringBuffer();
		if (!cit) sb.append('[');
		if (!cit) sb.append(type);
		for (int i = 0; i < n; i++) {
			sb.append(formatParameter(type, i));
		}
		if (!cit) sb.append(']');
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
}
