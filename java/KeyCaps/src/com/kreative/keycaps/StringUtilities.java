package com.kreative.keycaps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtilities {
	public static String quote(String s, char quote) {
		StringBuffer sb = new StringBuffer();
		sb.append(quote);
		for (char ch : s.toCharArray()) {
			if ((ch >= 0x20 && ch < 0x7F) || ch >= 0xA0) {
				if (ch == quote || ch == '\\') sb.append('\\');
				sb.append(ch);
			} else switch (ch) {
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
					sb.append("\\x");
					sb.append(Character.forDigit((ch >> 4) & 15, 16));
					sb.append(Character.forDigit(ch & 15, 16));
					break;
			}
		}
		sb.append(quote);
		return sb.toString();
	}
	
	public static String unescape(String s) {
		StringBuffer sb = new StringBuffer();
		int escapeState = 0;
		int n, d;
		for (char ch : s.toCharArray()) {
			if ((n = (escapeState >> 28) & 15) > 0) {
				escapeState &= 0x0FFFFFFF;
				if ((d = Character.digit(ch, 16)) >= 0) {
					escapeState <<= 4;
					escapeState |= d;
					if ((--n) > 0) {
						escapeState |= (n << 28);
						continue;
					}
					if (Character.isValidCodePoint(escapeState)) {
						sb.append(Character.toChars(escapeState));
					}
					escapeState = 0;
					continue;
				}
				if (Character.isValidCodePoint(escapeState)) {
					sb.append(Character.toChars(escapeState));
				}
				escapeState = 0;
				// fall through
			} else if ((n = (escapeState >> 24) & 15) > 0) {
				escapeState &= 0x00FFFFFF;
				if ((d = Character.digit(ch, 8)) >= 0) {
					escapeState <<= 3;
					escapeState |= d;
					if ((--n) > 0) {
						escapeState |= (n << 24);
						continue;
					}
					if (Character.isValidCodePoint(escapeState)) {
						sb.append(Character.toChars(escapeState));
					}
					escapeState = 0;
					continue;
				}
				if (Character.isValidCodePoint(escapeState)) {
					sb.append(Character.toChars(escapeState));
				}
				escapeState = 0;
				// fall through
			} else if (escapeState == 'c') {
				sb.append((char)(ch & 0x1F));
				escapeState = 0;
				continue;
			} else if (escapeState == '\\') {
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
					case 'c': escapeState = 'c'; continue;
					case 'x': escapeState = (2 << 28); continue;
					case 'u': escapeState = (4 << 28); continue;
					case 'w': escapeState = (6 << 28); continue;
					case 'U': escapeState = (8 << 28); continue;
					case '0': escapeState = ((2 << 24) | 0); continue;
					case '1': escapeState = ((2 << 24) | 1); continue;
					case '2': escapeState = ((2 << 24) | 2); continue;
					case '3': escapeState = ((2 << 24) | 3); continue;
				}
				sb.append(ch);
				escapeState = 0;
				continue;
			}
			if (ch == '\\') {
				escapeState = '\\';
			} else {
				sb.append(ch);
			}
		}
		if ((n = (escapeState >> 28) & 15) > 0) {
			escapeState &= 0x0FFFFFFF;
			if (Character.isValidCodePoint(escapeState)) {
				sb.append(Character.toChars(escapeState));
			}
		} else if ((n = (escapeState >> 24) & 15) > 0) {
			escapeState &= 0x00FFFFFF;
			if (Character.isValidCodePoint(escapeState)) {
				sb.append(Character.toChars(escapeState));
			}
		} else if (escapeState == 'c') {
			sb.append("\\c");
		} else if (escapeState == '\\') {
			sb.append("\\");
		}
		return sb.toString();
	}
	
	public static String toCodes(String s) {
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
	
	private static final Pattern CODE_TOKEN = Pattern.compile("([$]|[0][Xx]|[Uu][+])?([0-9A-Fa-f]+)");
	
	public static String fromCodes(String s) {
		StringBuffer sb = new StringBuffer();
		Matcher m = CODE_TOKEN.matcher(s);
		while (m.find()) {
			int ch = (int)Long.parseLong(m.group(2), 16);
			if (Character.isValidCodePoint(ch)) {
				sb.append(Character.toChars(ch));
			}
		}
		return sb.toString();
	}
	
	private StringUtilities() {}
}
