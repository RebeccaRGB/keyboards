package com.kreative.keycaps;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyCapLegend {
	public static enum Type {
		F(1), // Function or modifier key
		G(2), // Dual-function or marked modifier key
		L(1), // Letter key
		S(2), // Shifted/symbol key
		A(2), // Letter key with letter alt
		T(3), // Letter key with shifted/symbol alt
		Z(3), // Shifted/symbol key with letter alt
		Q(4), // Shifted/symbol key with shifted/symbol alt
		N(2), // Numpad key with non-numlock function
		SF(2), // Space Cadet function key with front
		ST(3), // Space Cadet character key with top and front
		SQ(4), // Space Cadet character key with top and shifted/symbol front
		ZX(5), // ZX Spectrum key
		NONE(0); // No legend
		public final int paramCount;
		private Type(int paramCount) {
			this.paramCount = paramCount;
		}
	}
	
	private static final String QUOTED_TOKEN_STR = "\'((\\\\.|[^\'])*)\'|\"((\\\\.|[^\"])*)\"|`((\\\\.|[^`])*)`";
	private static final String CODED_TOKEN_STR = "[$]([0-9A-Fa-f]+([+][0-9A-Fa-f]+)*)?";
	private static final String STRING_TOKEN_STR = "([A-Za-z]+)|" + QUOTED_TOKEN_STR + "|" + CODED_TOKEN_STR;
	public static final String PATTERN_STRING = "\\[\\s*((" + STRING_TOKEN_STR + ")\\s*(,\\s*)?)*\\]|((" + STRING_TOKEN_STR + ")\\s*)*";
	private static final Pattern STRING_TOKEN = Pattern.compile(STRING_TOKEN_STR);
	private static final Pattern CODE_TOKEN = Pattern.compile("([$]|[0][Xx]|[Uu][+])?([0-9A-Fa-f]+)");
	
	public static final KeyCapLegend DEFAULT = new KeyCapLegend(Type.NONE, new String[0]);
	
	private final Type type;
	private final LegendItem[] items;
	
	public KeyCapLegend(String s) {
		Type type = null;
		ArrayList<LegendItem> items = new ArrayList<LegendItem>();
		Matcher m = STRING_TOKEN.matcher(s);
		while (m.find()) {
			if (m.group(1) != null && m.group(1).length() > 0) {
				if (type == null) {
					for (Type t : Type.values()) {
						if (t.toString().equalsIgnoreCase(m.group(1))) {
							type = t;
						}
					}
					if (type == null) {
						items.add(LegendItem.text(m.group(1)));
					}
				} else {
					items.add(LegendItem.text(m.group(1)));
				}
			} else if (m.group(2) != null && m.group(2).length() > 0) {
				items.add(LegendItem.text(unescape(m.group(2))));
			} else if (m.group(4) != null && m.group(4).length() > 0) {
				items.add(LegendItem.text(unescape(m.group(4))));
			} else if (m.group(6) != null && m.group(6).length() > 0) {
				items.add(LegendItem.path(unescape(m.group(6))));
			} else if (m.group(8) != null && m.group(8).length() > 0) {
				items.add(LegendItem.text(fromCodes(m.group(8))));
			} else {
				items.add(null);
			}
		}
		this.items = items.toArray(new LegendItem[items.size()]);
		if (type == null) {
			switch (items.size()) {
				case 4: type = Type.Q; break;
				case 3: type = Type.T; break;
				case 2: type = (isImpliedLetterOrSymbol(0) && isImpliedLetterOrSymbol(1)) ? Type.S : Type.G; break;
				case 1: type = isImpliedLetterOrSymbol(0) ? Type.L : Type.F; break;
				default: type = Type.NONE;
			}
		}
		this.type = type;
	}
	
	public KeyCapLegend(Type type, String[] text) {
		this.type = type;
		this.items = new LegendItem[text.length];
		for (int i = 0; i < text.length; i++) {
			this.items[i] = LegendItem.text(text[i]);
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public int getParamCount() {
		return type.paramCount;
	}
	
	public String getText(int i) {
		return (i < items.length && items[i] != null) ? items[i].getText() : null;
	}
	
	public String getPath(int i) {
		return (i < items.length && items[i] != null) ? items[i].getPath() : null;
	}
	
	public String toString() {
		int n = minParamCount();
		if (n == 0) return "";
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		boolean cit = canImplyType();
		if (!cit) sb.append(type);
		for (int i = 0; i < n; i++) {
			if (!cit || i > 0) sb.append(',');
			sb.append(formatParameter(i));
		}
		sb.append(']');
		return sb.toString();
	}
	
	public String toNormalizedString() {
		int n = type.paramCount;
		if (n == 0) return "[]";
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		sb.append(type);
		for (int i = 0; i < n; i++) {
			sb.append(',');
			sb.append(formatParameter(i));
		}
		sb.append(']');
		return sb.toString();
	}
	
	public String toMinimizedString() {
		int n = minParamCount();
		if (n == 0) return "";
		StringBuffer sb = new StringBuffer();
		boolean cit = canImplyType();
		if (!cit) sb.append('[');
		if (!cit) sb.append(type);
		for (int i = 0; i < n; i++) {
			sb.append(formatParameter(i));
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
	
	private int minParamCount() {
		int n = type.paramCount - 1;
		while (n >= 0 && (n >= items.length || items[n] == null)) n--;
		return n + 1;
	}
	
	private boolean canImplyType() {
		int n = minParamCount();
		return (
			(type == Type.L && n == 1 && isImpliedLetterOrSymbol(0)) ||
			(type == Type.F && n == 1 && !isImpliedLetterOrSymbol(0)) ||
			(type == Type.S && n == 2 && (isImpliedLetterOrSymbol(0) && isImpliedLetterOrSymbol(1))) ||
			(type == Type.G && n == 2 && !(isImpliedLetterOrSymbol(0) && isImpliedLetterOrSymbol(1))) ||
			(type == Type.T && n == 3) ||
			(type == Type.Q && n == 4)
		);
	}
	
	private boolean isImpliedLetterOrSymbol(int i) {
		return (i < items.length && items[i] != null && items[i].isImpliedLetterOrSymbol());
	}
	
	private String formatParameter(int i) {
		return (i < items.length && items[i] != null) ? items[i].toString() : "$";
	}
	
	private static String unescape(String s) {
		StringBuffer sb = new StringBuffer();
		boolean escaped = false;
		for (char ch : s.toCharArray()) {
			if (escaped) {
				escaped = false;
				switch (ch) {
					case 'a': ch = 0x0007; break;
					case 'b': ch = 0x0008; break;
					case 't': ch = 0x0009; break;
					case 'n': ch = 0x000A; break;
					case 'v': ch = 0x000B; break;
					case 'f': ch = 0x000C; break;
					case 'r': ch = 0x000D; break;
					case 'o': ch = 0x000E; break;
					case 'i': ch = 0x000F; break;
					case 'z': ch = 0x001A; break;
					case 'e': ch = 0x001B; break;
					case 'd': ch = 0x007F; break;
				}
				sb.append(ch);
			} else if (ch == '\\') {
				escaped = true;
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}
	
	private static String fromCodes(String s) {
		StringBuffer sb = new StringBuffer();
		Matcher m = CODE_TOKEN.matcher(s);
		while (m.find()) {
			int ch = Integer.parseInt(m.group(2), 16);
			sb.append(Character.toChars(ch));
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		for (String arg : args) {
			KeyCapLegend kcl = new KeyCapLegend(arg);
			System.out.println(arg);
			System.out.println(kcl.toString());
			System.out.println(kcl.toNormalizedString());
			System.out.println(kcl.toMinimizedString());
		}
	}
}
