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
	private static final Pattern PATH_TAG = Pattern.compile("<path\\s+d\\s*=\\s*(\'[^\']*\'|\"[^\"]*\")\\s*/?>");
	private static final Pattern CODE_TOKEN = Pattern.compile("([$]|[0][Xx]|[Uu][+])?([0-9A-Fa-f]+)");
	
	public static final String TEXT_WINDOWS = "\uF810";
	public static final String TEXT_CONTEXT_MENU = "\uF811";
	public static final String TEXT_OPEN_APPLE = "\uF812";
	public static final String TEXT_SOLID_APPLE = "\uF813";
	public static final String TEXT_ATARI = "\uF820";
	public static final String TEXT_COMMODORE = "\uF821";
	public static final String TEXT_SOLID_AMIGA = "\uF822";
	public static final String TEXT_OPEN_AMIGA = "\uF823";
	public static final String TEXT_LEAP_BACKWARD = "\uF824";
	public static final String TEXT_LEAP_FORWARD = "\uF825";
	public static final String TEXT_GOOGLE_ASSISTANT = "\uF826";
	public static final String TEXT_RASPBERRY_PI = "\uF827";
	
	public static final String PATH_WINDOWS = "M 0 0 h 0.45 v 0.45 h -0.45 z M 1 0 v 0.45 h -0.45 v -0.45 z M 1 1 h -0.45 v -0.45 h 0.45 z M 0 1 v -0.45 h 0.45 v 0.45 z";
	public static final String PATH_CONTEXT_MENU = "M 0 0 V 1 H 1 V 0 Z M 0.9 0.9 H 0.1 V 0.1 H 0.9 Z M 0.7 0.35 H 0.3 V 0.25 H 0.7 Z M 0.7 0.55 H 0.3 V 0.45 H 0.7 Z M 0.7 0.75 H 0.3 V 0.65 H 0.7 Z";
	public static final String PATH_COMMODORE = "M 0.571 0.536 h 0.214 L 1 0.75 H 0.571 V 0.536 z M 0.571 0.464 V 0.25 H 1 L 0.786 0.464 H 0.571 z M 0 0.5 C 0 0.223 0.223 0 0.5 0 c 0.025 0 0.048 0.002 0.071 0.006 v 0.217 C 0.548 0.218 0.525 0.214 0.5 0.214 c -0.157 0 -0.286 0.128 -0.286 0.286 S 0.343 0.786 0.5 0.786 c 0.025 0 0.048 -0.004 0.071 -0.009 v 0.217 C 0.548 0.998 0.525 1 0.5 1 C 0.223 1 0 0.777 0 0.5 z";
	public static final String PATH_SOLID_AMIGA = "M 0.654 0.414 l -0.24 0.24 h 0.24 V 0.414 z M -0.039 1 V 0.923 h 0.077 L 0.961 0 v 0.923 h 0.078 V 1 H 0.577 V 0.923 h 0.077 V 0.73 H 0.336 L 0.144 0.923 H 0.23 V 1 H -0.039 z";
	public static final String PATH_OPEN_AMIGA = "M 0.885 0.185 L 0.731 0.338 v 0.585 h 0.154 V 0.185 z M 0.654 0.414 l -0.24 0.24 h 0.24 V 0.414 z M -0.039 1 V 0.923 h 0.077 L 0.961 0 v 0.923 h 0.078 V 1 H 0.577 V 0.923 h 0.077 V 0.73 H 0.336 L 0.144 0.923 h 0.087 V 1 H -0.039 z";
	
	public static final KeyCapLegend DEFAULT = new KeyCapLegend(Type.NONE, new String[0]);
	
	private final Type type;
	private final String[] text;
	private final String[] paths;
	
	public KeyCapLegend(String s) {
		Type type = null;
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<String> paths = new ArrayList<String>();
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
						text.add(m.group(1));
						paths.add(null);
					}
				} else {
					text.add(m.group(1));
					paths.add(null);
				}
			} else if (m.group(2) != null && m.group(2).length() > 0) {
				text.add(unescape(m.group(2)));
				paths.add(null);
			} else if (m.group(4) != null && m.group(4).length() > 0) {
				text.add(unescape(m.group(4)));
				paths.add(null);
			} else if (m.group(6) != null && m.group(6).length() > 0) {
				text.add(null);
				paths.add(unescape(m.group(6)));
			} else if (m.group(8) != null && m.group(8).length() > 0) {
				text.add(fromCodes(m.group(8)));
				paths.add(null);
			} else {
				text.add(null);
				paths.add(null);
			}
		}
		this.paths = paths.toArray(new String[paths.size()]);
		this.text = text.toArray(new String[text.size()]);
		if (type == null) {
			switch (text.size()) {
				case 4: type = Type.Q; break;
				case 3: type = Type.T; break;
				case 2: type = (isSingleCodePoint(0) && isSingleCodePoint(1)) ? Type.S : Type.G; break;
				case 1: type = isSingleCodePoint(0) ? Type.L : Type.F; break;
				default: type = Type.NONE;
			}
		}
		this.type = type;
	}
	
	public KeyCapLegend(Type type, String[] text) {
		this.type = type;
		this.text = new String[text.length];
		this.paths = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			if (text[i] != null && text[i].length() > 0) {
				Matcher m = PATH_TAG.matcher(text[i]);
				if (m.matches()) {
					String p = m.group(1);
					this.paths[i] = p.substring(1, p.length()-1);
				} else {
					this.text[i] = text[i];
				}
			}
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public int getParamCount() {
		return type.paramCount;
	}
	
	public String getRawText(int i) {
		return (i < text.length) ? text[i] : null;
	}
	
	public String getRawPath(int i) {
		return (i < paths.length) ? paths[i] : null;
	}
	
	public String getText(int i) {
		if (i < paths.length && paths[i] != null && paths[i].length() > 0) return null;
		if (i >= text.length || text[i] == null || text[i].length() == 0) return null;
		if (text[i].codePointCount(0, text[i].length()) != 1) return text[i];
		int ch = text[i].codePointAt(0);
		if (ch >= 0xF810 && ch <= 0xF813) return null;
		if (ch >= 0xF820 && ch <= 0xF827) return null;
		if (ch >= 0xFFCE0 && ch <= 0xFFCEF) return null;
		return text[i];
	}
	
	public String getPath(int i) {
		if (i < paths.length && paths[i] != null && paths[i].length() > 0) return paths[i];
		if (i >= text.length || text[i] == null || text[i].length() == 0) return null;
		if (text[i].codePointCount(0, text[i].length()) != 1) return null;
		switch (text[i].codePointAt(0)) {
			case 0xF810: case 0xFFCE0: return PATH_WINDOWS;
			case 0xF811: case 0xFFCE1: return PATH_CONTEXT_MENU;
			case 0xF821: case 0xFFCE5: return PATH_COMMODORE;
			case 0xF822: case 0xFFCE6: return PATH_SOLID_AMIGA;
			case 0xF823: case 0xFFCE7: return PATH_OPEN_AMIGA;
			default: return null;
		}
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
		while (
			n >= 0
			&& (n >= text.length || text[n] == null || text[n].length() == 0)
			&& (n >= paths.length || paths[n] == null || paths[n].length() == 0)
		) n--;
		return n + 1;
	}
	
	private boolean canImplyType() {
		int n = minParamCount();
		return (
			(type == Type.L && n == 1 && isSingleCodePoint(0)) ||
			(type == Type.F && n == 1 && !isSingleCodePoint(0)) ||
			(type == Type.S && n == 2 && (isSingleCodePoint(0) && isSingleCodePoint(1))) ||
			(type == Type.G && n == 2 && !(isSingleCodePoint(0) && isSingleCodePoint(1))) ||
			(type == Type.T && n == 3) ||
			(type == Type.Q && n == 4)
		);
	}
	
	private boolean isSingleCodePoint(int i) {
		if (i < paths.length && paths[i] != null && paths[i].length() > 0) return false;
		if (i >= text.length || text[i] == null || text[i].length() == 0) return false;
		if (text[i].codePointCount(0, text[i].length()) != 1) return false;
		int ch = text[i].codePointAt(0);
		if (ch >= 0xF810 && ch <= 0xF813) return false;
		if (ch >= 0xF820 && ch <= 0xF827) return false;
		if (ch >= 0xFFCE0 && ch <= 0xFFCEF) return false;
		return true;
	}
	
	private String formatParameter(int i) {
		return format(
			(i < text.length) ? text[i] : null,
			(i < paths.length) ? paths[i] : null
		);
	}
	
	private static String format(String text, String path) {
		if (path != null && path.length() > 0) return quote(path, '`');
		if (text == null || text.length() == 0) return "$";
		if (text.codePointCount(0, text.length()) == 1) {
			int ch = text.codePointAt(0);
			if (ch < 0x20 || ch >= 0x7F) {
				return toCodes(text);
			}
		}
		char q = text.contains("\"") ? '\'' : text.contains("\'") ? '\"' : '\'';
		return quote(text, q);
	}
	
	private static String quote(String s, char quote) {
		StringBuffer sb = new StringBuffer();
		sb.append(quote);
		for (char ch : s.toCharArray()) {
			switch (ch) {
				case 0x0007: sb.append("\\a"); break;
				case 0x0008: sb.append("\\b"); break;
				case 0x0009: sb.append("\\t"); break;
				case 0x000A: sb.append("\\n"); break;
				case 0x000B: sb.append("\\v"); break;
				case 0x000C: sb.append("\\f"); break;
				case 0x000D: sb.append("\\r"); break;
				case 0x000E: sb.append("\\o"); break;
				case 0x000F: sb.append("\\i"); break;
				case 0x001A: sb.append("\\z"); break;
				case 0x001B: sb.append("\\e"); break;
				case 0x007F: sb.append("\\d"); break;
				default:
					if (ch == quote || ch == '\\') sb.append('\\');
					sb.append(ch);
					break;
			}
		}
		sb.append(quote);
		return sb.toString();
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
	
	private static String toCodes(String s) {
		StringBuffer sb = new StringBuffer("$");
		int i = 0, n = s.length();
		while (i < n) {
			if (i > 0) sb.append('+');
			int ch = s.codePointAt(i);
			String h = Integer.toHexString(ch);
			for (int j = h.length(); j < 4; j++) sb.append('0');
			sb.append(h.toUpperCase());
			i += Character.charCount(ch);
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
